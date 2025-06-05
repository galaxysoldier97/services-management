package mc.monacotelecom.services.dto.search;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class SearchServiceComponentDTO extends BaseSearchServiceDTO {

    @Schema(description = "Type of the service component, for service component only")
    private String componentType;

    @Schema(description = "Internal ID of the related service access, for service component only", example = "1")
    private Long accessServiceId;
}
