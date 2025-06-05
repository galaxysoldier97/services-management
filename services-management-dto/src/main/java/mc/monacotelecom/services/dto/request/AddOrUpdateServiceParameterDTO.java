package mc.monacotelecom.services.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddOrUpdateServiceParameterDTO {

    @NotNull
    private String value;

    @NotNull
    @Schema(description = "Parameter Code of the related Technical parameter", example = "PARAM1")
    private String technicalParameterCode;
}
