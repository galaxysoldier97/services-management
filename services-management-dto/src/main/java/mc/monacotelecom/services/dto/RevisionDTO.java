package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Data
@Relation(collectionRelation = "revisions")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RevisionDTO<T> extends RepresentationModel<RevisionDTO<T>> {

    LocalDateTime date;

    T entity;

    String author;
}
