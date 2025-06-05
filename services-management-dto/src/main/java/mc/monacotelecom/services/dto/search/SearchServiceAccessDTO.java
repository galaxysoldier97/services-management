package mc.monacotelecom.services.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mc.monacotelecom.services.enums.Network;

import javax.validation.constraints.Pattern;

@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SearchServiceAccessDTO extends BaseSearchServiceDTO {

    @Schema(description = "Identifier of the parent service access, for service component only", example = "1")
    private Long parentServiceId;

    @Schema(description = "Equipment ID, for service access only", example = "2013")
    private Long equipmentId;

    @Schema(description = "Access Point Identifier, for service access only", example = "46P2-0001-1-1")
    private String accessPointId;

    @Schema(description = "Type of the Service access, for service access only")
    private Network accessType;

    @Schema(description = "Optical Network Termination ID", example = "BKR01:1-1-1-2-4")
    @Pattern(regexp = ".*:[0-9]+-[0-9]+-[0-9]+-[0-9]+-[0-9]+", message = "ONT ID should follow this kind of format: BKR01:1-1-1-2-4")
    private String ontId;
}
