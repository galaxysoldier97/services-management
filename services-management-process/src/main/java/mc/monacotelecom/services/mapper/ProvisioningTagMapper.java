package mc.monacotelecom.services.mapper;

import mc.monacotelecom.services.dto.ProvisioningTagDTO;
import mc.monacotelecom.services.dto.request.CreateProvisioningTagDTO;
import mc.monacotelecom.services.entity.ProvisioningTag;
import mc.monacotelecom.services.importer.data.ProvisioningLines;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProvisioningTagMapper {

    ProvisioningTagMapper INSTANCE = Mappers.getMapper(ProvisioningTagMapper.class);

    @Mapping(target = "tagId", source = "internalId")
    ProvisioningTagDTO toDto(ProvisioningTag provisioningTag);

    @Mapping(target = "internalId", source = "tagId")
    ProvisioningTag toEntity(ProvisioningTagDTO provisioningTagDTO);

    ProvisioningTag toEntity(CreateProvisioningTagDTO provisioningTagDTO);

    @Mapping(target = "persistent", source = "persistent", qualifiedByName = "parsePersistent")
    ProvisioningTag mapProvisioningTag(ProvisioningLines.ProvisioningTagCsvLine line);

    @Named("parsePersistent")
    static boolean parsePersistent(final String value){
        return StringUtils.isNotBlank(value) && value.equals("O");
    }
}
