package mc.monacotelecom.services.assembler;

import mc.monacotelecom.services.dto.ProvisioningActionDTO;
import mc.monacotelecom.services.dto.ProvisioningProductDTO;
import mc.monacotelecom.services.entity.ProvisioningProduct;
import mc.monacotelecom.services.mapper.ProvisioningProductMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class ProvisioningProductResourceAssembler extends RepresentationModelAssemblerSupport<ProvisioningProduct, ProvisioningProductDTO> {

    private final ProvisioningActionResourceAssembler provisioningActionResourceAssembler;

    private ProvisioningProductResourceAssembler(Class<?> controllerClass) {
        super(controllerClass, ProvisioningProductDTO.class);
        this.provisioningActionResourceAssembler = ProvisioningActionResourceAssembler.of(controllerClass);
    }

    public static ProvisioningProductResourceAssembler of(Class<?> controllerCLass) {
        return new ProvisioningProductResourceAssembler(controllerCLass);
    }

    @Override
    public ProvisioningProductDTO toModel(ProvisioningProduct entity) {
        ProvisioningProductDTO provisioningProductDTO = ProvisioningProductMapper.INSTANCE.toDto(entity);
        ProvisioningActionDTO provisioningActionDTO = provisioningActionResourceAssembler.toModel(entity.getProvisioningAction());
        if (entity.getProvisioningAction() != null) {
            provisioningActionDTO.setTagAction(entity.getProvisioningAction().getTagAction());
        }

        provisioningProductDTO.setProvisioningProductId(entity.getInternalId());
        provisioningProductDTO.setProductAction(entity.getActionRequest());
        provisioningProductDTO.setProvAction(provisioningActionDTO);

        return provisioningProductDTO;
    }

    public ProvisioningProduct toEntity(ProvisioningProductDTO provisioningProductDTO) {
        return ProvisioningProductMapper.INSTANCE.toEntity(provisioningProductDTO);
    }
}
