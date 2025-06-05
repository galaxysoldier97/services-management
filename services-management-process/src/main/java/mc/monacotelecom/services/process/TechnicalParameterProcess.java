package mc.monacotelecom.services.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.inventory.common.Lambdas;
import mc.monacotelecom.inventory.common.exporter.process.ExcelGenerator;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.services.assembler.TechnicalParameterResourceAssembler;
import mc.monacotelecom.services.dto.TechnicalParameterDTO;
import mc.monacotelecom.services.dto.request.UpdateTechnicalParameterDTO;
import mc.monacotelecom.services.dto.search.SearchTechnicalParamDTO;
import mc.monacotelecom.services.entity.TechnicalParameter;
import mc.monacotelecom.services.entity.TechnicalParameterKey;
import mc.monacotelecom.services.enums.TechnicalParameterType;
import mc.monacotelecom.services.exceptions.SvcNotFoundException;
import mc.monacotelecom.services.exceptions.SvcValidationException;
import mc.monacotelecom.services.importer.data.GenericDataCsvLines.TechnicalParameterCsvLine;
import mc.monacotelecom.services.mapper.ExporterEntityMapper;
import mc.monacotelecom.services.repository.TechnicalParameterRepository;
import mc.monacotelecom.services.repository.specification.TechnicalParameterSpecification;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.stream.Collectors;

import static mc.monacotelecom.services.translation.TranslationMessages.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@Component
@RequiredArgsConstructor
public class TechnicalParameterProcess {

    private final TechnicalParameterRepository technicalParameterRepository;
    private final TechnicalParameterResourceAssembler technicalParameterResourceAssembler = TechnicalParameterResourceAssembler.of(TechnicalParameterProcess.class);
    private final LocalizedMessageBuilder localizedMessageBuilder;
    private final ExcelGenerator excelGenerator;
    private final ExporterEntityMapper exporterEntityMapper;

    public TechnicalParameterDTO getByInternalId(Long id) {
        var technicalParameter = technicalParameterRepository.findByInternalId(id)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, TECHNICAL_PARAMETER_NOT_FOUND_ID, id));
        return technicalParameterResourceAssembler.toModel(technicalParameter);
    }

    public TechnicalParameterDTO get(String code, TechnicalParameterType type) {
        var technicalParameter = technicalParameterRepository.findById(new TechnicalParameterKey(code, type))
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, TECHNICAL_PARAMETER_NOT_FOUND_CODE_TYPE, code, type));
        return technicalParameterResourceAssembler.toModel(technicalParameter);
    }


    public void add(TechnicalParameterDTO dto) {
        if (technicalParameterRepository.existsById(new TechnicalParameterKey(dto.getParameterCode(), dto.getParameterType()))) {
            throw new SvcValidationException(localizedMessageBuilder, TECHNICAL_PARAMETER_EXISTING, dto.getParameterCode(), dto.getParameterType());
        }

        technicalParameterRepository.save(technicalParameterResourceAssembler.toEntity(dto));
    }

    public TechnicalParameterDTO update(Long id, UpdateTechnicalParameterDTO dto) {
        var technicalParameter = technicalParameterRepository.findByInternalId(id)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, TECHNICAL_PARAMETER_NOT_FOUND_ID, id));

        Lambdas.verifyAndApplyString.accept(StringUtils.isNotBlank(dto.getDescription()), technicalParameter::setDescription, dto.getDescription());

        return technicalParameterResourceAssembler.toModel(technicalParameterRepository.save(technicalParameter));
    }

    public PagedModel<TechnicalParameterDTO> search(final SearchTechnicalParamDTO searchTechnicalParamDTO, Pageable pageable, PagedResourcesAssembler<TechnicalParameter> assembler) {
        final Specification<TechnicalParameter> specification = StringUtils.isNotBlank(searchTechnicalParamDTO.getCode()) ? Specification.where(TechnicalParameterSpecification.hasCode(searchTechnicalParamDTO.getCode())) : null;

        Page<TechnicalParameter> technicalParameters = technicalParameterRepository.findAll(specification, pageable);
        return assembler.toModel(technicalParameters, technicalParameterResourceAssembler, linkTo(TechnicalParameterProcess.class).slash("/technicalparameters").withSelfRel());
    }

    public void delete(Long id) {
        var technicalParameter = technicalParameterRepository.findByInternalId(id)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, TECHNICAL_PARAMETER_NOT_FOUND_ID, id));

        if (!technicalParameter.getProvisioningActionParameters().isEmpty()) {
            throw new SvcValidationException(localizedMessageBuilder, TECHNICAL_PARAMETER_NOT_DELETABLE_ACTIONS, id,
                    technicalParameter.getProvisioningActionParameters().stream()
                            .map(provisioningActionParameter -> {
                                var provisioningAction = provisioningActionParameter.getProvisioningAction();
                                return provisioningAction.getTag().getTagCode() + " - " + provisioningAction.getTagAction();
                            })
                            .collect(Collectors.toList()));
        }

        if (!technicalParameter.getServiceParameters().isEmpty()) {
            log.warn(String.format("Deleting technical parameter '%s' referenced by %d services",
                    technicalParameter.getParameterCode(), technicalParameter.getServiceParameters().size()));
        }

        technicalParameterRepository.delete(technicalParameter);
    }

    public PagedModel<TechnicalParameterDTO> getAll(Pageable pageable, PagedResourcesAssembler<TechnicalParameter> assembler) {
        Page<TechnicalParameter> technicalParameters = technicalParameterRepository.findAll(pageable);
        return assembler.toModel(technicalParameters, technicalParameterResourceAssembler, linkTo(TechnicalParameterProcess.class).slash("/").withSelfRel());
    }

    public ByteArrayInputStream export() {
        final var toExport = technicalParameterRepository.findAll()
                .stream().flatMap(exporterEntityMapper::toTechnicalParamsExporter);

        return excelGenerator.writeToExcel(toExport, TechnicalParameterCsvLine.class, "export.technicalParameter");
    }
}
