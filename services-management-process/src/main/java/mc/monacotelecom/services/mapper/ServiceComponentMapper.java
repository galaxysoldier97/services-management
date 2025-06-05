package mc.monacotelecom.services.mapper;

import mc.monacotelecom.services.dto.ServiceComponentDTO;
import mc.monacotelecom.services.entity.ServiceComponent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(uses = {
        ServiceMapper.class,
        ServiceParameterMapper.class
})
public interface ServiceComponentMapper {

    ServiceComponentMapper INSTANCE = Mappers.getMapper(ServiceComponentMapper.class);

    @Mapping(target = "serviceAccessId", source = "entity.serviceAccess.serviceId")
    ServiceComponentDTO toDto(ServiceComponent entity);

    ServiceComponent toEntity(ServiceComponentDTO serviceToUpdate);
}
