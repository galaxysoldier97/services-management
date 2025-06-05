package mc.monacotelecom.services.assembler;

import mc.monacotelecom.services.dto.ProvisioningActionParameterDTO;
import mc.monacotelecom.services.dto.TechnicalParameterDTO;
import mc.monacotelecom.services.entity.ProvisioningActionParameter;
import mc.monacotelecom.services.mapper.ProvisioningActionParameterMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class ProvisioningActionParameterResourceAssembler extends RepresentationModelAssemblerSupport<ProvisioningActionParameter, ProvisioningActionParameterDTO> {

    private final TechnicalParameterResourceAssembler technicalParameterResourceAssembler;

    private ProvisioningActionParameterResourceAssembler(Class<?> controllerCLass) {
        super(controllerCLass, ProvisioningActionParameterDTO.class);
        this.technicalParameterResourceAssembler = TechnicalParameterResourceAssembler.of(controllerCLass);
    }

    public static ProvisioningActionParameterResourceAssembler of(Class<?> controllerCLass) {
        return new ProvisioningActionParameterResourceAssembler(controllerCLass);
    }

    @Override
    public ProvisioningActionParameterDTO toModel(ProvisioningActionParameter entity) {
        ProvisioningActionParameterDTO dto = new ProvisioningActionParameterDTO();
        TechnicalParameterDTO technicalParameterDTO = technicalParameterResourceAssembler.toModel(entity.getTechnicalParameter());
        dto.setTechnicalParameter(technicalParameterDTO);
        dto.setParameterValue(entity.getParameterValue());
        return dto;
    }

    public ProvisioningActionParameter toEntity(ProvisioningActionParameterDTO provisioningActionParameterDTO) {
        return ProvisioningActionParameterMapper.INSTANCE.toEntity(provisioningActionParameterDTO);
    }
}