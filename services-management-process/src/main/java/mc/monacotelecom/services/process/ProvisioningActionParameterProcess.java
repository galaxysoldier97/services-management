package mc.monacotelecom.services.process;

import lombok.RequiredArgsConstructor;
import mc.monacotelecom.inventory.common.CommonFunctions;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.services.assembler.ProvisioningActionParameterDetailsResourceAssembler;
import mc.monacotelecom.services.assembler.ProvisioningActionParameterResourceAssembler;
import mc.monacotelecom.services.dto.ProvisioningActionParameterDTO;
import mc.monacotelecom.services.dto.ProvisioningActionParameterDetailsDTO;
import mc.monacotelecom.services.dto.request.AddProvisioningActionParameterDTO;
import mc.monacotelecom.services.dto.search.SearchProvisioningActionDTO;
import mc.monacotelecom.services.entity.ProvisioningActionParameter;
import mc.monacotelecom.services.exceptions.SvcNotFoundException;
import mc.monacotelecom.services.repository.ProvisioningActionParameterRepository;
import mc.monacotelecom.services.repository.ProvisioningActionRepository;
import mc.monacotelecom.services.repository.TechnicalParameterRepository;
import mc.monacotelecom.services.repository.specification.ProvisioningActionParameterSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static mc.monacotelecom.services.translation.TranslationMessages.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Component
public class ProvisioningActionParameterProcess {

    private final ProvisioningActionParameterRepository provisioningActionParameterRepository;
    private final TechnicalParameterRepository technicalParameterRepository;
    private final ProvisioningActionRepository provisioningActionRepository;
    private final ProvisioningActionParameterResourceAssembler provisioningActionParameterResourceAssembler = ProvisioningActionParameterResourceAssembler.of(ProvisioningActionParameterProcess.class);
    private final ProvisioningActionParameterDetailsResourceAssembler provisioningActionParameterDetailsResourceAssembler= ProvisioningActionParameterDetailsResourceAssembler.of(ProvisioningActionParameterProcess.class);
    private final LocalizedMessageBuilder localizedMessageBuilder;

    public ProvisioningActionParameterDTO getById(Long provisioningActionId, Long parameterId) {
        var provisioningActionParameter = checkActionAndParameter(provisioningActionId, parameterId);
        return provisioningActionParameterResourceAssembler.toModel(provisioningActionParameter);
    }

    public PagedModel<ProvisioningActionParameterDetailsDTO> search(final SearchProvisioningActionDTO searchProvisioningActionDTO, Pageable pageable, PagedResourcesAssembler<ProvisioningActionParameter> assembler) {
        final Specification<ProvisioningActionParameter> specification = prepareSpecification(searchProvisioningActionDTO);
        Page<ProvisioningActionParameter> tagParameters = provisioningActionParameterRepository.findAll(specification, pageable);
        return assembler.toModel(tagParameters, provisioningActionParameterDetailsResourceAssembler, linkTo(ProvisioningActionParameterProcess.class).slash("/tagactivationcodes").withSelfRel());
    }

    public Specification<ProvisioningActionParameter> prepareSpecification(final SearchProvisioningActionDTO searchProvisioningActionDTO) {
        Specification<ProvisioningActionParameter> specification = null;

        specification = Objects.nonNull(searchProvisioningActionDTO.getTagCodes()) ? Specification.where(ProvisioningActionParameterSpecification.hasTagCode(searchProvisioningActionDTO.getTagCodes())) : specification;
        specification = Objects.nonNull(searchProvisioningActionDTO.getAccessType()) ? CommonFunctions.addSpecification(specification, ProvisioningActionParameterSpecification.hasAccessType(searchProvisioningActionDTO.getAccessType())) : specification;
        specification = Objects.nonNull(searchProvisioningActionDTO.getNature()) ? CommonFunctions.addSpecification(specification, ProvisioningActionParameterSpecification.hasNature(searchProvisioningActionDTO.getNature())) : specification;
        specification = Objects.nonNull(searchProvisioningActionDTO.getActivity()) ? CommonFunctions.addSpecification(specification, ProvisioningActionParameterSpecification.hasActivity(searchProvisioningActionDTO.getActivity())) : specification;

        return specification;
    }

    public PagedModel<ProvisioningActionParameterDetailsDTO> getAll(Pageable pageable, PagedResourcesAssembler<ProvisioningActionParameter> assembler) {
        Page<ProvisioningActionParameter> tagParameters = provisioningActionParameterRepository.findAll(pageable);
        return assembler.toModel(tagParameters, provisioningActionParameterDetailsResourceAssembler, linkTo(ProvisioningActionParameterProcess.class).slash("/").withSelfRel());
    }

