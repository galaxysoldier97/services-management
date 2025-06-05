package mc.monacotelecom.services.dto.search;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import mc.monacotelecom.services.enums.ProvisioningProductActionRequest;
import mc.monacotelecom.services.enums.ProvisioningTagAction;
import mc.monacotelecom.services.enums.ServiceAction;

@Data
public class SearchProvisioningProductDTO {

    @Schema(description = "Product Code")
    private String code;

    @Schema(description = "Product Action")
    private ProvisioningProductActionRequest action;

    @Schema(description = "Provisioning Tag Code")
    private String tagCode;

    @Schema(description = "Provisioning Tag Action")
    private ProvisioningTagAction tagAction;

    @Schema(description = "Service Action")
    private ServiceAction serviceAction;

}
