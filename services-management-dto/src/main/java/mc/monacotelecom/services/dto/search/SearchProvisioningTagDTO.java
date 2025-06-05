package mc.monacotelecom.services.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ProvisioningTagActivity;
import mc.monacotelecom.services.enums.ProvisioningTagNature;
import mc.monacotelecom.services.enums.ServiceCategory;

@Data
public class SearchProvisioningTagDTO {

    @Schema(description = "Tag Code")
    private String tagCode;

    @Schema(description = "Network Access type")
    private Network accessType;

    @Schema(description = "Component type")
    private String componentType;

    @Schema(description = "Provisioning Tag Nature")
    private ProvisioningTagNature nature;

    @Schema(description = "Provisioning Tag activity")
    private ProvisioningTagActivity activity;

    @Schema(description = "Provisioning Tag category")
    private ServiceCategory category;

    @Schema(description = "Whether the tag association with a service should be persisted in database")
    private Boolean persistent;
}
