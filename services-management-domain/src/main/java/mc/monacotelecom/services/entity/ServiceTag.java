package mc.monacotelecom.services.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_tag")
@IdClass(ServiceTagKey.class)
public class ServiceTag implements Serializable {

    @Column(name = "tag_value", columnDefinition = "varchar(255)")
    private Long tagValue;

    @Id
    @Column(name = "tag_code", nullable = false, updatable = false, insertable = false)
    private String tagCode;

    @Id
    @Column(name = "service_id", nullable = false, updatable = false, insertable = false)
    private Long serviceId;

    // Ignore warning at startup about the LAZY fetchType being overriden, it's important to keep it that way.
    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "tag_code", referencedColumnName = "tag_code", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private ProvisioningTag provisioningTag;

    @MapsId
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "service_id", referencedColumnName = "id", nullable = false)
    private Service service;
}
