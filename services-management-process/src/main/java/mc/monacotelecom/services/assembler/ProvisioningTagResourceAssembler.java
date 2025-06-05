package mc.monacotelecom.services.assembler;

import mc.monacotelecom.services.dto.ProvisioningTagDTO;
import mc.monacotelecom.services.dto.request.CreateProvisioningTagDTO;
import mc.monacotelecom.services.entity.ProvisioningTag;
import mc.monacotelecom.services.mapper.ProvisioningTagMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class ProvisioningTagResourceAssembler extends RepresentationModelAssemblerSupport<ProvisioningTag, ProvisioningTagDTO> {

    private ProvisioningTagResourceAssembler(Class<?> controllerClass) {
        super(controllerClass, ProvisioningTagDTO.class);
    }

    public static ProvisioningTagResourceAssembler of(Class<?> controllerClass) {
        return new ProvisioningTagResourceAssembler(controllerClass);
    }

    @Override
    public ProvisioningTagDTO toModel(ProvisioningTag entity) {
        return ProvisioningTagMapper.INSTANCE.toDto(entity);
    }

    public ProvisioningTag toEntity(ProvisioningTagDTO provisioningTagDTO) {
        return ProvisioningTagMapper.INSTANCE.toEntity(provisioningTagDTO);
    }

    public ProvisioningTag toEntity(CreateProvisioningTagDTO provisioningTagDTO) {
        return ProvisioningTagMapper.INSTANCE.toEntity(provisioningTagDTO);
    }
}