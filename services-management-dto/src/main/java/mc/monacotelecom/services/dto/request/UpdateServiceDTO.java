package mc.monacotelecom.services.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mc.monacotelecom.services.enums.EquipmentCategory;
import mc.monacotelecom.services.enums.ServiceUpdateAction;

import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateServiceDTO {

    @Schema(description = "Action to apply on the service, describing what fields should be updated")
    private ServiceUpdateAction action;

    @Schema(description = "List of actions to apply on the service, describing what fields should be updated")
    private List<ServiceUpdateAction> actions = new ArrayList<>();

    @Schema(description = "For Service Access only, ID of the target parent service access")
    private Long parentServiceId;

    @Schema(description = "For Service Component only, ID of the related service access")
    private Long accessServiceId;

    @Schema(description = "For Service Access only, identifier of the equipment")
    private Long equipmentId;

    @Schema(description = "For Service Access only, category of the equipment")
    private EquipmentCategory equipmentCategory;

    @Schema(description = "Related phone number")
    private String number;

    @Schema(description = "Activity of the related phone number")
    private String activityNumber;

    @Schema(description = "For Service Access only, identifier of the access point")
    private String accessPointId;

    @Schema(description = "CRM Identifier")
    private String crmServiceId;

    @Schema(description = "Customer Number")
    private Long customerNo;

    @Schema(description = "Identifier of the main range number")
    private Long mainRangeId;

    @Schema(description = "Optical Network Termination ID", example = "BKR01:1-1-1-2-4")
    @Pattern(regexp = ".*:[0-9]+-[0-9]+-[0-9]+-[0-9]+-[0-9]+", message = "ONT ID should follow this kind of format: BKR01:1-1-1-2-4")
    private String ontId;

    @Schema(description = "Technical identifier")
    private String techId;
}
