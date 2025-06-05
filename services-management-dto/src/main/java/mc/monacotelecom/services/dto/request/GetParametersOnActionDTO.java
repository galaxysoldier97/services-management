package mc.monacotelecom.services.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import mc.monacotelecom.services.enums.ProvisioningTagAction;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetParametersOnActionDTO {

    List<ParametersOnActionDTO> parametersOnActionDTO;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ParametersOnActionDTO {

        @NotEmpty
        private String tagCode;

        @NotNull
        private ProvisioningTagAction tagAction;
    }

}
