package mc.monacotelecom.services.mapper;

import mc.monacotelecom.services.dto.ProvisioningProductDTO;
import mc.monacotelecom.services.entity.ProvisioningProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProvisioningProductMapper {

    ProvisioningProductMapper INSTANCE = Mappers.getMapper(ProvisioningProductMapper.class);

    @Mapping(target = "productAction", source = "actionRequest")
    @Mapping(target = "productCode", source = "productCode")
    @Mapping(target = "provisioningProductId", source = "internalId")
    ProvisioningProductDTO toDto(ProvisioningProduct provisioningProduct);

    @Mapping(target = "actionRequest", source = "productAction")
    @Mapping(target = "productCode", source = "productCode")
    @Mapping(target = "internalId", source = "provisioningProductId")
    ProvisioningProduct toEntity(ProvisioningProductDTO provisioningProductDTO);
}
