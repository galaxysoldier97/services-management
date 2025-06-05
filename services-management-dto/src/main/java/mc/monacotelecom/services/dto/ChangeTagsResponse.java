package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeTagsResponse {
    List<ProvisioningTagDTO> serviceTags = new ArrayList<>();
    List<ServiceActivationDTO> serviceActivations = new ArrayList<>();
}
