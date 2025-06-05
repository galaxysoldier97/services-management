package mc.monacotelecom.services.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ProvisioningTagActivity;
import mc.monacotelecom.services.enums.ProvisioningTagNature;
import mc.monacotelecom.services.enums.ServiceCategory;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateProvisioningTagDTO {

    @NotNull
    private ProvisioningTagNature nature;

    @NotNull
    private ProvisioningTagActivity activity;

    private String description;

    private ServiceCategory category;

    private Network accessType;

    private String componentType;

    private Boolean persistent;
}
