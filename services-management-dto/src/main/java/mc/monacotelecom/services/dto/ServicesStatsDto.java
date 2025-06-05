package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import mc.monacotelecom.services.enums.Status;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Map;

@Data
@Relation(collectionRelation = "servicesstats")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServicesStatsDto extends RepresentationModel<ServicesStatsDto> {

    Map<Status, Long> statusOfServices;

}
