package mc.monacotelecom.services.mapper;

import mc.monacotelecom.services.dto.ServiceActivationDTO;
import mc.monacotelecom.services.dto.ServiceDTO;
import mc.monacotelecom.services.dto.ServiceTagDTO;
import mc.monacotelecom.services.entity.Service;
import mc.monacotelecom.services.entity.ServiceActivation;
import mc.monacotelecom.services.entity.ServiceTag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(uses = {
        ServiceParameterMapper.class,
        ActivationCodeMapper.class,
        ProvisioningTagMapper.class
})
public interface ServiceMapper {

    ServiceMapper INSTANCE = Mappers.getMapper(ServiceMapper.class);

    ServiceDTO toDto(Service entity);

    Service toEntity(ServiceDTO serviceToUpdate);

    @Mapping(target = "provisioningTag", source = "provisioningTag")
    ServiceTagDTO toDto(ServiceTag serviceTag);

    @Mapping(target = "activCode", source = "activationCode")
    ServiceActivationDTO toDto(ServiceActivation serviceTag);

    @Mapping(target = "activCode", ignore = true)
    ServiceActivation toEntity(ServiceActivationDTO serviceTagDTO);

    default List<ServiceTagDTO> toServiceTagList(List<ServiceTag> serviceTags) {
        if (serviceTags == null) {
            return new ArrayList<>();
        }

        List<ServiceTagDTO> serviceTagDTOs = new ArrayList<>(serviceTags.size());
        for (ServiceTag serviceTag : serviceTags) {
            if (serviceTag.getProvisioningTag() != null) {
                serviceTagDTOs.add(toDto(serviceTag));
            }
        }

        return serviceTagDTOs;
    }

    default List<ServiceActivationDTO> toServiceActivationList(List<ServiceActivation> serviceActivations) {
        if (serviceActivations == null) {
            return new ArrayList<>();
        }

        List<ServiceActivationDTO> serviceActivationDTOs = new ArrayList<>(serviceActivations.size());
        for (ServiceActivation serviceActivation : serviceActivations) {
            if (serviceActivation.getActivationCode() != null) {
                serviceActivationDTOs.add(toDto(serviceActivation));
            }
        }

        return serviceActivationDTOs;
    }
}
