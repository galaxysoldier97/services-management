package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode(callSuper = false)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Relation(collectionRelation = "provisioningActionParameterDetailsDToes")
public class ProvisioningActionParameterDetailsDTO extends RepresentationModel<ProvisioningActionParameterDetailsDTO> {

    ProvisioningActionDTO provisioningActionDTO;
    TechnicalParameterDTO technicalParameter;
    String parameterValue;
}
