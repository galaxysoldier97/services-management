package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParametersOnActionResponseDTO {

    List<ProvisioningActionParameterDTO> provisioningActionParameterDTO;
}
