package mc.monacotelecom.services.assembler;

import mc.monacotelecom.services.dto.TagActivationCodeDTO;
import mc.monacotelecom.services.entity.TagActivation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class TagActivationCodeResourceAssembler extends RepresentationModelAssemblerSupport<TagActivation, TagActivationCodeDTO> {

    private final ProvisioningTagResourceAssembler provisioningTagResourceAssembler;

    private final ActivationCodeResourceAssembler activationCodeResourceAssembler;

    private TagActivationCodeResourceAssembler(Class<?> controllerClass) {
        super(controllerClass, TagActivationCodeDTO.class);
        this.provisioningTagResourceAssembler = ProvisioningTagResourceAssembler.of(controllerClass);
        this.activationCodeResourceAssembler = ActivationCodeResourceAssembler.of(controllerClass);
    }

    public static TagActivationCodeResourceAssembler of(Class<?> controllerClass) {
        return new TagActivationCodeResourceAssembler(controllerClass);
    }

    @Override
    public TagActivationCodeDTO toModel(TagActivation entity) {
        TagActivationCodeDTO dto = new TagActivationCodeDTO();
        dto.setActivationCode(activationCodeResourceAssembler.toModel(entity.getActivationCode()));
        dto.setProvisioningTag(provisioningTagResourceAssembler.toModel(entity.getProvisioningTag()));
        dto.setTagValue(entity.getTagValue());
        return dto;
    }
}
