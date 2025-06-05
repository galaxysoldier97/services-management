package mc.monacotelecom.services.assembler;

import mc.monacotelecom.services.dto.ServiceAccessDTO;
import mc.monacotelecom.services.dto.unm.ServiceUnmDTO;
import mc.monacotelecom.services.entity.ServiceAccess;
import mc.monacotelecom.services.enums.Status;
import mc.monacotelecom.services.enums.unm.CustomerServiceStatus;
import mc.monacotelecom.services.mapper.ServiceAccessMapper;

import java.util.List;

public class ServiceAccessResourceAssembler extends AbstractServicesResourceAssembler<ServiceAccess, ServiceAccessDTO> {

    private ServiceAccessResourceAssembler(Class<?> controllerClass) {
        super(controllerClass, ServiceAccessDTO.class);
    }

    public static ServiceAccessResourceAssembler of(Class<?> controllerClass) {
        return new ServiceAccessResourceAssembler(controllerClass);
    }

    @Override
    public ServiceAccessDTO toModel(ServiceAccess entity) {
        return ServiceAccessMapper.INSTANCE.toDto(entity);
    }

    public ServiceUnmDTO toUnmResource(ServiceAccess service) {
        ServiceUnmDTO serviceUnmDTO = new ServiceUnmDTO();
        serviceUnmDTO.setActivity(service.getServiceActivity());
        serviceUnmDTO.setIdentifier(service.getCrmServiceId());
        if (service.getStatus() != null && List.of(Status.ACTIVATED, Status.BARRED, Status.SUSPENDED).contains(service.getStatus())) {
            serviceUnmDTO.setStatus(CustomerServiceStatus.ONLINE);
        } else {
            serviceUnmDTO.setStatus(CustomerServiceStatus.OFFLINE);
        }
        serviceUnmDTO.setAccessId(service.getAccessPointId());
        serviceUnmDTO.setServiceTechnicalId(service.getServiceId());

        return serviceUnmDTO;
    }

    public ServiceAccess toEntity(ServiceAccessDTO serviceToUpdate) {
        return  ServiceAccessMapper.INSTANCE.toEntity(serviceToUpdate);
    }
}