package mc.monacotelecom.services.assembler;

import mc.monacotelecom.services.dto.ServiceActivationDTO;
import mc.monacotelecom.services.entity.ServiceActivation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class ServiceActivationResourceAssembler extends RepresentationModelAssemblerSupport<ServiceActivation, ServiceActivationDTO> {

    private final ActivationCodeResourceAssembler activationCodeResourceAssembler;

    private ServiceActivationResourceAssembler(Class<?> controllerClass) {
        super(controllerClass, ServiceActivationDTO.class);
        this.activationCodeResourceAssembler = ActivationCodeResourceAssembler.of(controllerClass);
    }

    public static ServiceActivationResourceAssembler of(Class<?> controllerClass) {
        return new ServiceActivationResourceAssembler(controllerClass);
    }

    @Override
    public ServiceActivationDTO toModel(ServiceActivation entity) {
        ServiceActivationDTO serviceActivationDTO = new ServiceActivationDTO();
        serviceActivationDTO.setActivCode(activationCodeResourceAssembler.toModel(entity.getActivationCode()));
        serviceActivationDTO.setActivValue(entity.getActivValue());
        return serviceActivationDTO;
    }
}