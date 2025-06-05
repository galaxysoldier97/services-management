package mc.monacotelecom.services.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SearchTechnicalParamDTO {

    @Schema(description = "Identifier of the technical parameter")
    private String code;
}
