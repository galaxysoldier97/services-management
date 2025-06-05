package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceTagDTO {

    @Schema(example = "1")
    private Long tagValue;

    private ProvisioningTagDTO provisioningTag;

}
