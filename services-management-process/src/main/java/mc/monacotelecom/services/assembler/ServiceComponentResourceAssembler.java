package mc.monacotelecom.services.assembler;

import mc.monacotelecom.services.dto.ServiceComponentDTO;
import mc.monacotelecom.services.entity.ServiceComponent;
import mc.monacotelecom.services.mapper.ServiceComponentMapper;

public class ServiceComponentResourceAssembler extends AbstractServicesResourceAssembler<ServiceComponent, ServiceComponentDTO> {

    private ServiceComponentResourceAssembler(Class<?> controllerClass) {
        super(controllerClass, ServiceComponentDTO.class);
    }

    public static ServiceComponentResourceAssembler of(Class<?> controllerClass) {
        return new ServiceComponentResourceAssembler(controllerClass);
    }

    @Override
    public ServiceComponentDTO toModel(ServiceComponent entity) {
        return ServiceComponentMapper.INSTANCE.toDto(entity);
    }

    public ServiceComponent toEntity(ServiceComponentDTO serviceToUpdate) {
        return ServiceComponentMapper.INSTANCE.toEntity(serviceToUpdate);
    }
}