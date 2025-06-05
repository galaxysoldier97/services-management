package mc.monacotelecom.services.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mc.monacotelecom.inventory.common.recycling.entities.BaseJobConfiguration;
import mc.monacotelecom.services.enums.JobRecyclingOperation;
import mc.monacotelecom.services.enums.ServiceActivity;
import mc.monacotelecom.services.enums.ServiceCategory;
import mc.monacotelecom.services.enums.Status;

import javax.persistence.*;

import static mc.monacotelecom.services.entity.Constants.*;

@Entity
@Table(name = "job_configuration")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobConfiguration extends BaseJobConfiguration<JobRecyclingOperation> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "operation", columnDefinition = "enum('SERVICES_PURGE')", nullable = false)
    @Enumerated(EnumType.STRING)
    private JobRecyclingOperation operation;

    @Column(name = "days", columnDefinition = "int(11)", nullable = false)
    private long days;

    @Column(name = "enabled", columnDefinition = "bit(1)", nullable = false)
    private boolean enabled;

    @Enumerated(EnumType.STRING)
    @Column(name = CATEGORY, columnDefinition = "enum('ACCESS', 'COMPONENT')")
    private ServiceCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = STATUS, nullable = false, columnDefinition = "enum('PENDING', 'ACTIVATED', 'DEACTIVATED', 'SUSPENDED', 'CANCELED', 'BARRED')")
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = ACTIVITY, columnDefinition = "enum('MOBILE', 'INTERNET', 'SIP', 'TV', 'NDD', 'MAILPRO', 'MAIL', 'WIFI', 'VOIP', 'MEVO', 'TELEPHONY')")
    private ServiceActivity activity;
}
