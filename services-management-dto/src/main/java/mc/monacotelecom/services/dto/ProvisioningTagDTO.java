package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ProvisioningTagActivity;
import mc.monacotelecom.services.enums.ProvisioningTagNature;
import mc.monacotelecom.services.enums.ServiceCategory;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@Relation(collectionRelation = "provisioningtags")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProvisioningTagDTO extends RepresentationModel<ProvisioningTagDTO> {

    private Long tagId;
    @Schema(description = "Provisioning tag code", example = "HAXACC")
    private String tagCode;
    private String description;
    private ProvisioningTagActivity activity;
    private String componentType;
    private ServiceCategory category;
    private Network accessType;
    private ProvisioningTagNature nature;
    @Schema(description = "Whether the tag association with a service should be persisted in database")
    private Boolean persistent;
    private List<TagParameterDTO> tagParameters;
}
