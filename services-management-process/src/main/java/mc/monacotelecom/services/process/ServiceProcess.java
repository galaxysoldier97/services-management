package mc.monacotelecom.services.process;


import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.inventory.common.CommonFunctions;
import mc.monacotelecom.inventory.common.Lambdas;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.inventory.common.sm.StatusChanger;
import mc.monacotelecom.services.assembler.ServiceAccessResourceAssembler;
import mc.monacotelecom.services.assembler.ServiceComponentResourceAssembler;
import mc.monacotelecom.services.assembler.ServicesResourceAssembler;
import mc.monacotelecom.services.assembler.revision.RevisionServiceAccessAssembler;
import mc.monacotelecom.services.assembler.revision.RevisionServiceComponentAssembler;
import mc.monacotelecom.services.dto.*;
import mc.monacotelecom.services.dto.request.AddServiceRequestDTO;
import mc.monacotelecom.services.dto.request.ServiceActionRequestDTO;
import mc.monacotelecom.services.dto.request.UpdateServiceDTO;
import mc.monacotelecom.services.dto.search.BaseSearchServiceDTO;
import mc.monacotelecom.services.dto.search.SearchServiceAccessDTO;
import mc.monacotelecom.services.dto.search.SearchServiceComponentDTO;
import mc.monacotelecom.services.dto.search.SearchServiceDTO;
import mc.monacotelecom.services.entity.Service;
import mc.monacotelecom.services.entity.ServiceAccess;
import mc.monacotelecom.services.entity.ServiceComponent;
import mc.monacotelecom.services.entity.projection.ServiceAccessUnmProjection;
import mc.monacotelecom.services.enums.*;
import mc.monacotelecom.services.exceptions.SvcConflictException;
import mc.monacotelecom.services.exceptions.SvcNotFoundException;
import mc.monacotelecom.services.exceptions.SvcValidationException;
import mc.monacotelecom.services.mapper.MapSearchServices;
import mc.monacotelecom.services.mapper.ServiceAccessMapper;
import mc.monacotelecom.services.mapper.ServiceComponentMapper;
import mc.monacotelecom.services.repository.*;
import mc.monacotelecom.services.repository.specification.ServiceAccessSpecification;
import mc.monacotelecom.services.repository.specification.ServiceComponentSpecification;
import mc.monacotelecom.services.repository.specification.ServiceSpecification;
import mc.monacotelecom.services.utils.TechIdProcessor;
import mc.monacotelecom.services.utils.UnmSynchronizer;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static mc.monacotelecom.services.enums.Network.FREEDHOME;
import static mc.monacotelecom.services.enums.ServiceActivity.MOBILE;
import static mc.monacotelecom.services.enums.ServiceCategory.ACCESS;
import static mc.monacotelecom.services.enums.ServiceCategory.COMPONENT;
import static mc.monacotelecom.services.translation.TranslationMessages.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
@Slf4j
public class ServiceProcess {

    private final ServiceAccessRepository serviceAccessRepository;
    private final ServiceComponentRepository serviceComponentRepository;
    private final ServiceRepository<Service> serviceRepository;
    private final ServiceAccessResourceAssembler serviceAccessResourceAssembler = ServiceAccessResourceAssembler.of(ServiceProcess.class);
    private final ServiceComponentResourceAssembler serviceComponentResourceAssembler = ServiceComponentResourceAssembler.of(ServiceProcess.class);
    private final ServicesResourceAssembler servicesResourceAssembler = ServicesResourceAssembler.of(ServiceProcess.class);
    private final StatusChanger<Status, Event> serviceStatusChanger;
    private final RevisionServiceComponentAssembler revisionServiceComponentAssembler;
    private final RevisionServiceAccessAssembler revisionServiceAccessAssembler;
    private final ServiceTagRepository serviceTagRepository;
    private final ServiceActivationRepository serviceActivationRepository;
    private final TechIdProcessor techIdProcessor;
    private final UnmSynchronizer unmSynchronizer;
    private final Clock clock;
    private final LocalizedMessageBuilder localizedMessageBuilder;

    public ServiceProcess(final ServiceTagRepository serviceTagRepository,
                          final ServiceActivationRepository serviceActivationRepository,
                          final StatusChanger<Status, Event> serviceStatusChanger,
                          final ServiceAccessRepository serviceAccessRepository,
                          final UnmSynchronizer unmSynchronizer,
                          final ServiceComponentRepository serviceComponentRepository,
                          final ServiceRepository<Service> serviceRepository,
                          final TechIdProcessor techIdProcessor,
                          final Clock clock,
                          final ZoneId zoneId,
                          final LocalizedMessageBuilder localizedMessageBuilder) {
        this.serviceAccessRepository = serviceAccessRepository;
        this.serviceComponentRepository = serviceComponentRepository;
        this.serviceRepository = serviceRepository;
        this.serviceTagRepository = serviceTagRepository;
        this.serviceActivationRepository = serviceActivationRepository;
        this.serviceStatusChanger = serviceStatusChanger;
        this.unmSynchronizer = unmSynchronizer;
        this.localizedMessageBuilder = localizedMessageBuilder;
        this.revisionServiceComponentAssembler = RevisionServiceComponentAssembler.of(ServiceProcess.class, zoneId);
        this.revisionServiceAccessAssembler = RevisionServiceAccessAssembler.of(ServiceProcess.class, zoneId);
        this.clock = clock;
        this.techIdProcessor = techIdProcessor;
    }

    public synchronized ServiceDTO addServiceOnSubscription(AddServiceRequestDTO addServiceRequest) {
        //create service (access or component)
        Service toCreateService;
        Long subscriptionId = addServiceRequest.getSubscriptionId();
        switch (addServiceRequest.getServiceCategory()) {
            case ACCESS:
                if (subscriptionId != null) {
                    Optional<ServiceAccess> optionalService = serviceAccessRepository.findBySubscriptionIdAndServiceCategoryAndServiceActivityAndAccessType(addServiceRequest.getSubscriptionId(), addServiceRequest.getServiceCategory(), addServiceRequest.getServiceActivity(), addServiceRequest.getAccessType());
                    if (optionalService.isPresent()) {
                        throw new SvcConflictException(localizedMessageBuilder, SERVICE_ALREADY_EXIST_SUBSCRIPTION_ID_CATEGORY_ACTIVITY_ACCESS_TYPE,
                                addServiceRequest.getSubscriptionId(),
                                addServiceRequest.getServiceCategory(),
                                addServiceRequest.getServiceActivity(),
                                addServiceRequest.getAccessType());
                    }
                }
                toCreateService = this.createServiceAccess(addServiceRequest.getAccessType(), addServiceRequest.getServiceActivity());
                break;
            case COMPONENT:
                if (subscriptionId != null) {
                    Optional<ServiceComponent> optionalService = serviceComponentRepository.findBySubscriptionIdAndServiceCategoryAndServiceActivityAndComponentType(addServiceRequest.getSubscriptionId(), addServiceRequest.getServiceCategory(), addServiceRequest.getServiceActivity(), addServiceRequest.getComponentType());
                    if (optionalService.isPresent()) {
                        throw new SvcConflictException(localizedMessageBuilder, SERVICE_ALREADY_EXIST_SUBSCRIPTION_ID_CATEGORY_ACTIVITY_ACCESS_TYPE,
                                addServiceRequest.getSubscriptionId(),
                                addServiceRequest.getServiceCategory(),
                                addServiceRequest.getServiceActivity(),
                                addServiceRequest.getComponentType());
                    }
                }
                toCreateService = this.createServiceComponent(addServiceRequest.getComponentType());
                break;
            default:
                throw new SvcValidationException(localizedMessageBuilder, SERVICE_INVALID_CATEGORY, addServiceRequest.getServiceCategory());
        }

        toCreateService.setStatus(Status.PENDING);
        toCreateService.setServiceActivity(addServiceRequest.getServiceActivity());
        toCreateService.setServiceCategory(addServiceRequest.getServiceCategory());

        if (addServiceRequest.getSubscriptionId() != null) {
            toCreateService = this.linkServiceToSubscription(toCreateService, subscriptionId);
        }

        this.checkBeforeSave(toCreateService);

        var savedService = serviceRepository.save(toCreateService);

        if (savedService.getServiceCategory().equals(ACCESS)) {
            unmSynchronizer.synchronizeUnmService((ServiceAccess) savedService);
            return serviceAccessResourceAssembler.toModel((ServiceAccess) toCreateService);
        } else {
            return serviceComponentResourceAssembler.toModel((ServiceComponent) toCreateService);
        }
    }

