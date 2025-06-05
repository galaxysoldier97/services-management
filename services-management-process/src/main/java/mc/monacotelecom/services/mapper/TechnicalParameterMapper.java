package mc.monacotelecom.services.mapper;

import mc.monacotelecom.services.dto.TechnicalParameterDTO;
import mc.monacotelecom.services.entity.TechnicalParameter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TechnicalParameterMapper {

    TechnicalParameterMapper INSTANCE = Mappers.getMapper(TechnicalParameterMapper.class);

    @Mapping(target = "parameterCode", source = "parameterCode")
    @Mapping(target = "parameterType", source = "parameterType")
    @Mapping(target = "parameterId", source = "internalId")
    TechnicalParameterDTO toDto(TechnicalParameter tagActivation);


    @Mapping(target = "parameterCode", source = "parameterCode")
    @Mapping(target = "parameterType", source = "parameterType")
    @Mapping(target = "internalId", source = "parameterId")
    TechnicalParameter toEntity(TechnicalParameterDTO technicalParameterDTO);
}
