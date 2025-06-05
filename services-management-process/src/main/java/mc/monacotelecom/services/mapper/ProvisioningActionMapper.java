package mc.monacotelecom.services.mapper;

import mc.monacotelecom.services.dto.ProvisioningActionDTO;
import mc.monacotelecom.services.entity.ProvisioningAction;
import mc.monacotelecom.services.importer.data.ProvisioningLines;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProvisioningActionMapper {

    ProvisioningActionMapper INSTANCE = Mappers.getMapper(ProvisioningActionMapper.class);

    @Mapping(target = "tagAction", source = "tagAction")
    @Mapping(target = "tagActionId", source = "internalId")
    ProvisioningActionDTO toDto(ProvisioningAction provisioningAction);

    @Mapping(target = "tagAction", source = "tagAction")
    @Mapping(target = "tagCode", source = "provisioningTag.tagCode")
    @Mapping(target = "internalId", source = "tagActionId")
    @Mapping(target = "tag.internalId", source = "provisioningTag.tagId")
    @Mapping(target = "tag", source = "provisioningTag")
    ProvisioningAction toEntity(ProvisioningActionDTO provisioningActionDTO);

    ProvisioningAction mapProvisioningAction(ProvisioningLines.ProvisioningProductCsvLines line);
}
