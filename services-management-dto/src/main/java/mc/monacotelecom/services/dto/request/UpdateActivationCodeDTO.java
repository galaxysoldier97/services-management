package mc.monacotelecom.services.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import mc.monacotelecom.services.enums.ActivationNature;
import mc.monacotelecom.services.enums.NetworkComponent;
import org.hibernate.validator.constraints.Length;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateActivationCodeDTO {

    @Length(max = 255)
    private String description;

    private ActivationNature nature;

    private NetworkComponent networkComponent;
}

