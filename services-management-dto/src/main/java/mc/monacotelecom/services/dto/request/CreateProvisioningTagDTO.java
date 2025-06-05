package mc.monacotelecom.services.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ProvisioningTagActivity;
import mc.monacotelecom.services.enums.ProvisioningTagNature;
import mc.monacotelecom.services.enums.ServiceCategory;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateProvisioningTagDTO {

    @NotNull
    @Schema(description = "Provisioning tag code", example = "HAXACC")
    private String tagCode;

    @NotNull
    private ProvisioningTagNature nature;

    @NotNull
    private ProvisioningTagActivity activity;

    private String description;

    private String componentType;

    private ServiceCategory category;

    private Network accessType;

    private Boolean persistent;
}
