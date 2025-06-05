package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceParameterDTO {

    @NotNull
    @Schema(description = "Value of this parameter on this service")
    private String value;

    @NotNull
    @Schema(description = "Related Technical parameter")
    private TechnicalParameterDTO technicalParameter;
}