    public synchronized ServiceDTO addServiceAndActivate(AddServiceRequestDTO addServiceRequest) {
        ServiceDTO createdService = addServiceOnSubscription(addServiceRequest);
        Service service = serviceRepository.findById(createdService.getServiceId())
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_NOT_FOUND_ID, createdService.getServiceId()));
        setEventOnService(service, Event.activate);
        serviceRepository.save(service);
        if (service.getServiceCategory().equals(ACCESS)) {
            return serviceAccessResourceAssembler.toModel((ServiceAccess) service);
        } else {
            return serviceComponentResourceAssembler.toModel((ServiceComponent) service);
        }
    }

    private void checkBeforeSave(Service service) {

        if (service instanceof ServiceAccess) {
            var serviceAccess = (ServiceAccess) service;
            if (serviceAccess.getServiceActivity().equals(MOBILE) && serviceAccess.getAccessType().equals(FREEDHOME)) {
                String techId = serviceAccess.getTechId();
                while (serviceAccessRepository.existsByTechId(techId)) {
                    techId = techIdProcessor.nextTechId(techId, serviceAccess.getAccessType());
                }

                serviceAccess.setTechId(techId);
            }
        }
    }

    public ServiceAccess createServiceAccess(Network accessType, ServiceActivity serviceActivity) {
        var serviceAccess = new ServiceAccess();
        if (accessType == null) {
            throw new SvcValidationException(localizedMessageBuilder, ACCESS_TYPE_NULL);
        }
        serviceAccess.setAccessType(accessType);
        serviceAccess.setServiceActivity(serviceActivity);
        techIdProcessor.getAtCreationTime(serviceAccess).ifPresent(serviceAccess::setTechId);
        unmSynchronizer.synchronizeUnmService(serviceAccess);
        return serviceAccess;
    }

    private Service createServiceComponent(String componentType) {
        var serviceComponent = new ServiceComponent();
        serviceComponent.setComponentType(componentType);
        return serviceComponent;
    }

    public Service linkServiceToSubscription(Service service, Long subscriptionId) {
        service.setSubscriptionId(subscriptionId);
        return service;
    }

    public List<ServiceAccessUnmProjection> getServicesWithCrmIdIn(List<String> crmServiceIds) {
        return serviceAccessRepository.findByCrmServiceIdIn(crmServiceIds);
    }

    public ServiceDTO getById(Long serviceId) {
        var service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_NOT_FOUND_ID, serviceId));
        List<Event> serviceTransitions = new ArrayList<>(serviceStatusChanger.getAvailableEventsWithId(String.valueOf(service.getServiceId()), service.getStatus()));

        if (ACCESS.equals(service.getServiceCategory())) {
            var serviceAccess = serviceAccessRepository.findById(serviceId)
                    .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_ACCESS_NOT_FOUND_ID, serviceId));
            var serviceAccessDTO = serviceAccessResourceAssembler.toModel(serviceAccess);
            serviceAccessDTO.setServiceTransitions(serviceTransitions);
            return serviceAccessDTO;
        } else if (ServiceCategory.COMPONENT.equals(service.getServiceCategory())) {
            var serviceComponent = serviceComponentRepository.findById(serviceId)
                    .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_COMPONENT_NOT_FOUND_ID, serviceId));
            var serviceComponentDTO = serviceComponentResourceAssembler.toModel(serviceComponent);
            serviceComponentDTO.setServiceTransitions(serviceTransitions);
            return serviceComponentDTO;
        } else {
            throw new SvcValidationException(localizedMessageBuilder, SERVICE_INVALID_CATEGORY, service.getServiceCategory());
        }
    }

    public ServiceDTO getByOntId(final String ontId) {
        var serviceAccess = serviceAccessRepository.findByOntId(ontId)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_ACCESS_NOT_FOUND_ONT_ID, ontId));

        List<Event> serviceTransitions = new ArrayList<>(serviceStatusChanger.getAvailableEventsWithId(String.valueOf(serviceAccess.getServiceId()), serviceAccess.getStatus()));
        var serviceAccessDTO = serviceAccessResourceAssembler.toModel(serviceAccess);
        serviceAccessDTO.setServiceTransitions(serviceTransitions);

        return serviceAccessDTO;
    }

    public ServiceDTO update(Long serviceId, UpdateServiceDTO updateServiceRequest) {
        var service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new SvcValidationException(localizedMessageBuilder, SERVICE_NOT_FOUND_ID, serviceId));

        if (ACCESS.equals(service.getServiceCategory())) {
            var serviceAccess = serviceAccessRepository.findById(serviceId)
                    .orElseThrow(() -> new SvcValidationException(localizedMessageBuilder, SERVICE_ACCESS_NOT_FOUND_ID, serviceId));
            this.updateService(updateServiceRequest, serviceAccess);
            var savedService = serviceAccessRepository.save(serviceAccess);
            unmSynchronizer.synchronizeUnmService(savedService);
            return serviceAccessResourceAssembler.toModel(serviceAccess);
        } else {
            var serviceComponent = serviceComponentRepository.findById(serviceId)
                    .orElseThrow(() -> new SvcValidationException(localizedMessageBuilder, SERVICE_COMPONENT_NOT_FOUND_ID, serviceId));
            this.updateService(updateServiceRequest, serviceComponent);
            serviceComponentRepository.save(serviceComponent);
            return serviceComponentResourceAssembler.toModel(serviceComponent);
        }
    }

    private <T extends Service> void updateService(UpdateServiceDTO updateServiceRequest, T service) throws SvcValidationException {
        List<ServiceUpdateAction> actions = Optional.ofNullable(updateServiceRequest.getAction())
                .map(Collections::singletonList)
                .orElse(updateServiceRequest.getActions());

        if (actions.isEmpty()) {
            throw new SvcValidationException(localizedMessageBuilder, SERVICE_NO_UPDATE_ACTION);
        }

        actions.forEach(action -> {
            switch (action) {
                case addParent:
                case changeParent:
                    changeParent(updateServiceRequest, service);
                    break;
                case removeParent:
                    removeParent(service);
                    break;
                case addAccess:
                case changeAccess:
                    changeRelatedServiceAccess(updateServiceRequest, service);
                    break;
                case removeAccess:
                    removeRelatedServiceAccess(service);
                    break;
                case addEqt:
                case changeEqt:
                    changeEquipment(updateServiceRequest, service);
                    break;
                case removeEqt:
                    removeEquipment(service);
                    break;
                case addNumber:
                case changeNumber:
                    changeNumber(updateServiceRequest, service);
                    break;
                case removeNumber:
                    removeNumber(service);
                    break;
                case addAccPoint:
                case changeAccPoint:
                    changeAccessPointId(updateServiceRequest, service);
                    break;
                case removeAccPoint:
                    removeAccessPointId(service);
                    break;
                case addCrmRef:
                case changeCrmRef:
                    changeCrmServiceId(updateServiceRequest, service);
                    break;
                case removeCrmRef:
                    removeCrmServiceId(service);
                    break;
                case addRange:
                case changeRange:
                    changeMainRangeNumber(updateServiceRequest, service);
                    break;
                case removeRange:
                    removeMainRangeNumber(service);
                    break;
                case addOntId:
                case changeOntId:
                    changeOntId(updateServiceRequest, service);
                    break;
                case removeOntId:
                    removeOntId(service);
                    break;
                case addTechId:
                case changeTechId:
                    changeTechId(updateServiceRequest, service);
                    break;
                case removeTechId:
                    removeTechId(service);
                    break;
                default:
                    throw new SvcValidationException(localizedMessageBuilder, SERVICE_INVALID_ACTION, action);
            }
        });
    }

    private static <T extends Service> void removeTechId(final T service) {
        if (ACCESS.equals(service.getServiceCategory())) {
            ((ServiceAccess) service).setTechId(null);
        } else {
            ((ServiceComponent) service).setTechId(null);
        }
    }

    private <T extends Service> void changeTechId(final UpdateServiceDTO updateServiceRequest, final T service) {
        final var newTechId = updateServiceRequest.getTechId();
        if (newTechId == null) {
            throw new SvcValidationException(localizedMessageBuilder, MANDATORY_FIELD_TECH_ID_IS_NOT_SENT);
        }

        if (ACCESS.equals(service.getServiceCategory())) {
            ((ServiceAccess) service).setTechId(newTechId);
        } else {
            ((ServiceComponent) service).setTechId(newTechId);
        }
    }

    private <T extends Service> void removeOntId(final T service) {
        if (!ACCESS.equals(service.getServiceCategory())) {
            throw new SvcValidationException(localizedMessageBuilder, SERVICE_CATEGORY_IS_NOT_OF_TYPE_ACCESS);
        }

        ((ServiceAccess) service).setOntId(null);
    }

    private <T extends Service> void changeOntId(final UpdateServiceDTO updateServiceRequest, final T service) {
        final var newOntId = updateServiceRequest.getOntId();
        if (newOntId == null) {
            throw new SvcValidationException(localizedMessageBuilder, MANDATORY_FIELD_ONT_ID_IS_NOT_SENT);
        }

        if (!ACCESS.equals(service.getServiceCategory())) {
            throw new SvcValidationException(localizedMessageBuilder, SERVICE_CATEGORY_IS_NOT_OF_TYPE_ACCESS);
        }

        serviceAccessRepository.findByOntId(newOntId).ifPresent(existing -> {
            if (!existing.getServiceId().equals(service.getServiceId())) {
                throw new SvcConflictException(localizedMessageBuilder, SERVICE_ALREADY_EXIST_ONTID, existing.getServiceId(), newOntId);
            }
        });

        ((ServiceAccess) service).setOntId(newOntId);
    }

    private static <T extends Service> void removeMainRangeNumber(final T service) {
        service.setMainRangeId(null);
    }

    private <T extends Service> void changeMainRangeNumber(final UpdateServiceDTO updateServiceRequest, final T service) {
        final var newRangeId = updateServiceRequest.getMainRangeId();
        if (newRangeId == null) {
            throw new SvcValidationException(localizedMessageBuilder, MANDATORY_FIELD_MAIN_RANGE_ID_IS_NOT_SENT);
        }

        serviceRepository.findByMainRangeId(newRangeId).ifPresent(existing -> {
            if (!existing.getServiceId().equals(service.getServiceId())) {
                throw new SvcConflictException(localizedMessageBuilder, SERVICE_ALREADY_EXIST_RANGEID, existing.getServiceId(), newRangeId);
            }
        });

        service.setMainRangeId(newRangeId);
    }

    private static <T extends Service> void removeCrmServiceId(final T service) {
        service.setCrmServiceId(null);
    }

    private <T extends Service> void changeCrmServiceId(final UpdateServiceDTO updateServiceRequest, final T service) {
        final var newCrmServiceId = updateServiceRequest.getCrmServiceId();
        if (newCrmServiceId != null) {
            serviceRepository.findByCrmServiceIdAndServiceActivityAndServiceCategory(newCrmServiceId, service.getServiceActivity(), service.getServiceCategory()).ifPresent(existing -> {
                if (!existing.getServiceId().equals(service.getServiceId())) {
                    throw new SvcConflictException(localizedMessageBuilder, SERVICE_ALREADY_EXIST_CRMSERVICEID_ACTIVITY, existing.getServiceId(), newCrmServiceId, service.getServiceActivity(), service.getServiceCategory());
                }
            });
        }

        service.setCrmServiceId(newCrmServiceId);

        Long customerNo = updateServiceRequest.getCustomerNo();
        if (customerNo != null) {
            service.setCustomerNo(customerNo);
        }

        techIdProcessor.getAtUpdateTime(service, updateServiceRequest)
                .ifPresent(newTechId -> {
                    if (ACCESS.equals(service.getServiceCategory())) {
                        ((ServiceAccess) service).setTechId(newTechId);
                    } else {
                        ((ServiceComponent) service).setTechId(newTechId);
                    }
                });
    }

    private <T extends Service> void removeAccessPointId(final T service) {
        if (!ACCESS.equals(service.getServiceCategory())) {
            throw new SvcValidationException(localizedMessageBuilder, SERVICE_CATEGORY_IS_NOT_OF_TYPE_ACCESS);
        }

        ((ServiceAccess) service).setAccessPointId(null);
    }

    private <T extends Service> void changeAccessPointId(final UpdateServiceDTO updateServiceRequest, final T service) {
        final String newAccessPointId = updateServiceRequest.getAccessPointId();
        if (newAccessPointId == null) {
            throw new SvcValidationException(localizedMessageBuilder, MANDATORY_FIELD_ACCESS_POINT_ID_IS_NOT_SENT);
        }

        serviceAccessRepository.findByAccessPointId(newAccessPointId).ifPresent(existingService -> {
            if (!existingService.getServiceId().equals(service.getServiceId())) {
                throw new SvcValidationException(localizedMessageBuilder, SERVICEID_ALREADY_IN_USE, existingService.getServiceId());
            }
        });

        if (!ACCESS.equals(service.getServiceCategory())) {
            throw new SvcValidationException(localizedMessageBuilder, SERVICE_CATEGORY_IS_NOT_OF_TYPE_ACCESS);
        }

        ((ServiceAccess) service).setAccessPointId(newAccessPointId);
    }

    private static <T extends Service> void removeNumber(final T service) {
        service.setNumber(null);
    }

    private <T extends Service> void changeNumber(final UpdateServiceDTO updateServiceRequest, final T service) {
        final String newNumber = updateServiceRequest.getNumber();
        if (newNumber == null) {
            throw new SvcValidationException(localizedMessageBuilder, MANDATORY_FIELD_NUMBER_IS_NOT_SENT);
        }

        if (!service.getServiceActivity().toString().equalsIgnoreCase(updateServiceRequest.getActivityNumber())) {
            throw new SvcValidationException(localizedMessageBuilder, NUMBER_ACTIVITY_MUST_BE_EQUAL_TO_SERVICE_ACTIVITY,
                    updateServiceRequest.getActivityNumber(), service.getServiceActivity());
        }

        serviceRepository.findByNumber(newNumber).ifPresent(existing -> {
            if (!existing.getServiceId().equals(service.getServiceId())) {
                throw new SvcConflictException(localizedMessageBuilder, SERVICE_ALREADY_EXIST_NUMBER, existing.getServiceId(), newNumber);
            }
        });

        service.setNumber(newNumber);
    }

    private <T extends Service> void removeEquipment(final T service) {
        if (!ACCESS.equals(service.getServiceCategory())) {
            throw new SvcValidationException(localizedMessageBuilder, SERVICE_CATEGORY_IS_NOT_OF_TYPE_ACCESS);
        }

        ((ServiceAccess) service).setEquipmentId(null);
        ((ServiceAccess) service).setEquipmentCategory(null);
    }

    private <T extends Service> void changeEquipment(final UpdateServiceDTO updateServiceRequest, final T service) {
        final Long newEquipmentId = updateServiceRequest.getEquipmentId();
        if (newEquipmentId == null) {
            throw new SvcValidationException(localizedMessageBuilder, MANDATORY_FIELD_EQUIPMENT_ID_IS_NOT_SENT);
        }

        if (!ACCESS.equals(service.getServiceCategory())) {
            throw new SvcValidationException(localizedMessageBuilder, SERVICE_CATEGORY_IS_NOT_OF_TYPE_ACCESS);
        }

        ((ServiceAccess) service).setEquipmentId(newEquipmentId);
        ((ServiceAccess) service).setEquipmentCategory(updateServiceRequest.getEquipmentCategory());
    }

    private <T extends Service> void removeRelatedServiceAccess(final T service) {
        if (!COMPONENT.equals(service.getServiceCategory())) {
            throw new SvcValidationException(localizedMessageBuilder, SERVICE_CATEGORY_IS_NOT_OF_TYPE_COMPONENT);
        }

        ((ServiceComponent) service).setServiceAccess(null);
    }

    private <T extends Service> void changeRelatedServiceAccess(final UpdateServiceDTO updateServiceRequest, final T service) {
        final Long newAccessServiceId = updateServiceRequest.getAccessServiceId();
        if (newAccessServiceId == null) {
            throw new SvcValidationException(localizedMessageBuilder, ACCESS_SERVICE_ID_IS_NOT_SENT);
        }

        if (!ServiceCategory.COMPONENT.equals(service.getServiceCategory())) {
            throw new SvcValidationException(localizedMessageBuilder, SERVICE_CATEGORY_IS_NOT_OF_TYPE_COMPONENT);
        }

        var serviceToAssociate = serviceAccessRepository.findById(newAccessServiceId)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_ACCESS_NOT_FOUND_ID, newAccessServiceId));
        if (!ACCESS.equals(serviceToAssociate.getServiceCategory())) {
            throw new SvcValidationException(localizedMessageBuilder, PARENT_SERVICE_CATEGORY_IS_NOT_OF_TYPE_ACCESS);
        }

        ((ServiceComponent) service).setServiceAccess(serviceToAssociate);
    }

    private <T extends Service> void removeParent(final T service) {
        if (!ACCESS.equals(service.getServiceCategory())) {
            throw new SvcValidationException(localizedMessageBuilder, SERVICE_CATEGORY_IS_NOT_OF_TYPE_ACCESS);
        }

        ((ServiceAccess) service).setParentService(null);
    }

    private <T extends Service> void changeParent(final UpdateServiceDTO updateServiceRequest, final T service) {
        if (updateServiceRequest.getParentServiceId() == null) {
            throw new SvcValidationException(localizedMessageBuilder, MANDATORY_FIELD_PARENT_SERVICE_PARENT_SERVICE_ID_IS_NOT_SENT);
        }

        if (!ACCESS.equals(service.getServiceCategory())) {
            throw new SvcValidationException(localizedMessageBuilder, SERVICE_CATEGORY_IS_NOT_OF_TYPE_ACCESS);
        }

        var serviceParent = serviceAccessRepository.findById(updateServiceRequest.getParentServiceId())
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_ACCESS_NOT_FOUND_ID, updateServiceRequest.getParentServiceId()));
        if (!ACCESS.equals(serviceParent.getServiceCategory())) {
            throw new SvcValidationException(localizedMessageBuilder, PARENT_SERVICE_CATEGORY_IS_NOT_OF_TYPE_ACCESS);
        }

        ((ServiceAccess) service).setParentService(serviceParent);

        unmSynchronizer.synchronizeUnmService(((ServiceAccess) service));
    }


    public ServiceDTO addRequest(ServiceRequestDTO serviceRequest) {

        var service = serviceRepository.findById(serviceRequest.getServiceId())
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_NOT_FOUND_ID, serviceRequest.getServiceId()));
        if (service.getActionRequest() == null || ServiceActionStatus.COMPLETED.equals(service.getServiceActionStatus()) || ServiceActionStatus.CANCELED.equals(service.getServiceActionStatus())) {
            addRequestToService(service, serviceRequest);
        } else {
            throw new SvcValidationException(localizedMessageBuilder, SERVICE_ALREADY_REQUEST, service.getServiceId(), service.getActionRequest(), service.getServiceActionStatus());
        }
        var savedService = serviceRepository.save(service);
        if (savedService.getServiceCategory().equals(ACCESS)) {
            unmSynchronizer.synchronizeUnmService((ServiceAccess) savedService);
        }
        return servicesResourceAssembler.toModel(service);
    }

    private void addRequestToService(Service service, ServiceRequestDTO serviceRequest) {
        switch (serviceRequest.getActionRequest()) {
            case DEACTIVATION:
                if (!Status.ACTIVATED.equals(service.getStatus()) && !Status.SUSPENDED.equals(service.getStatus()) && !Status.BARRED.equals(service.getStatus())) {
                    throw new SvcValidationException(localizedMessageBuilder, SERVICE_INCOMPATIBLE_STATUS, service.getServiceId(), service.getStatus());
                }
                break;
            case ACTIVATION:
                if (!Status.PENDING.equals(service.getStatus())) {
                    throw new SvcValidationException(localizedMessageBuilder, SERVICE_INCOMPATIBLE_STATUS, service.getServiceId(), service.getStatus());
                }
                break;
            case BARRING:
                if (!List.of(Status.ACTIVATED, Status.SUSPENDED, Status.BARRED).contains(service.getStatus())) {
                    throw new SvcValidationException(localizedMessageBuilder, SERVICE_INCOMPATIBLE_STATUS, service.getServiceId(), service.getStatus());
                }
                break;
            case UNBARRING:
                if (!Status.BARRED.equals(service.getStatus()) && !"OPER".equals(service.getReasonRequest())) {
                    throw new SvcValidationException(localizedMessageBuilder, SERVICE_INCOMPATIBLE_STATUS_REASON_REQUEST, service.getServiceId(), service.getStatus(), service.getReasonRequest());
                }
                break;
            case SUSP_OPER:
                if (!Status.ACTIVATED.equals(service.getStatus()) && !Status.BARRED.equals(service.getStatus())) {
                    throw new SvcValidationException(localizedMessageBuilder, SERVICE_INCOMPATIBLE_STATUS, service.getServiceId(), service.getStatus());
                }
                break;
            case RESUM_OPER:
                if (!Status.SUSPENDED.equals(service.getStatus()) && !"OPER".equals(service.getReasonRequest())) {
                    throw new SvcValidationException(localizedMessageBuilder, SERVICE_INCOMPATIBLE_STATUS_REASON_REQUEST, service.getServiceId(), service.getStatus(), service.getReasonRequest());
                }
                break;
            case SUSP_CLI:
                if (!Status.ACTIVATED.equals(service.getStatus())) {
                    throw new SvcValidationException(localizedMessageBuilder, SERVICE_INCOMPATIBLE_STATUS, service.getServiceId(), service.getStatus());
                }
                break;
            case RESUM_CLI:
                if (!Status.SUSPENDED.equals(service.getStatus()) && !"CLI".equals(service.getReasonRequest())) {
                    throw new SvcValidationException(localizedMessageBuilder, SERVICE_INCOMPATIBLE_STATUS_REASON_REQUEST, service.getServiceId(), service.getStatus(), service.getReasonRequest());
                }
                break;
        }
        service.setPlannedDate(serviceRequest.getPlannedDate());
        service.setActionRequest(serviceRequest.getActionRequest());
        service.setActionPlanned(service.getPlannedDate() != null);
        service.setDateRequest(LocalDateTime.now(clock));
        service.setServiceActionStatus(ServiceActionStatus.PENDING);
        service.setActionEnd(null);
        if (!Boolean.TRUE.equals(service.getActionPlanned())) {
            service.setActionStart(LocalDateTime.now(clock));
        } else {
            service.setActionStart(null);
        }

        if (serviceRequest.getSubscriptionOrderId() != null) {
            service.setServiceOrderId(serviceRequest.getSubscriptionOrderId() + "_" + service.getServiceId());
        }
    }

    private Service requestToService(SearchServiceDTO requestDTO) {
        if (requestDTO.getServiceId() != null) {
            return serviceRepository.findById(requestDTO.getServiceId())
                    .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_NOT_FOUND_ID, requestDTO.getServiceId()));
        } else if (requestDTO.getSubscriptionOrderId() != null) {
            return serviceRepository.findByServiceOrderIdStartingWith(String.valueOf(requestDTO.getSubscriptionOrderId()))
                    .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_NOT_FOUND_ORDER_ID, requestDTO.getSubscriptionOrderId()));
        } else {
            throw new SvcValidationException(localizedMessageBuilder, MANDATORY_SERVICE_ID_OR_SUBSCRIPTION_ORDER_ID);
        }
    }

    public void setEventOnService(Service service, Event event) {
        if (service.getActionRequest() == null || ServiceActionStatus.COMPLETED.equals(service.getServiceActionStatus()) || ServiceActionStatus.CANCELED.equals(service.getServiceActionStatus())) {
            switch (event) {
                case activate:
                    service.setStatus(serviceStatusChanger.updateStatus(String.valueOf(service.getServiceId()), service.getStatus(), Event.activate));
                    if (Status.ACTIVATED.equals(service.getStatus())) {
                        synchronize(service);
                        service.setActivationDate(LocalDateTime.now(clock));
                    }
                    break;
                case cancel:
                    service.setStatus(serviceStatusChanger.updateStatus(String.valueOf(service.getServiceId()), service.getStatus(), Event.cancel));
                    if (Status.CANCELED.equals(service.getStatus())) {
                        if (ACCESS.equals(service.getServiceCategory())) {
                            ((ServiceAccess) Hibernate.unproxy(service)).setEquipmentId(null);
                            ((ServiceAccess) Hibernate.unproxy(service)).setAccessPointId(null);
                        }
                        service.setNumber(null);
                        service.setMainRangeId(null);
                        synchronize(service);
                        service.setCancellationDate(LocalDateTime.now(clock));
                    }
                    break;
                case barring:
                    service.setStatus(serviceStatusChanger.updateStatus(String.valueOf(service.getServiceId()), service.getStatus(), Event.barring));
                    if (Status.BARRED.equals(service.getStatus())) {
                        synchronize(service);
                        service.setBarringDate(LocalDateTime.now(clock));
                        service.setReasonRequest("OPER");
                    }
                    break;
                case unBarring:
                    service.setStatus(serviceStatusChanger.updateStatus(String.valueOf(service.getServiceId()), service.getStatus(), Event.unBarring));
                    if (Status.ACTIVATED.equals(service.getStatus())) {
                        synchronize(service);
                        service.setBarringDate(null);
                        service.setReasonRequest(null);
                    }
                    break;
                case deactivate:
                    service.setStatus(serviceStatusChanger.updateStatus(String.valueOf(service.getServiceId()), service.getStatus(), Event.deactivate));
                    if (Status.DEACTIVATED.equals(service.getStatus())) {
                        synchronize(service);
                        service.setDeactivationDate(LocalDateTime.now(clock));
                        if (ACCESS.equals(service.getServiceCategory())) {
                            ((ServiceAccess) Hibernate.unproxy(service)).setEquipmentId(null);
                            ((ServiceAccess) Hibernate.unproxy(service)).setAccessPointId(null);
                            ((ServiceAccess) Hibernate.unproxy(service)).setOntId(null);
                        }
                        if (COMPONENT.equals(service.getServiceCategory())) {
                            service.setCrmServiceId(null);
                        }
                        service.setReasonRequest(null);
                        service.setNumber(null);
                        service.setMainRangeId(null);
                    }
                    break;
                case susp_oper:
                    service.setStatus(serviceStatusChanger.updateStatus(String.valueOf(service.getServiceId()), service.getStatus(), Event.susp_oper));
                    if (Status.SUSPENDED.equals(service.getStatus())) {
                        synchronize(service);
                        service.setResumedDate(null);
                        service.setSuspensionDate(LocalDateTime.now(clock));
                        service.setReasonRequest("OPER");
                    }
                    break;
                case susp_cli:
                    service.setStatus(serviceStatusChanger.updateStatus(String.valueOf(service.getServiceId()), service.getStatus(), Event.susp_cli));
                    if (Status.SUSPENDED.equals(service.getStatus())) {
                        synchronize(service);
                        service.setResumedDate(null);
                        service.setSuspensionDate(LocalDateTime.now(clock));
                        service.setReasonRequest("CLI");
                    }
                    break;
                case resum_oper:
                    service.setStatus(serviceStatusChanger.updateStatus(String.valueOf(service.getServiceId()), service.getStatus(), Event.resum_oper));
                    if (Status.ACTIVATED.equals(service.getStatus())) {
                        synchronize(service);
                        service.setSuspensionDate(null);
                        service.setResumedDate(LocalDateTime.now(clock));
                        service.setReasonRequest(null);
                    }
                    break;
                case resum_cli:
                    service.setStatus(serviceStatusChanger.updateStatus(String.valueOf(service.getServiceId()), service.getStatus(), Event.resum_cli));
                    if (Status.ACTIVATED.equals(service.getStatus())) {
                        synchronize(service);
                        service.setSuspensionDate(null);
                        service.setResumedDate(LocalDateTime.now(clock));
                        service.setReasonRequest(null);
                    }
                    break;
                case rollbackCancellation:
                    service.setStatus(serviceStatusChanger.updateStatus(String.valueOf(service.getServiceId()), service.getStatus(), Event.rollbackCancellation));
                    if (Status.PENDING.equals(service.getStatus())) {
                        synchronize(service);
                        service.setCancellationDate(null);
                    }
                    break;
                case rollbackActivation:
                    service.setStatus(serviceStatusChanger.updateStatus(String.valueOf(service.getServiceId()), service.getStatus(), Event.rollbackActivation));
                    if (Status.PENDING.equals(service.getStatus())) {
                        synchronize(service);
                        service.setActivationDate(null);
                    }
                    break;
                case rollbackDeactivation:
                    service.setStatus(serviceStatusChanger.updateStatus(String.valueOf(service.getServiceId()), service.getStatus(), Event.rollbackDeactivation));
                    if (Status.ACTIVATED.equals(service.getStatus())) {
                        service.setDeactivationDate(null);
                        if (ACCESS.equals(service.getServiceCategory())) {
                            Revisions<Integer, ServiceAccess> revisions = serviceAccessRepository.findRevisions(service.getServiceId());
                            List<Revision<Integer, ServiceAccess>> data = revisions.getContent();
                            if (data.size() < 2) {
                                throw new SvcValidationException(localizedMessageBuilder, SERVICE_NOT_ENOUGH_INFO_ROLLBACK_DEACTIVATION);
                            }
                            var lastServiceAccess = data.get(data.size() - 2).getEntity();
                            ((ServiceAccess) Hibernate.unproxy(service)).setEquipmentId(lastServiceAccess.getEquipmentId());
                            ((ServiceAccess) Hibernate.unproxy(service)).setAccessPointId(lastServiceAccess.getAccessPointId());
                            synchronize(service);

                        }
                        Revisions<Integer, Service> revisions = serviceRepository.findRevisions(service.getServiceId());
                        List<Revision<Integer, Service>> data = revisions.getContent();
                        if (data.size() < 2) {
                            throw new SvcValidationException(localizedMessageBuilder, SERVICE_NOT_ENOUGH_INFO_ROLLBACK_DEACTIVATION);
                        }
                        var lastService = data.get(data.size() - 2).getEntity();
                        service.setNumber(lastService.getNumber());
                        service.setMainRangeId(lastService.getMainRangeId());
                    }
                    break;
                default:
                    throw new SvcValidationException(localizedMessageBuilder, SERVICE_INVALID_ACTION, event);
            }
        } else {
            throw new SvcValidationException(localizedMessageBuilder, SERVICE_ALREADY_REQUEST, service.getServiceId(), service.getActionRequest(), service.getServiceActionStatus());
        }
    }

    public ServiceDTO activateRequest(SearchServiceDTO requestDTO) {
        var service = requestToService(requestDTO);

        if (ServiceActionStatus.PENDING.equals(service.getServiceActionStatus())) {
            service.setServiceActionStatus(ServiceActionStatus.COMPLETED);
            service.setActionEnd(LocalDateTime.now(clock));
            switch (service.getActionRequest()) {
                case ACTIVATION:
                    setEventOnService(service, Event.activate);
                    break;
                case RESUM_OPER:
                    setEventOnService(service, Event.resum_oper);
                    break;
                case SUSP_OPER:
                    setEventOnService(service, Event.susp_oper);
                    break;
                case RESUM_CLI:
                    setEventOnService(service, Event.resum_cli);
                    break;
                case SUSP_CLI:
                    setEventOnService(service, Event.susp_cli);
                    break;
                case BARRING:
                    setEventOnService(service, Event.barring);
                    break;
                case UNBARRING:
                    // The service should stay BARRED if there is still an active Barring
                    if (!Status.BARRED.equals(requestDTO.getStatus())) {
                        setEventOnService(service, Event.unBarring);
                    }
                    break;
                case DEACTIVATION:
                    setEventOnService(service, Event.deactivate);
                    break;
            }
        } else {
            log.warn(localizedMessageBuilder.getLocalizedMessage(SERVICE_ACTION_STATUS_NOT_PENDING, service.getServiceId(), service.getServiceActionStatus()));
        }

        serviceRepository.save(service);
        return servicesResourceAssembler.toModel(service);
    }

    public ServiceDTO cancelActivationRequest(SearchServiceDTO requestDTO) {
        var service = requestToService(requestDTO);

        if (ServiceActionRequest.ACTIVATION.equals(service.getActionRequest())) {
            if (!ServiceActionStatus.COMPLETED.equals(service.getServiceActionStatus()) && !ServiceActionStatus.CANCELED.equals(service.getServiceActionStatus())) {
                service.setActionRequest(null);
                service.setActionPlanned(null);
                service.setActionEnd(LocalDateTime.now(clock));
                service.setPlannedDate(null);
                if (ServiceAccess.class.isAssignableFrom(service.getClass())) {
                    ((ServiceAccess) service).setEquipmentId(null);
                    ((ServiceAccess) service).setAccessPointId(null);
                    if (service.getServiceCategory().equals(ACCESS)) {
                        unmSynchronizer.synchronizeUnmService(((ServiceAccess) service));
                    }
                }
                service.setNumber(null);
                service.setMainRangeId(null);
                service.setServiceOrderId(null);
                service.setServiceActionStatus(ServiceActionStatus.CANCELED);

                setEventOnService(service, Event.cancel);

            } else {
                throw new SvcValidationException(localizedMessageBuilder, SERVICE_NO_CURRENT_REQUEST, service.getServiceId(), service.getServiceActionStatus(), service.getActionRequest());
            }
        } else {
            throw new SvcValidationException(localizedMessageBuilder, SERVICE_ACTION_NOT_ACTIVATION, service.getActionRequest());
        }

        serviceRepository.save(service);
        return servicesResourceAssembler.toModel(service);
    }

    public <T extends Service> Specification<T> prepareSpecificationGenericService(final BaseSearchServiceDTO searchServiceDTO) {
        Specification<T> specification;
        specification = StringUtils.isNotBlank(searchServiceDTO.getCrmServiceId()) ? Specification.where(ServiceSpecification.hasCrmServiceId(searchServiceDTO.getCrmServiceId())) : null;
        specification = Objects.nonNull(searchServiceDTO.getServiceId()) ? CommonFunctions.addSpecification(specification, ServiceSpecification.hasServiceId(searchServiceDTO.getServiceId())) : specification;
        specification = Objects.nonNull(searchServiceDTO.getSubscriptionId()) ? CommonFunctions.addSpecification(specification, ServiceSpecification.hasSubscriptionId(searchServiceDTO.getSubscriptionId())) : specification;
        specification = Objects.nonNull(searchServiceDTO.getServiceActivity()) ? CommonFunctions.addSpecification(specification, ServiceSpecification.hasServiceActivity(searchServiceDTO.getServiceActivity())) : specification;
        specification = Objects.nonNull(searchServiceDTO.getStatus()) ? CommonFunctions.addSpecification(specification, ServiceSpecification.hasStatus(searchServiceDTO.getStatus())) : specification;
        specification = StringUtils.isNotBlank(searchServiceDTO.getNumber()) ? CommonFunctions.addSpecification(specification, ServiceSpecification.hasNumber(searchServiceDTO.getNumber())) : specification;
        specification = Objects.nonNull(searchServiceDTO.getMainRangeId()) ? CommonFunctions.addSpecification(specification, ServiceSpecification.hasMainRangeId(searchServiceDTO.getMainRangeId())) : specification;
        specification = Objects.nonNull(searchServiceDTO.getActivationDate()) ? CommonFunctions.addSpecification(specification, ServiceSpecification.hasActivationDate(searchServiceDTO.getActivationDate())) : specification;
        specification = Objects.nonNull(searchServiceDTO.getCustomerNo()) ? CommonFunctions.addSpecification(specification, ServiceSpecification.hasCustomerNo(searchServiceDTO.getCustomerNo())) : specification;

        return specification;
    }

    public Specification<ServiceAccess> prepareSpecificationServiceAccess(final BaseSearchServiceDTO searchServiceDTO) {
        final var searchAccessDTO = (SearchServiceAccessDTO) searchServiceDTO;
        Specification<ServiceAccess> specification = prepareSpecificationGenericService(searchServiceDTO);

        specification = Objects.nonNull(searchAccessDTO.getParentServiceId()) ? CommonFunctions.addSpecification(specification, ServiceAccessSpecification.hasParentServiceId(searchAccessDTO.getParentServiceId())) : specification;
        specification = Objects.nonNull(searchAccessDTO.getAccessPointId()) ? CommonFunctions.addSpecification(specification, ServiceAccessSpecification.hasAccessPointId(searchAccessDTO.getAccessPointId())) : specification;
        specification = Objects.nonNull(searchAccessDTO.getEquipmentId()) ? CommonFunctions.addSpecification(specification, ServiceAccessSpecification.hasEquipmentId(searchAccessDTO.getEquipmentId())) : specification;
        specification = Objects.nonNull(searchAccessDTO.getAccessType()) ? CommonFunctions.addSpecification(specification, ServiceAccessSpecification.hasAccessType(searchAccessDTO.getAccessType())) : specification;
        specification = StringUtils.isNotBlank(searchAccessDTO.getOntId()) ? CommonFunctions.addSpecification(specification, ServiceAccessSpecification.hasOntId(searchAccessDTO.getOntId())) : specification;
        specification = StringUtils.isNotBlank(searchServiceDTO.getTechId()) ? CommonFunctions.addSpecification(specification, ServiceAccessSpecification.hasTechId(searchServiceDTO.getTechId())) : specification;

        return specification;
    }

    public Specification<ServiceComponent> prepareSpecificationServiceComponent(final BaseSearchServiceDTO searchServiceDTO) {
        final var searchComponentDTO = (SearchServiceComponentDTO) searchServiceDTO;
        Specification<ServiceComponent> specification = prepareSpecificationGenericService(searchServiceDTO);
        specification = Objects.nonNull(searchServiceDTO.getAccessServiceId()) ? CommonFunctions.addSpecification(specification, ServiceComponentSpecification.hasAccessServiceId(searchServiceDTO.getAccessServiceId())) : specification;
        specification = Objects.nonNull(searchServiceDTO.getAccessCrmServiceId()) ? CommonFunctions.addSpecification(specification, ServiceComponentSpecification.hasAccessCrmServiceId(searchServiceDTO.getAccessCrmServiceId())) : specification;
        specification = Objects.nonNull(searchComponentDTO.getComponentType()) ? CommonFunctions.addSpecification(specification, ServiceComponentSpecification.hasComponentType(searchComponentDTO.getComponentType())) : specification;
        specification = StringUtils.isNotBlank(searchServiceDTO.getTechId()) ? CommonFunctions.addSpecification(specification, ServiceComponentSpecification.hasTechId(searchServiceDTO.getTechId())) : specification;

        return specification;
    }

    private Page<ServiceComponent> searchServiceComponentInner(BaseSearchServiceDTO searchServiceDTO, Pageable pageable) {
        final Specification<ServiceComponent> specification = prepareSpecificationServiceComponent(searchServiceDTO);
        return serviceComponentRepository.findAll(specification, pageable);
    }

    private Page<ServiceAccess> searchServiceAccessInner(BaseSearchServiceDTO searchServiceDTO, Pageable pageable) {
        final Specification<ServiceAccess> specification = prepareSpecificationServiceAccess(searchServiceDTO);
        return serviceAccessRepository.findAll(specification, pageable);
    }

    private Page<Service> searchService(SearchServiceDTO searchServiceDTO, Pageable pageable) {
        final Specification<Service> specification = prepareSpecificationGenericService(searchServiceDTO);
        return serviceRepository.findAll(specification, pageable);
    }

    public Page<ServiceAccessDTO> searchServiceAccess(final BaseSearchServiceDTO searchServiceDTO, Pageable pageable) {
        return new PageImpl<>(this.searchServiceAccessInner(searchServiceDTO, pageable).map(ServiceAccessMapper.INSTANCE::toDto).stream().collect(Collectors.toList()));
    }

    public Page<ServiceComponentDTO> searchServiceComponent(final BaseSearchServiceDTO searchServiceDTO, Pageable pageable) {
        return new PageImpl<>(this.searchServiceComponentInner(searchServiceDTO, pageable).map(ServiceComponentMapper.INSTANCE::toDto).stream().collect(Collectors.toList()));
    }

    public PagedModel<ServiceDTO> search(SearchServiceDTO searchServiceDTO, Pageable pageable, PagedResourcesAssembler assembler) {

        PagedModel<ServiceDTO> serviceDTOS;
        if (ACCESS.equals(searchServiceDTO.getServiceCategory())) {
            var servicesAccess = searchServiceAccessInner(MapSearchServices.INSTANCE.toServiceAccess(searchServiceDTO), pageable);
            serviceDTOS = assembler.toModel(servicesAccess, serviceAccessResourceAssembler, linkTo(ServiceProcess.class).slash("/").withSelfRel());
        } else if (ServiceCategory.COMPONENT.equals(searchServiceDTO.getServiceCategory())) {
            var servicesComponent = searchServiceComponentInner(MapSearchServices.INSTANCE.toServiceComponent(searchServiceDTO), pageable);
            serviceDTOS = assembler.toModel(servicesComponent, serviceComponentResourceAssembler, linkTo(ServiceProcess.class).slash("/").withSelfRel());
        } else {
            var services = searchService(searchServiceDTO, pageable);
            serviceDTOS = assembler.toModel(services, servicesResourceAssembler, linkTo(ServiceProcess.class).slash("/").withSelfRel());
        }

        serviceDTOS.iterator().forEachRemaining(serviceAccessDTO -> {
            List<Event> serviceTransitions = new ArrayList<>(serviceStatusChanger.getAvailableEventsWithId(String.valueOf(serviceAccessDTO.getServiceId()), serviceAccessDTO.getStatus()));
            serviceAccessDTO.setServiceTransitions(serviceTransitions);
        });
        return serviceDTOS;
    }

    public PagedModel<ServiceDTO> getAll(Pageable pageable, PagedResourcesAssembler<Service> assembler) {
        Page<Service> services = serviceRepository.findAll(pageable);
        PagedModel<ServiceDTO> pr = assembler.toModel(services, servicesResourceAssembler, linkTo(ServiceProcess.class).slash("/").withSelfRel());
        pr.iterator().forEachRemaining(serviceDTO -> {
            List<Event> serviceTransitions = new ArrayList<>(serviceStatusChanger.getAvailableEventsWithId(String.valueOf(serviceDTO.getServiceId()), serviceDTO.getStatus()));
            serviceDTO.setServiceTransitions(serviceTransitions);
        });
        return pr;
    }

    public PagedModel<RevisionDTO<ServiceDTO>> getServiceRevisionsById(Long serviceId, Pageable pageable, PagedResourcesAssembler assembler) {

        var service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_NOT_FOUND_ID, serviceId));

        if (ACCESS.equals(service.getServiceCategory())) {
            Page<Revision<Integer, ServiceAccess>> revisions = serviceAccessRepository.findRevisions(serviceId, pageable);
            return assembler.toModel(revisions, revisionServiceAccessAssembler, linkTo(ServiceProcess.class).slash("/").withSelfRel());
        } else if (ServiceCategory.COMPONENT.equals(service.getServiceCategory())) {
            Page<Revision<Integer, ServiceComponent>> revisions = serviceComponentRepository.findRevisions(serviceId, pageable);
            return assembler.toModel(revisions, revisionServiceComponentAssembler, linkTo(ServiceProcess.class).slash("/").withSelfRel());
        } else {
            throw new SvcValidationException(localizedMessageBuilder, SERVICE_INVALID_CATEGORY, service.getServiceCategory());
        }
    }

    public ServiceDTO setEventOnService(ServiceActionRequestDTO serviceActionRequestDTO) {
        var requestDTO = new SearchServiceDTO();
        requestDTO.setServiceId(serviceActionRequestDTO.getServiceId());
        requestDTO.setSubscriptionOrderId(serviceActionRequestDTO.getSubscriptionOrderId());
        var service = requestToService(requestDTO);
        this.setEventOnService(service, serviceActionRequestDTO.getAction());
        serviceRepository.save(service);
        return servicesResourceAssembler.toModel(service);
    }

    public void synchronize(Service service) {
        if (service.getServiceCategory().equals(ACCESS)) {
            unmSynchronizer.synchronizeUnmService((ServiceAccess) service);
        }
    }

    public void delete(final long serviceId, final boolean isForced) {
        ServiceAccess serviceAccess;

        var service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_NOT_FOUND_ID, serviceId));

        Lambdas.verifyOrThrow.test(!List.of(Status.CANCELED, Status.DEACTIVATED).contains(service.getStatus()),
                new SvcValidationException(localizedMessageBuilder, SERVICE_CANNOT_BE_DELETED_NOT_VALID_STATUS, service.getServiceId()));

        if (ACCESS.equals(service.getServiceCategory())) {
            serviceAccess = serviceAccessRepository.findById(service.getServiceId())
                    .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_ACCESS_NOT_FOUND_ID, service.getServiceId()));

            Lambdas.verifyOrThrow.test(!serviceAccess.getChildServiceAccess().isEmpty(),
                    new SvcValidationException(localizedMessageBuilder, SERVICE_CANNOT_BE_DELETED_PARENT, service.getServiceId()));
            Lambdas.verifyOrThrow.test(!isForced && !serviceAccess.getServiceComponents().isEmpty(),
                    new SvcValidationException(localizedMessageBuilder, SERVICE_CANNOT_BE_DELETED_HAS_COMPONENT, service.getServiceId()));

            // Orphan Service Tags and Codes are not removed automatically by Hibernate
            serviceTagRepository.deleteOrphansByService(service.getServiceId());
            serviceActivationRepository.deleteOrphansByService(service.getServiceId());

            serviceAccess.getServiceComponents().forEach(serviceComponent -> {
                // Orphan Service Tags and Codes are not removed automatically by Hibernate
                serviceTagRepository.deleteOrphansByService(serviceComponent.getServiceId());
                serviceActivationRepository.deleteOrphansByService(serviceComponent.getServiceId());
            });

            serviceAccessRepository.deleteById(serviceAccess.getServiceId());
            //delete audit
            serviceRepository.deleteServiceAccessAuditWhereServiceIdIn(List.of(service.getServiceId()));
            serviceRepository.deleteServiceAuditWhereServiceIdIn(List.of(service.getServiceId()));
        }

        if (COMPONENT.equals(service.getServiceCategory())) {
            // Orphan Service Tags and Codes are not removed automatically by Hibernate
            serviceTagRepository.deleteOrphansByService(service.getServiceId());
            serviceActivationRepository.deleteOrphansByService(service.getServiceId());

            serviceComponentRepository.deleteById(service.getServiceId());
            //delete audit
            serviceRepository.deleteServiceComponentAuditWhereServiceIdIn(List.of(service.getServiceId()));
            serviceRepository.deleteServiceAuditWhereServiceIdIn(List.of(service.getServiceId()));
        }
    }

    public ServiceDTO rollBackAddRequest(SearchServiceDTO requestDTO) {
        var service = requestToService(requestDTO);
        if (service.getActionRequest() != null && !ServiceActionRequest.ACTIVATION.equals(service.getActionRequest())) {
            if (!ServiceActionStatus.COMPLETED.equals(service.getServiceActionStatus()) && !ServiceActionStatus.CANCELED.equals(service.getServiceActionStatus())) {
                service.setActionRequest(null);
                service.setServiceActionStatus(null);
                service.setActionPlanned(null);
                service.setActionStart(null);
                service.setActionEnd(null);
                service.setPlannedDate(null);
                service.setServiceOrderId(null);
                service.setDateRequest(null);
            } else {
                log.warn(localizedMessageBuilder.getLocalizedMessage(SERVICE_NO_CURRENT_REQUEST, service.getServiceId(), service.getServiceActionStatus(), service.getActionRequest()));
            }
        } else {
            log.warn(localizedMessageBuilder.getLocalizedMessage(SERVICE_ACTION_NOT_ACTIVATION, service.getActionRequest()));
        }

        serviceRepository.save(service);
        return servicesResourceAssembler.toModel(service);
    }

    public Collection<Event> findEventsForState(Long serviceId) {
        var service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_NOT_FOUND_ID, serviceId));
        return serviceStatusChanger.getAvailableEventsWithId(String.valueOf(service.getServiceId()), service.getStatus());
    }

    public void deleteAllTagsOnService(Long serviceId) {
        var service = serviceRepository.findById(serviceId)//
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_NOT_FOUND_ID, serviceId));

        serviceTagRepository.deleteAllByService(service);
        serviceActivationRepository.deleteAllByServiceId(service);
    }

    /**
     * Retrieve services declaring a link to non-existent activation codes or provisioning tags
     */
    public List<Long> getOrphans() {
        // Services referring to a non-existent activation code
        List<Long> activationOrphans = serviceRepository.findServicesWithNonExistentActivationCode();
        log.info(String.format("Found %d services having orphan service activations", activationOrphans.size()));

        // Services referring to a non-existent provisioning tag
        List<Long> tagOrphans = serviceRepository.findServicesWithNonExistentProvisioningTag();
        log.info(String.format("Found %d services having orphan service tags", tagOrphans.size()));

        activationOrphans.addAll(tagOrphans);
        return activationOrphans;
    }
}
