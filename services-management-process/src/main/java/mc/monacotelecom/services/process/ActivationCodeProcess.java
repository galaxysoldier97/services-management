package mc.monacotelecom.services.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.inventory.common.CommonFunctions;
import mc.monacotelecom.inventory.common.exporter.process.ExcelGenerator;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.services.assembler.ActivationCodeResourceAssembler;
import mc.monacotelecom.services.assembler.ServiceActivationResourceAssembler;
import mc.monacotelecom.services.dto.ActivationCodeDTO;
import mc.monacotelecom.services.dto.ServiceActivationDTO;
import mc.monacotelecom.services.dto.request.CreateActivationCodeDTO;
import mc.monacotelecom.services.dto.request.UpdateActivationCodeDTO;
import mc.monacotelecom.services.dto.search.SearchActivationCodeDTO;
import mc.monacotelecom.services.entity.ActivationCode;
import mc.monacotelecom.services.entity.Service;
import mc.monacotelecom.services.entity.ServiceAccess;
import mc.monacotelecom.services.entity.ServiceActivation;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.Status;
import mc.monacotelecom.services.exceptions.SvcNotFoundException;
import mc.monacotelecom.services.exceptions.SvcValidationException;
import mc.monacotelecom.services.importer.data.GenericDataCsvLines.ActivationCodeCsvLine;
import mc.monacotelecom.services.mapper.ExporterEntityMapper;
import mc.monacotelecom.services.repository.ActivationCodeRepository;
import mc.monacotelecom.services.repository.ServiceAccessRepository;
import mc.monacotelecom.services.repository.ServiceActivationRepository;
import mc.monacotelecom.services.repository.specification.ActivationCodeSpecification;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static mc.monacotelecom.services.translation.TranslationMessages.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivationCodeProcess {

    private final ActivationCodeResourceAssembler activationCodeResourceAssembler = ActivationCodeResourceAssembler.of(ActivationCodeProcess.class);
    private final ActivationCodeRepository activationCodeRepository;
    private final ServiceAccessRepository serviceAccessRepository;
    private final ServiceActivationRepository serviceActivationRepository;
    private final ServiceActivationResourceAssembler serviceActivationResourceAssembler = ServiceActivationResourceAssembler.of(ActivationCodeProcess.class);
    private final LocalizedMessageBuilder localizedMessageBuilder;
    private final ExcelGenerator excelGenerator;
    private final ExporterEntityMapper exporterEntityMapper;

    public ActivationCodeDTO getByInternalId(Long id) {
        var activationCode = activationCodeRepository.findByInternalId(id)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, ACTIVATION_CODE_NOT_FOUND_ID, id));
        return activationCodeResourceAssembler.toModel(activationCode);
    }

    public ActivationCodeDTO get(String code) {
        var activationCode = activationCodeRepository.findById(code)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, ACTIVATION_CODE_NOT_FOUND_CODE, code));
        return activationCodeResourceAssembler.toModel(activationCode);
    }

    public void add(CreateActivationCodeDTO dto) {
        if (activationCodeRepository.existsById(dto.getCode())) {
            throw new SvcValidationException(localizedMessageBuilder, ACTIVATION_CODE_ALREADY_EXIST, dto.getCode());
        }

        activationCodeRepository.save(activationCodeResourceAssembler.toEntity(dto));
    }

    public PagedModel<ActivationCodeDTO> getAll(Pageable pageable, PagedResourcesAssembler<ActivationCode> assembler) {
        Page<ActivationCode> activationCodes = activationCodeRepository.findAll(pageable);
        return assembler.toModel(activationCodes, activationCodeResourceAssembler, linkTo(ActivationCodeProcess.class).slash("/").withSelfRel());
    }

    public PagedModel<ActivationCodeDTO> search(final SearchActivationCodeDTO searchActivationCodeDTO, Pageable pageable, PagedResourcesAssembler<ActivationCode> assembler) {
        final Specification<ActivationCode> specification = prepareSpecification(searchActivationCodeDTO);
        Page<ActivationCode> activationCodes = activationCodeRepository.findAll(specification, pageable);

        return assembler.toModel(activationCodes, activationCodeResourceAssembler, linkTo(ActivationCodeProcess.class).slash("/").withSelfRel());
    }

    public Specification<ActivationCode> prepareSpecification(final SearchActivationCodeDTO searchActivationCodeDTO) {
        Specification<ActivationCode> specification = null;

        specification = StringUtils.isNotBlank(searchActivationCodeDTO.getActivCode()) ? Specification.where(ActivationCodeSpecification.hasActivCode(searchActivationCodeDTO.getActivCode())) : specification;
        specification = Objects.nonNull(searchActivationCodeDTO.getNature()) ? CommonFunctions.addSpecification(specification, ActivationCodeSpecification.hasNature(searchActivationCodeDTO.getNature())) : specification;
        specification = Objects.nonNull(searchActivationCodeDTO.getNetworkComponent()) ? CommonFunctions.addSpecification(specification, ActivationCodeSpecification.hasNetworkComponent(searchActivationCodeDTO.getNetworkComponent())) : specification;
        specification = StringUtils.isNotBlank(searchActivationCodeDTO.getProductCode()) ? CommonFunctions.addSpecification(specification, ActivationCodeSpecification.hasProductCodeLike(searchActivationCodeDTO.getProductCode())) : specification;
        specification = StringUtils.isNotBlank(searchActivationCodeDTO.getTagCode()) ? CommonFunctions.addSpecification(specification, ActivationCodeSpecification.hasTagCodeLike(searchActivationCodeDTO.getTagCode())) : specification;

        return specification;
    }

    public PagedModel<ServiceActivationDTO> getAllByAccessType(String accessType, Pageable pageable, PagedResourcesAssembler<ServiceActivation> assembler) {
        List<ServiceAccess> serviceAccesses = serviceAccessRepository.findByAccessTypeAndStatus(Network.valueOf(accessType), Status.ACTIVATED);
        List<Long> ids = serviceAccesses.stream().map(Service::getServiceId).collect(Collectors.toList());
        Page<ServiceActivation> serviceActivations = serviceActivationRepository.findByServiceServiceIdIn(ids, pageable);
        if (serviceActivations.isEmpty()) {
            throw new SvcNotFoundException(localizedMessageBuilder, SERVICE_ACTIVATION_NOT_FOUND);
        }
        return assembler.toModel(serviceActivations, serviceActivationResourceAssembler, linkTo(ActivationCodeProcess.class).slash("/").withSelfRel());
    }

    public PagedModel<ServiceActivationDTO> getAllByTechId(String techId, String accessType, Pageable pageable, PagedResourcesAssembler<ServiceActivation> assembler) {
        final var ignoredServiceStatuses = Arrays.asList(Status.DEACTIVATED, Status.CANCELED);
        var serviceAccess = serviceAccessRepository.findByTechIdAndAccessTypeAndStatusNotIn(techId, Network.valueOf(accessType.toUpperCase()), ignoredServiceStatuses)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_ACCESS_NOT_FOUND_TECH_ID_ACCESS_NOT_STATUS, techId, accessType.toUpperCase(), ignoredServiceStatuses));
        Page<ServiceActivation> serviceActivations = serviceActivationRepository.findByServiceServiceId(serviceAccess.getServiceId(), pageable);
        if (serviceActivations.isEmpty()) {
            throw new SvcNotFoundException(localizedMessageBuilder, SERVICE_ACTIVATION_NOT_FOUND);
        }
        return assembler.toModel(serviceActivations, serviceActivationResourceAssembler, linkTo(ActivationCodeProcess.class).slash("/").withSelfRel());
    }

    public ActivationCodeDTO update(Long id, UpdateActivationCodeDTO dto) {
        var activationCode = activationCodeRepository.findByInternalId(id)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, ACTIVATION_CODE_NOT_FOUND_ID, id));

        final BiConsumer<UpdateActivationCodeDTO, ActivationCode> patcher = (dto1, code1) -> {
            Optional.ofNullable(dto1.getNature()).ifPresent(code1::setNature);
            Optional.ofNullable(dto1.getDescription()).ifPresent(code1::setDescription);
            Optional.ofNullable(dto1.getNetworkComponent()).ifPresent(code1::setNetworkComponent);
        };

        patcher.accept(dto, activationCode);

        return activationCodeResourceAssembler.toModel(activationCodeRepository.save(activationCode));
    }

    public void delete(final Long id, final boolean force) {
        var activationCode = activationCodeRepository.findByInternalId(id)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, ACTIVATION_CODE_NOT_FOUND_ID, id));

        if (!activationCode.getTagActivations().isEmpty()) {
            log.warn(String.format("Deleting activation code '%s' having tag activations", activationCode.getActivCode()));
        }

        if (!activationCode.getServiceActivations().isEmpty() && !force) {
            throw new SvcValidationException(localizedMessageBuilder, ACTIVATION_CODE_NOT_DELETABLE_SERVICES, activationCode.getActivCode());
        }

        activationCodeRepository.delete(activationCode);
    }

    /**
     * Retrieve activation codes not declared in the provisioning model but expected by existing services
     */
    public List<String> getOrphans() {
        return activationCodeRepository.findMissingCodes();
    }

    public ByteArrayInputStream export() {
        final var toExport = activationCodeRepository.findAll()
                .stream().flatMap(exporterEntityMapper::toActivationCodeExporter);
        return excelGenerator.writeToExcel(toExport, ActivationCodeCsvLine.class, "export.activationCode");
    }
}
