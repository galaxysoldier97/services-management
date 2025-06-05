package mc.monacotelecom.services.assembler;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public abstract class AbstractServicesResourceAssembler<T, D extends RepresentationModel<?>> extends RepresentationModelAssemblerSupport<T, D> {

    protected AbstractServicesResourceAssembler(Class<?> controllerClass, Class<D> resourceType) {
        super(controllerClass, resourceType);
    }
}
