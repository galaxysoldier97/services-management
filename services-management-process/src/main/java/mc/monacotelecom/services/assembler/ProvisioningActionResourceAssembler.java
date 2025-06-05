package mc.monacotelecom.services.assembler;

import mc.monacotelecom.services.dto.ProvisioningActionDTO;
import mc.monacotelecom.services.dto.ProvisioningActionParameterDTO;
import mc.monacotelecom.services.dto.ProvisioningTagDTO;
import mc.monacotelecom.services.entity.ProvisioningAction;
import mc.monacotelecom.services.mapper.ProvisioningActionMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import java.util.ArrayList;
import java.util.List;

public class ProvisioningActionResourceAssembler extends RepresentationModelAssemblerSupport<ProvisioningAction, ProvisioningActionDTO> {

    private final ProvisioningTagResourceAssembler provisioningTagResourceAssembler;

    private final ProvisioningActionParameterResourceAssembler provisioningActionParameterResourceAssembler;

    private ProvisioningActionResourceAssembler(Class<?> controllerClass) {
        super(controllerClass, ProvisioningActionDTO.class);
        this.provisioningTagResourceAssembler = ProvisioningTagResourceAssembler.of(controllerClass);
        this.provisioningActionParameterResourceAssembler = ProvisioningActionParameterResourceAssembler.of(controllerClass);
    }

    public static ProvisioningActionResourceAssembler of(Class<?> controllerClass) {
        return new ProvisioningActionResourceAssembler(controllerClass);
    }

    @Override
    public ProvisioningActionDTO toModel(ProvisioningAction entity) {
        if (entity == null) {
            return null;
        }
        ProvisioningActionDTO provisioningActionDTO = ProvisioningActionMapper.INSTANCE.toDto(entity);
        List<ProvisioningActionParameterDTO> provisioningActionParameterDTOs = new ArrayList<>();
        if(entity.getProvisioningActionParameters() != null) {
            entity.getProvisioningActionParameters().forEach(p -> provisioningActionParameterDTOs.add(provisioningActionParameterResourceAssembler.toModel(p)));
        }
        provisioningActionDTO.setProvActionParameters(provisioningActionParameterDTOs);
        ProvisioningTagDTO provisioningTagDTO = provisioningTagResourceAssembler.toModel(entity.getTag());
        provisioningActionDTO.setProvisioningTag(provisioningTagDTO);
        provisioningActionDTO.setTagActionId(entity.getInternalId());
        return provisioningActionDTO;
    }

    public ProvisioningAction toEntity(ProvisioningActionDTO provisioningActionDTO) {
        return ProvisioningActionMapper.INSTANCE.toEntity(provisioningActionDTO);
    }
}

