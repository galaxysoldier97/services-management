package mc.monacotelecom.services.assembler;

import mc.monacotelecom.services.dto.ActivationCodeDTO;
import mc.monacotelecom.services.dto.request.CreateActivationCodeDTO;
import mc.monacotelecom.services.entity.ActivationCode;
import mc.monacotelecom.services.mapper.ActivationCodeMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class ActivationCodeResourceAssembler extends RepresentationModelAssemblerSupport<ActivationCode, ActivationCodeDTO> {

    private ActivationCodeResourceAssembler(Class<?> controllerCLass) {
        super(controllerCLass, ActivationCodeDTO.class);
    }

    public static ActivationCodeResourceAssembler of(Class<?> controllerClass) {
        return new ActivationCodeResourceAssembler(controllerClass);
    }

    @Override
    public ActivationCodeDTO toModel(ActivationCode entity) {
        return ActivationCodeMapper.INSTANCE.toDto(entity);
    }

    public ActivationCode toEntity(ActivationCodeDTO activationCodeDTO) {
        return ActivationCodeMapper.INSTANCE.toEntity(activationCodeDTO);
    }

    public ActivationCode toEntity(CreateActivationCodeDTO activationCodeDTO) {
        return ActivationCodeMapper.INSTANCE.toEntity(activationCodeDTO);
    }
}