package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mc.monacotelecom.services.enums.TechnicalParameterType;
import org.hibernate.validator.constraints.Length;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@Relation(collectionRelation = "technicalParameterDToes")
public class TechnicalParameterDTO extends RepresentationModel<TechnicalParameterDTO> {

    private Long parameterId;

    @Length(max = 30)
    @NotEmpty
    private String parameterCode;

    @Length(max = 40)
    private String description;

    @NotNull
    private TechnicalParameterType parameterType;
}
