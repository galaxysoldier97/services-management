package mc.monacotelecom.services.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateTechnicalParameterDTO {

    @Length(max = 255)
    private String description;
}
