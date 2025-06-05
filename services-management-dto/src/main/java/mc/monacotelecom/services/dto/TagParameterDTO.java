package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TagParameterDTO {

    private String code;

    private String description;

    private String paramValue;

}
