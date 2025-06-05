package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import mc.monacotelecom.services.enums.TagAction;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TagActionDTO {

    @NotEmpty
    private String tagCode;

    @NotNull
    private TagAction tagAction;

}
