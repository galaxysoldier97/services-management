package mc.monacotelecom.services.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ServiceCategory;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SearchServiceDTO  extends BaseSearchServiceDTO {

    @Schema(description = "Identifier of the parent service access, for service component only", example = "1")
    private Long parentServiceId;

    @Schema(description = "Equipment ID, for service access only", example = "2013")
    private Long equipmentId;

    @Schema(description = "Access Point Identifier, for service access only", example = "46P2-0001-1-1")
    private String accessPointId;

    @Schema(description = "Type of the Service access, for service access only")
    private Network accessType;

    @Schema(description = "Type of the service component, for service component only")
    private String componentType;

    @Schema(description = "Internal ID of the related service access, for service component only", example = "1")
    private Long accessServiceId;

    @Schema(description = "Optical Network Termination ID", example = "BKR01:1-1-1-2-4")
    @Pattern(regexp = ".*:[0-9]+-[0-9]+-[0-9]+-[0-9]+-[0-9]+", message = "ONT ID should follow this kind of format: BKR01:1-1-1-2-4")

    private String ontId;

    private ServiceCategory serviceCategory;
}
