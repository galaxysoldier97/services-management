package mc.monacotelecom.services.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import mc.monacotelecom.services.enums.ProvisioningTagAction;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateProvisioningProductDTO {

    @NotNull
    private String parentTagCode;

    @NotNull
    private ProvisioningTagAction parentTagAction;
}
