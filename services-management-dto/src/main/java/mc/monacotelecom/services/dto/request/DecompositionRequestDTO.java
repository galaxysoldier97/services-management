package mc.monacotelecom.services.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import mc.monacotelecom.services.enums.ProvisioningProductActionRequest;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DecompositionRequestDTO {

    @NotNull
    private List<ProvisioningProductDTO> provisioningProductRequests;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProvisioningProductDTO {
        @NotNull
        @Schema(description = "Product code", example = "PAY_AS_YOU_GO_1")
        private String productCode;

        @NotNull
        private ProvisioningProductActionRequest productAction;
    }
}
