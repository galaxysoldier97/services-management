package mc.monacotelecom.services.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import mc.monacotelecom.services.enums.ActivationNature;
import mc.monacotelecom.services.enums.NetworkComponent;

@Data
public class SearchActivationCodeDTO {

    @Schema(description = "Identifier", example = "GPRSLOCK")
    private String activCode;

    @Schema(description = "Nature")
    private ActivationNature nature;

    @Schema(description = "Network Component")
    private NetworkComponent networkComponent;

    @Schema(description = "Code of the related provisioning product", example = "M2M_LOW")
    private String productCode;

    @Schema(description = "Code of the related provisioning tag", example = "HAXACC")
    private String tagCode;
}
