package mc.monacotelecom.services.mapper;

import mc.monacotelecom.services.dto.ServiceParameterDTO;
import mc.monacotelecom.services.entity.ServiceParameter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceParameterMapper {

    @Mapping(target = "technicalParameter.parameterType", source = "technicalParameter.parameterType")
    @Mapping(target = "technicalParameter.parameterCode", source = "technicalParameter.parameterCode")
    @Mapping(target = "technicalParameter.internalId", source = "technicalParameter.parameterId")
    ServiceParameter toEntity(ServiceParameterDTO dto);

    @Mapping(target = "technicalParameter.parameterType", source = "technicalParameter.parameterType")
    @Mapping(target = "technicalParameter.parameterCode", source = "technicalParameter.parameterCode")
    @Mapping(target = "technicalParameter.parameterId", source = "technicalParameter.internalId")
    ServiceParameterDTO toDto(ServiceParameter entity);
}
