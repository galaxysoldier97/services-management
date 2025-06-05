package mc.monacotelecom.services.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddTagActivationCodeDTO {

    @NotNull
    private Long activId;

    @NotNull
    private Long tagId;

    private Long tagValue;
}
