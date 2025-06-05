package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Relation(collectionRelation = "tagActivationCodeDetailsDToes")
public class TagActivationCodeDTO extends RepresentationModel<TagActivationCodeDTO> {

    ActivationCodeDTO activationCode;
    ProvisioningTagDTO provisioningTag;
    Long tagValue;
}
