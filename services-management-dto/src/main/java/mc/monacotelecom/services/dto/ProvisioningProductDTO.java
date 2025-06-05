package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mc.monacotelecom.services.enums.ProvisioningProductActionRequest;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode(callSuper = false)
@Data
@Relation(collectionRelation = "provisioningproducts")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProvisioningProductDTO extends RepresentationModel<ProvisioningProductDTO> {

    private Long provisioningProductId;
    private String productCode;
    private ProvisioningProductActionRequest productAction;
    private ProvisioningActionDTO provAction;
}
