package mc.monacotelecom.services.client.connector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mc.monacotelecom.services.dto.ServiceComponentDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceComponentSearchResponse{
    @JsonProperty("servicecomponents")
    List<ServiceComponentDTO> serviceComponents;
}
