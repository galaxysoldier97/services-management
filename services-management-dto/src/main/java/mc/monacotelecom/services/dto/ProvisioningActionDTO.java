package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mc.monacotelecom.services.enums.ProvisioningTagAction;
import mc.monacotelecom.services.enums.ServiceAction;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@Relation(collectionRelation = "provisioningactions")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProvisioningActionDTO extends RepresentationModel<ProvisioningActionDTO> {

    private Long tagActionId;
    private ProvisioningTagDTO provisioningTag;
    private ProvisioningTagAction tagAction;
    private List<ProvisioningActionParameterDTO> provActionParameters;
    private ServiceAction serviceAction;
}
