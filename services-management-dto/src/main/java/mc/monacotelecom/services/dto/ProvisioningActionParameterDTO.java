package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = false)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProvisioningActionParameterDTO extends RepresentationModel<ProvisioningActionParameterDTO> {

    TechnicalParameterDTO technicalParameter;
    String parameterValue;
}
