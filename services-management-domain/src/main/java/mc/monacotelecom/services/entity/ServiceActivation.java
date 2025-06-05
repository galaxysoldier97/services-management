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
@Table(name = "service_activation")
@IdClass(ServiceActivationKey.class)
public class ServiceActivation implements Serializable {

    @Column(name = "activ_value", columnDefinition = "bigint(20)")
    private Long activValue;

    @Id
    @Column(name = "activ_code", nullable = false, updatable = false, insertable = false)
    private String activCode;

    @Id
    @Column(name = "service_id", nullable = false, updatable = false, insertable = false)
    private Long serviceId;

    // Ignore warning at startup about the LAZY fetchType being overriden, it's important to keep it that way.
    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "activ_code", referencedColumnName = "activ_code", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private ActivationCode activationCode;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "service_id", referencedColumnName = "id", nullable = false)
    private Service service;
}
