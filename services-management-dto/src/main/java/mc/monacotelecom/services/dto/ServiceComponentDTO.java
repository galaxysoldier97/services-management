package mc.monacotelecom.services.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode(callSuper = true)
@Data
@Relation(collectionRelation = "servicecomponents")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceComponentDTO extends ServiceDTO {

    @Schema(description = "Identifier of the related service access", example = "1")
    private Long serviceAccessId ;

    @Schema(description = "Type of the service component")
    private String componentType;

    @Schema(description = "Technical ID")
    private String techId;
}
