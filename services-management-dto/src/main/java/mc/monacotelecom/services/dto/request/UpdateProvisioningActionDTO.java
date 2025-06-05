package mc.monacotelecom.services.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import mc.monacotelecom.services.enums.ServiceAction;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateProvisioningActionDTO {

    @Schema(description = "Action on service", example = "ACTIVATION")
    private ServiceAction serviceAction;
}
