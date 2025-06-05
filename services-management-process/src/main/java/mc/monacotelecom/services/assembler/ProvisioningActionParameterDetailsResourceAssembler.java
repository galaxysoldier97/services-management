package mc.monacotelecom.services.assembler;

import mc.monacotelecom.services.dto.ProvisioningActionDTO;
import mc.monacotelecom.services.dto.ProvisioningActionParameterDetailsDTO;
import mc.monacotelecom.services.entity.ProvisioningActionParameter;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class ProvisioningActionParameterDetailsResourceAssembler extends RepresentationModelAssemblerSupport<ProvisioningActionParameter, ProvisioningActionParameterDetailsDTO> {

    private final TechnicalParameterResourceAssembler technicalParameterResourceAssembler;
    private final ProvisioningActionResourceAssembler provisioningActionResourceAssembler;

    private ProvisioningActionParameterDetailsResourceAssembler(Class<?> controllerCLass) {
        super(controllerCLass, ProvisioningActionParameterDetailsDTO.class);
        this.technicalParameterResourceAssembler = TechnicalParameterResourceAssembler.of(controllerCLass);
        this.provisioningActionResourceAssembler = ProvisioningActionResourceAssembler.of(controllerCLass);
    }

    public static ProvisioningActionParameterDetailsResourceAssembler of(Class<?> controllerClass) {
        return new ProvisioningActionParameterDetailsResourceAssembler(controllerClass);
    }

    @Override
    public ProvisioningActionParameterDetailsDTO toModel(ProvisioningActionParameter entity) {
        ProvisioningActionParameterDetailsDTO dto = new ProvisioningActionParameterDetailsDTO();
        ProvisioningActionDTO provisioningActionDTO = provisioningActionResourceAssembler.toModel(entity.getProvisioningAction());
        dto.setTechnicalParameter(technicalParameterResourceAssembler.toModel(entity.getTechnicalParameter()));
        dto.setProvisioningActionDTO(provisioningActionDTO);
        dto.setParameterValue(entity.getParameterValue());

        return dto;
    }
}
