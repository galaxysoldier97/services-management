package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mc.monacotelecom.services.enums.EquipmentCategory;
import mc.monacotelecom.services.enums.Network;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.Pattern;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Relation(collectionRelation = "serviceaccesses")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceAccessDTO extends ServiceDTO {

    @Schema(description = "Type of the Service access")
    private Network accessType;

    @Schema(description = "Access Point Identifier", example = "46P2-0001-1-1")
    private String accessPointId;

    @Schema(description = "Parent service access")
    private ServiceAccessDTO parentServiceAccess;

    @Schema(description = "Equipment ID", example = "2013")
    private Long equipmentId;

    @Schema(description = "Equipment category")
    private EquipmentCategory equipmentCategory;

    @Schema(description = "Related service components")
    private List<ServiceComponentDTO> serviceComponents;

    @Schema(description = "Optical Network Termination ID", example = "BKR01:1-1-1-2-4")
    @Pattern(regexp = ".*:[0-9]+-[0-9]+-[0-9]+-[0-9]+-[0-9]+", message = "ONT ID should follow this kind of format: BKR01:1-1-1-2-4")
    private String ontId;

    @Schema(description = "Technical ID")
    private String techId;
}
