package mc.monacotelecom.services.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import mc.monacotelecom.services.enums.ActivationNature;
import mc.monacotelecom.services.enums.NetworkComponent;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateActivationCodeDTO {

    @NotNull
    @Schema(description = "Activation code", example = "HOF_227")
    private String code;

    @NotNull
    private ActivationNature nature;

    @NotNull
    private NetworkComponent networkComponent;

    @Length(max = 255)
    private String description;
}