    public ProvisioningActionParameterDetailsDTO add(AddProvisioningActionParameterDTO provisioningActionParameterDTO) {
        var provisioningAction = provisioningActionRepository.findByInternalId(provisioningActionParameterDTO.getTagActionId())
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, PROVISIONING_ACTION_NOT_FOUND_ID, provisioningActionParameterDTO.getTagActionId()));
        var technicalParameter = technicalParameterRepository.findByInternalId(provisioningActionParameterDTO.getParameterId())
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, TECHNICAL_PARAMETER_NOT_FOUND_ID, provisioningActionParameterDTO.getParameterId()));
        var provisioningActionParameter = new ProvisioningActionParameter(provisioningAction.getTagCode(), provisioningAction.getTagAction(), technicalParameter.getParameterCode(), technicalParameter.getParameterType(),
                provisioningAction, technicalParameter, provisioningActionParameterDTO.getParameterValue());
        return provisioningActionParameterDetailsResourceAssembler.toModel(provisioningActionParameterRepository.save(provisioningActionParameter));
    }

    public ProvisioningActionParameterDTO add(final Long provisioningActionId, final ProvisioningActionParameterDTO provisioningActionParameterDTO) {
        var provisioningAction = provisioningActionRepository.findByInternalId(provisioningActionId)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, PROVISIONING_ACTION_NOT_FOUND_ID, provisioningActionId));
        var technicalParameter = technicalParameterRepository.findByInternalId(provisioningActionParameterDTO.getTechnicalParameter().getParameterId())
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, TECHNICAL_PARAMETER_NOT_FOUND_ID, provisioningActionParameterDTO.getTechnicalParameter().getParameterId()));
        var provisioningActionParameter = new ProvisioningActionParameter(provisioningAction.getTagCode(), provisioningAction.getTagAction(), technicalParameter.getParameterCode(), technicalParameter.getParameterType(),
                provisioningAction, technicalParameter, provisioningActionParameterDTO.getParameterValue());
        provisioningActionParameterRepository.save(provisioningActionParameter);
        provisioningActionParameter = provisioningActionParameterRepository.findByProvisioningActionInternalIdAndTechnicalParameterInternalId(provisioningActionId, provisioningActionParameter.getTechnicalParameter().getInternalId())
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, PROVISIONING_ACTION_PARAMETER_NOT_FOUND, provisioningActionId, provisioningActionParameterDTO.getTechnicalParameter().getParameterId()));
        return provisioningActionParameterResourceAssembler.toModel(provisioningActionParameter);

    }

    public ProvisioningActionParameterDTO update(Long provisioningActionId, Long parameterId, ProvisioningActionParameterDTO provisioningActionParameterDTO) {
        var provisioningAction = provisioningActionRepository.findByInternalId(provisioningActionId)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, PROVISIONING_ACTION_NOT_FOUND_ID, provisioningActionId));
        var technicalParameter = technicalParameterRepository.findByInternalId(parameterId)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, TECHNICAL_PARAMETER_NOT_FOUND_ID, parameterId));
        var provisioningActionParameter = provisioningActionParameterRepository.findByProvisioningActionInternalIdAndTechnicalParameterInternalId(provisioningActionId, parameterId)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, PROVISIONING_ACTION_PARAMETER_NOT_FOUND, provisioningActionId, parameterId));

        if (provisioningActionParameterDTO.getParameterValue() != null) {
            provisioningActionParameter.setParameterValue(provisioningActionParameterDTO.getParameterValue());
        }
        provisioningActionParameter.setProvisioningAction(provisioningAction);
        provisioningActionParameter.setTechnicalParameter(technicalParameter);
        provisioningActionParameterRepository.save(provisioningActionParameter);
        provisioningActionParameter = provisioningActionParameterRepository.findByProvisioningActionInternalIdAndTechnicalParameterInternalId(provisioningActionId, provisioningActionParameter.getTechnicalParameter().getInternalId())
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, PROVISIONING_ACTION_PARAMETER_NOT_FOUND, provisioningActionId, parameterId));
        return provisioningActionParameterResourceAssembler.toModel(provisioningActionParameter);

    }

    public void delete(Long provisioningActionId, Long parameterId) {
        var provisioningActionParameter = checkActionAndParameter(provisioningActionId, parameterId);
        provisioningActionParameterRepository.delete(provisioningActionParameter);
    }

    private ProvisioningActionParameter checkActionAndParameter(final Long provisioningActionId, final Long parameterId) {
        if (!provisioningActionRepository.existsByInternalId(provisioningActionId)) {
            throw new SvcNotFoundException(localizedMessageBuilder, PROVISIONING_ACTION_NOT_FOUND_ID, provisioningActionId);
        }

        if (!technicalParameterRepository.existsByInternalId(parameterId)) {
            throw new SvcNotFoundException(localizedMessageBuilder, TECHNICAL_PARAMETER_NOT_FOUND_ID, parameterId);
        }

        return provisioningActionParameterRepository.findByProvisioningActionInternalIdAndTechnicalParameterInternalId(provisioningActionId, parameterId)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, PROVISIONING_ACTION_PARAMETER_NOT_FOUND, provisioningActionId, parameterId));
    }
}
