package mc.monacotelecom.services.mapper;

import mc.monacotelecom.services.dto.ServiceAccessDTO;
import mc.monacotelecom.services.entity.ServiceAccess;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(uses = {
        ServiceMapper.class,
        ServiceParameterMapper.class,
        ServiceComponentMapper.class,
})
public interface ServiceAccessMapper {
    ServiceAccessMapper INSTANCE = Mappers.getMapper(ServiceAccessMapper.class);

    @Mapping(target = "parentServiceAccess", source = "parentService")
    ServiceAccessDTO toDto(ServiceAccess entity);

    ServiceAccess toEntity(ServiceAccessDTO serviceToUpdate);
}
