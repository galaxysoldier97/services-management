package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mc.monacotelecom.services.enums.ActivationNature;
import mc.monacotelecom.services.enums.NetworkComponent;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Set;

@EqualsAndHashCode(callSuper = false)
@Data
@Relation(collectionRelation = "activationcodes")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivationCodeDTO extends RepresentationModel<ActivationCodeDTO> {

    private long activationCodeId;

    private String code;

    private String description;

    private ActivationNature nature;

    private NetworkComponent networkComponent;

    private Set<String> tagCodes;
}

