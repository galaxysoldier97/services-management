package mc.monacotelecom.services.dto.search;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ProvisioningTagActivity;
import mc.monacotelecom.services.enums.ProvisioningTagNature;

import java.util.List;

@Data
public class SearchProvisioningActionDTO {

    @Schema(description = "Tag Code of the provisioning Action parameters, could be multiples")
    private List<String> tagCodes;

    @Schema(description = "Network Access type of the provisioning Action parameters")
    private Network accessType;

    @Schema(description = "Provisioning Tag Nature of the provisioning Action parameters")
    private ProvisioningTagNature nature;

    @Schema(description = "Provisioning Tag activity of the provisioning Action parameters")
    private ProvisioningTagActivity activity;
}
