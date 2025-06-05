package mc.monacotelecom.services.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Audited
@Entity
@Table(name = "service_component")
public class ServiceComponent extends Service {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "access_service_id", referencedColumnName = "id")
    private ServiceAccess serviceAccess;

    @Column(name = "component_type", columnDefinition = "varchar(30)")
    private String componentType;

    @Column(name = "tech_id", length = 40, columnDefinition = "VARCHAR(40)")
    private String techId;
}
