package mc.monacotelecom.services.assembler;

import mc.monacotelecom.services.dto.ServiceDTO;
import mc.monacotelecom.services.entity.Service;
import mc.monacotelecom.services.mapper.ServiceMapper;

public class ServicesResourceAssembler extends AbstractServicesResourceAssembler<Service, ServiceDTO> {

    private ServicesResourceAssembler(Class<?> controllerClass) {
        super(controllerClass, ServiceDTO.class);
    }

    public static ServicesResourceAssembler of(Class<?> controllerClass) {
        return new ServicesResourceAssembler(controllerClass);
    }

    @Override
    public ServiceDTO toModel(Service entity) {
        return ServiceMapper.INSTANCE.toDto(entity);
    }


    public Service toEntity(ServiceDTO serviceToUpdate) {
        return  ServiceMapper.INSTANCE.toEntity(serviceToUpdate);
    }
}