package mc.monacotelecom.services.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mc.monacotelecom.services.enums.ProvisioningTagAction;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateProvisioningActionDTO extends UpdateProvisioningActionDTO {

    @NotNull
    @Schema(description = "Provisioning Tag internal ID", example = "12")
    private Long tagId;

    @NotNull
    @Schema(description = "Action on tag", example = "PURCHASE")
    private ProvisioningTagAction tagAction;
}
