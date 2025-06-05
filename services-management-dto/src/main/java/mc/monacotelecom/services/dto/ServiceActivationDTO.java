package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode(callSuper = false)
@Data
@Relation(collectionRelation = "serviceActivations")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceActivationDTO extends RepresentationModel<ServiceActivationDTO> {

    private ActivationCodeDTO activCode;
    private Long activValue;
}
