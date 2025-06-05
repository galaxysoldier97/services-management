package mc.monacotelecom.services.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.services.assembler.ActivationCodeResourceAssembler;
import mc.monacotelecom.services.assembler.ProvisioningTagResourceAssembler;
import mc.monacotelecom.services.assembler.ServiceActivationResourceAssembler;
import mc.monacotelecom.services.dto.ChangeTagsResponse;
import mc.monacotelecom.services.dto.ProvisioningTagDTO;
import mc.monacotelecom.services.dto.ServiceActivationDTO;
import mc.monacotelecom.services.dto.TagActionDTO;
import mc.monacotelecom.services.dto.request.ChangeTagsDTO;
import mc.monacotelecom.services.entity.*;
import mc.monacotelecom.services.enums.TagAction;
import mc.monacotelecom.services.exceptions.SvcNotFoundException;
import mc.monacotelecom.services.exceptions.SvcValidationException;
import mc.monacotelecom.services.repository.ProvisioningTagRepository;
import mc.monacotelecom.services.repository.ServiceActivationRepository;
import mc.monacotelecom.services.repository.ServiceRepository;
import mc.monacotelecom.services.repository.ServiceTagRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static mc.monacotelecom.services.enums.ActivationNature.BOOSTER;
import static mc.monacotelecom.services.enums.ActivationNature.BUCKET;
import static mc.monacotelecom.services.enums.ServiceCategory.ACCESS;
import static mc.monacotelecom.services.enums.ServiceCategory.COMPONENT;
import static mc.monacotelecom.services.translation.TranslationMessages.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChangeTagsProcess {
    private final ServiceRepository<Service> serviceRepository;
    private final ProvisioningTagRepository provisioningTagRepository;
    private final ServiceActivationRepository serviceActivationRepository;
    private final ServiceTagRepository serviceTagRepository;
    private final ServiceActivationResourceAssembler serviceActivationResourceAssembler = ServiceActivationResourceAssembler.of(ServiceProcess.class);
    private final ActivationCodeResourceAssembler activationCodeResourceAssembler = ActivationCodeResourceAssembler.of(ServiceProcess.class);
    private final ProvisioningTagResourceAssembler provisioningTagResourceAssembler = ProvisioningTagResourceAssembler.of(ServiceProcess.class);
    private final LocalizedMessageBuilder localizedMessageBuilder;
    public static final String GODET_HORS_FORFAIT_ACTIVCODE = "HOG_HF";

    public ChangeTagsResponse changeTags(Long serviceId, ChangeTagsDTO changeTags) {
        var service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_NOT_FOUND_ID, serviceId));

        var response = new ChangeTagsResponse();

        // 1. group by action, get only distinct provisioning tags
        Map<TagAction, Set<String>> actionTags = changeTags.getChangeTags().stream()
                .collect(Collectors.groupingBy(TagActionDTO::getTagAction, Collectors.mapping(TagActionDTO::getTagCode, Collectors.toSet())));

        // 2. get the union of those lists
        Set<String> union = actionTags.getOrDefault(TagAction.ADD, new HashSet<>()).stream()
                .filter(tag -> actionTags.getOrDefault(TagAction.REMOVE, new HashSet<>()).contains(tag))
                .collect(Collectors.toSet());
        actionTags.values().forEach(set -> set.removeAll(union));

        // 3. Check if all provisioning tags exists and are compliant with the current service
        Map<String, ProvisioningTag> provisioningTags = provisioningTagRepository.findByTagCodeIn(actionTags.values().stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet())).stream()
                .collect(Collectors.toMap(ProvisioningTag::getTagCode, Function.identity()));

        if (provisioningTags.size() == 0) {
            return response;
        }

        validateRequestAgainstService(service, actionTags, provisioningTags);

        // retrieve from database service activation
        Map<String, ServiceActivation> serviceActivations =
                serviceActivationRepository.findByService(service).stream()
                        .collect(Collectors.toMap(ServiceActivation::getActivCode, Function.identity()));

        // Map service tags ID to accelerate controller
        Set<ProvisioningTag> refined = provisioningTags.values().stream()
                .filter(provisioningTag -> provisioningTag.getPersistent() != null && provisioningTag.getPersistent())
                .collect(Collectors.toSet());

        Map<String, ServiceTag> currentServiceTags = !refined.isEmpty() ?
                serviceTagRepository.findAllByServiceAndTags(service, provisioningTags.values())
                        .stream().collect(Collectors.toMap(ServiceTag::getTagCode, Function.identity()))
                : new HashMap<>();

        // Backup service activation value
        Map<String, Long> serviceActivationValues = new HashMap<>();
        serviceActivations.forEach((k, v) -> serviceActivationValues.put(k, v.getActivValue()));

        // Create new service activation
        provisioningTags.values().forEach(provisioningTag ->
                provisioningTag.getTagActivations().forEach(tagActivation ->
                        serviceActivations.putIfAbsent(tagActivation.getActivationCode().getActivCode(),
                                new ServiceActivation(0L, tagActivation.getActivationCode().getActivCode(), serviceId, tagActivation.getActivationCode(), service))));

        Map<String, Set<TagActivation>> tagActivations = provisioningTags.values().stream()
                .collect(Collectors.toMap(ProvisioningTag::getTagCode, ProvisioningTag::getTagActivations));

        Map<Boolean, Set<TagActivation>> persistentTagActivations = provisioningTags.values().stream()
                .map(ProvisioningTag::getTagActivations)
                .flatMap(Collection::stream)
                .collect(Collectors.partitioningBy(tagActivation -> tagActivation.getProvisioningTag().getPersistent(), Collectors.toSet()));

        Map<Boolean, Set<TagActivation>> quotaTagActivations = persistentTagActivations.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.partitioningBy(
                        o -> Arrays.asList(BOOSTER, BUCKET).contains(o.getActivationCode().getNature()),
                        Collectors.toSet()));

        // Process new values of service activation
        actionTags.getOrDefault(TagAction.ADD, new HashSet<>()).stream()
                .filter(provisioningTag -> !currentServiceTags.containsKey(provisioningTag))
                .map(tagActivations::get)
                .flatMap(Collection::stream)
                // TRP-183 : additional filter
                .filter(tagActivation -> persistentTagActivations.getOrDefault(true, new HashSet<>()).contains(tagActivation)
                        && quotaTagActivations.getOrDefault(true, new HashSet<>()).contains(tagActivation))
                .forEach(tagActivation -> {
                    Long v = serviceActivations.get(tagActivation.getActivationCode().getActivCode()).getActivValue();
                    if (tagActivation.getTagValue() != null) {
                        v += tagActivation.getTagValue();
                    } else if (quotaTagActivations.getOrDefault(true, new HashSet<>()).contains(tagActivation)) {
                        v++;
                    }
                    serviceActivations.get(tagActivation.getActivationCode().getActivCode()).setActivValue(v);
                });

        actionTags.getOrDefault(TagAction.ADD, new HashSet<>()).stream()
                .filter(provisioningTag -> !currentServiceTags.containsKey(provisioningTag))
                .map(tagActivations::get)
                .flatMap(Collection::stream)
                .filter(tagActivation -> persistentTagActivations.getOrDefault(true, new HashSet<>()).contains(tagActivation)
                        && quotaTagActivations.getOrDefault(false, new HashSet<>()).contains(tagActivation)
                        // We don't consider the activation codes already present on the service, they should not be present in the response
                        && !serviceActivationValues.containsKey(tagActivation.getActivationCode().getActivCode()))
                .forEach(tagActivation -> serviceActivations.get(tagActivation.getActivationCode().getActivCode()).setActivValue(1L));

        actionTags.getOrDefault(TagAction.REMOVE, new HashSet<>()).stream()
                .filter(currentServiceTags::containsKey)
                .map(tagActivations::get)
                .flatMap(Collection::stream)
                // TRP-183 : additional filter
                .filter(tagActivation -> persistentTagActivations.getOrDefault(true, new HashSet<>()).contains(tagActivation) && quotaTagActivations.getOrDefault(true, new HashSet<>()).contains(tagActivation))
                .forEach(tagActivation -> {
                    ServiceActivation serviceActivation = serviceActivations.get(tagActivation.getActivationCode().getActivCode());
                    serviceActivation.setActivValue(Optional.ofNullable(serviceActivation.getActivValue()).orElse(0L) - Optional.ofNullable(tagActivation.getTagValue()).orElse(0L));
                });

        // When removing a service tag, we want to keep the service activations that are shared with other remaining service tags
        // To manage this, we retrieve all service activations that are related to remaining service tags, and we prevent them from being deleted
        List<ActivationCode> remainingCodes = service.getServiceTags()
                .stream()
                .filter(serviceTag -> !actionTags.getOrDefault(TagAction.REMOVE, new HashSet<>()).contains(serviceTag.getTagCode()))
                .map(ServiceTag::getProvisioningTag)
                .map(ProvisioningTag::getTagActivations)
                .flatMap(Collection::stream)
                .map(TagActivation::getActivationCode)
                .collect(Collectors.toList());

        actionTags.getOrDefault(TagAction.REMOVE, new HashSet<>()).stream()
                .filter(currentServiceTags::containsKey)
                .map(tagActivations::get)
                .flatMap(Collection::stream)
                .filter(tagActivation -> persistentTagActivations.getOrDefault(true, new HashSet<>()).contains(tagActivation) && quotaTagActivations.getOrDefault(false, new HashSet<>()).contains(tagActivation))
                .map(TagActivation::getActivationCode)
                .filter(activationCode -> !remainingCodes.contains(activationCode))
                .forEach(activationCode -> serviceActivations.get(activationCode.getActivCode()).setActivValue(-1L));

        // save all the things
        // true -> save / update / false -> delete
        Optional.ofNullable(actionTags.get(TagAction.REMOVE))
                .map(set -> set.stream().filter(pt -> provisioningTags.get(pt).getPersistent() != null && provisioningTags.get(pt).getPersistent()).collect(Collectors.toSet()))
                .filter(set -> !set.isEmpty())
                .ifPresent(set ->
                        serviceTagRepository.deleteAllByServiceAndTags(service, set.stream().map(provisioningTags::get).collect(Collectors.toSet())));

        Set<ServiceTag> s = Optional.ofNullable(actionTags.get(TagAction.ADD))
                .filter(set -> !set.isEmpty()).stream().flatMap(Collection::stream)
                .filter(tag -> provisioningTags.get(tag).getPersistent())
                // TagValue might not be set as null here
                .map(tag -> new ServiceTag(null, provisioningTags.get(tag).getTagCode(), service.getServiceId(), provisioningTags.get(tag), service))
                .collect(Collectors.toSet());
        if (!s.isEmpty()) {
            serviceTagRepository.saveAll(s);
        }

        // TRP-183 : Delete service activation
        Set<ServiceActivation> serviceActivationsToDelete = serviceActivations.values().stream()
                .filter(serviceActivation -> persistentTagActivations.getOrDefault(true, new HashSet<>()).stream().map(TagActivation::getActivationCode).collect(Collectors.toSet()).contains(serviceActivation.getActivationCode()) && serviceActivation.getActivValue() != null && serviceActivation.getActivValue() < 1L)
                .collect(Collectors.toSet());
        if (!serviceActivationsToDelete.isEmpty()) {
            service.getServiceActivations().removeAll(serviceActivationsToDelete);
            serviceActivationRepository.deleteAll(serviceActivationsToDelete);
        }

        // TRP-183 : Save service activation with quota (BUCKET)
        Set<ActivationCode> quotaActivationCode = quotaTagActivations.getOrDefault(true, new HashSet<>())
                .stream().map(TagActivation::getActivationCode)
                .collect(Collectors.toSet());
        List<ServiceActivation> persistentServiceActivation = persistentTagActivations.getOrDefault(true, new HashSet<>()).stream()
                .map(TagActivation::getActivationCode)
                .map(ActivationCode::getActivCode)
                .map(serviceActivations::get)
                .filter(serviceActivation -> serviceActivation.getActivValue() != null && serviceActivation.getActivValue() > 0L)
                .collect(Collectors.toList());
        Optional.of(persistentServiceActivation)
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    list.replaceAll(serviceActivation -> {
                        if (!quotaActivationCode.contains(serviceActivation.getActivationCode()) || GODET_HORS_FORFAIT_ACTIVCODE.equals(serviceActivation.getActivCode())) {
                            serviceActivation.setActivValue(null);
                        }
                        return serviceActivation;
                    });
                    return list;
                })
                .ifPresent(serviceActivationRepository::saveAll);

        // Aggregate BOOSTER value
        persistentTagActivations.getOrDefault(false, new HashSet<>())
                .forEach(tagActivation -> {
                    Long v = serviceActivations.get(tagActivation.getActivationCode().getActivCode()).getActivValue();
                    if (actionTags.getOrDefault(TagAction.REMOVE, new HashSet<>()).contains(tagActivation.getProvisioningTag().getTagCode())) {
                        if (tagActivation.getTagValue() != null) {
                            serviceActivations.get(tagActivation.getActivationCode().getActivCode()).setActivValue(Math.max(v - tagActivation.getTagValue(), 0L));
                        } else {
                            serviceActivations.get(tagActivation.getActivationCode().getActivCode()).setActivValue(0L);
                        }
                    } else {
                        if (tagActivation.getTagValue() != null) {
                            serviceActivations.get(tagActivation.getActivationCode().getActivCode()).setActivValue(Math.max(v + tagActivation.getTagValue(), 0L));
                        } else {
                            serviceActivations.get(tagActivation.getActivationCode().getActivCode()).setActivValue(1L);
                        }
                    }
                });

        persistentServiceActivation.forEach(serviceActivation -> {
            if (serviceActivation.getActivValue() == null) {
                serviceActivation.setActivValue(1L);
            }
        });

        // fill the response
        response.getServiceActivations().addAll(serviceActivations.values().stream()
                .filter(sa ->
                        // Comparison with != instead of !.equals() is needed here, somehow
                        !serviceActivationValues.containsKey(sa.getActivCode()) || serviceActivationValues.get(sa.getActivCode()) != sa.getActivValue()
                )
                .map(sa -> {
                    ServiceActivationDTO serviceActivationDTO = serviceActivationResourceAssembler.toModel(sa);
                    serviceActivationDTO.setActivCode(activationCodeResourceAssembler.toModel(sa.getActivationCode()));
                    // Case HOG_HF : "Hors-Forfait" has null value, required for POM
                    Optional.of(serviceActivationDTO)
                            .filter(sa1 -> BUCKET.equals(sa1.getActivCode().getNature()) && GODET_HORS_FORFAIT_ACTIVCODE.equals(sa1.getActivCode().getCode()))
                            .ifPresent(sa1 -> sa1.setActivValue(null));
                    return serviceActivationDTO;
                })
                .sorted(Comparator.comparing(sa -> sa.getActivCode().getCode()))
                .collect(Collectors.toList()));

        response.getServiceTags().addAll(actionTags.getOrDefault(TagAction.ADD, new HashSet<>()).stream()
                .map(provisioningTags::get)
                .map(provisioningTagResourceAssembler::toModel)
                .sorted(Comparator.comparing(ProvisioningTagDTO::getTagCode))
                .collect(Collectors.toList()));

        return response;
    }

    /**
     * Validate that the request contents are consistent with the service characteristics
     */
    private void validateRequestAgainstService(final Service service, final Map<TagAction, Set<String>> tagsCodesByAction, final Map<String, ProvisioningTag> provisioningTags) {
        validateServiceTagsAndActivations(service);
        validateTagsExistence(provisioningTags.values(), tagsCodesByAction);
        if (ACCESS.equals(service.getServiceCategory())) {
            validateTagsAgainstServiceAccess(provisioningTags.values(), service);
        } else if (COMPONENT.equals(service.getServiceCategory())) {
            validateTagsAgainstServiceComponent(provisioningTags.values(), service);
        }
    }

    /**
     * Validation function to check that all activation codes and provisioning tags referred to by the service do exist in the database
     */
    private void validateServiceTagsAndActivations(Service service) {

        var missingTags = serviceRepository.findServicesWithNonExistentProvisioningTagByService(service.getServiceId())
                .stream()
                .flatMap(Stream::ofNullable)
                .collect(Collectors.toList());
        var missingCodes = serviceRepository.findServicesWithNonExistentActivationCodeByService(service.getServiceId())
                .stream()
                .flatMap(Stream::ofNullable)
                .collect(Collectors.toList());

        if (!missingTags.isEmpty() || !missingCodes.isEmpty()) {
            throw new SvcValidationException(localizedMessageBuilder, SERVICE_HAS_MISSING_TAGS_CODES, String.valueOf(service.getServiceId()), missingTags, missingCodes);
        }
    }

    /**
     * Validate that all requested tags exist in the database
     */
    private void validateTagsExistence(final Collection<ProvisioningTag> existingTags, final Map<TagAction, Set<String>> requestedTags) {
        Collection<String> requestedTagCodes = requestedTags.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
        Collection<String> existingTagCodes = existingTags.stream().map(ProvisioningTag::getTagCode).collect(Collectors.toSet());

        var inconsistentTags = CollectionUtils.subtract(requestedTagCodes, existingTagCodes);
        if (!inconsistentTags.isEmpty()) {
            throw new SvcValidationException(localizedMessageBuilder, CHANGE_TAGS_INCONSISTENCY_MISSING_TAG, inconsistentTags);
        }
    }

    /**
     * Validate that requested provisioning tags are consistent with a given service access
     */
    private void validateTagsAgainstServiceAccess(final Collection<ProvisioningTag> provisioningTags, final Service service) {
        validateService(provisioningTags, service);

        provisioningTags.stream()
                .filter(provisioningTag ->
                        !(ServiceAccess.class.isAssignableFrom(service.getClass()) && provisioningTag.getAccessType().equals(((ServiceAccess) service).getAccessType())))
                .findFirst()
                .ifPresent(inconsistentProvisioningTag -> {
                            assert service instanceof ServiceAccess;
                            throw new SvcValidationException(localizedMessageBuilder, CHANGE_TAGS_INCONSISTENCY_ACCESS_TYPE,
                                    inconsistentProvisioningTag.getTagCode(),
                                    inconsistentProvisioningTag.getAccessType(),
                                    ((ServiceAccess) service).getAccessType());
                        }
                );
    }

    /**
     * Validate that requested provisioning tags are consistent with a given service component
     */
    private void validateTagsAgainstServiceComponent(final Collection<ProvisioningTag> provisioningTags, final Service service) {
        validateService(provisioningTags, service);
    }

    private void validateService(final Collection<ProvisioningTag> provisioningTags, final Service service) {
        provisioningTags.stream()
                .filter(provisioningTag ->
                        !service.getServiceActivity().name().equals(provisioningTag.getActivity().name()))
                .findFirst()
                .ifPresent(inconsistentProvisioningTag -> {
                            throw new SvcValidationException(localizedMessageBuilder, CHANGE_TAGS_INCONSISTENCY_ACTIVITY,
                                    inconsistentProvisioningTag.getTagCode(),
                                    inconsistentProvisioningTag.getActivity(),
                                    service.getServiceActivity());
                        }
                );

        provisioningTags.stream()
                .filter(provisioningTag ->
                        !service.getServiceCategory().equals(provisioningTag.getCategory()))
                .findFirst()
                .ifPresent(inconsistentProvisioningTag -> {
                            throw new SvcValidationException(localizedMessageBuilder, CHANGE_TAGS_INCONSISTENCY_CATEGORY,
                                    inconsistentProvisioningTag.getTagCode(),
                                    inconsistentProvisioningTag.getCategory(),
                                    service.getServiceCategory());
                        }
                );
    }
}
