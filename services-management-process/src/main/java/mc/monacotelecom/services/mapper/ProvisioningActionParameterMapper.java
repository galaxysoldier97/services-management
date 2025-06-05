package mc.monacotelecom.services.mapper;

import mc.monacotelecom.services.dto.ProvisioningActionParameterDTO;
import mc.monacotelecom.services.entity.ProvisioningActionParameter;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProvisioningActionParameterMapper {

    ProvisioningActionParameterMapper INSTANCE = Mappers.getMapper(ProvisioningActionParameterMapper.class);

    ProvisioningActionParameterDTO toDto(ProvisioningActionParameter provisioningActionParameter);

    ProvisioningActionParameter toEntity(ProvisioningActionParameterDTO provisioningActionParameterDTO);
}
