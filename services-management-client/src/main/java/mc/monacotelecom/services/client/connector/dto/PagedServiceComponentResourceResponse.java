package mc.monacotelecom.services.client.connector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedServiceComponentResourceResponse {
    @JsonProperty("_embedded")
    ServiceComponentSearchResponse embedded;
}

