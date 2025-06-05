package mc.monacotelecom.services.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mc.monacotelecom.services.enums.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static mc.monacotelecom.services.entity.Constants.STATUS;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Audited
@Entity
@Table(name = "service", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"service_activity", "crm_service_id", "service_category"})
})
@Inheritance(strategy = InheritanceType.JOINED)
public class Service implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @Length(max = 64)
    @Column(name = "crm_service_id", columnDefinition = "varchar(64)")
    private String crmServiceId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "service_category", columnDefinition = "enum('ACCESS', 'COMPONENT')")
    private ServiceCategory serviceCategory;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "service_activity", columnDefinition = "enum('MOBILE', 'INTERNET', 'SIP', 'TV', 'NDD', 'MAILPRO', 'MAIL', 'WIFI', 'VOIP', 'MEVO', 'TELEPHONY')")
    private ServiceActivity serviceActivity;

    @Column(name = "subscription_id")
    private Long subscriptionId;

    @Length(max = 32)
    @Column(name = "number", columnDefinition = "varchar(32)", unique = true)
    private String number;

    @Column(name = "main_range_id", unique = true)
    private Long mainRangeId;

    @CreationTimestamp
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "activation_date")
    private LocalDateTime activationDate;

    @Column(name = "deactivation_date")
    private LocalDateTime deactivationDate;

    @Column(name = "suspension_date")
    private LocalDateTime suspensionDate;

    @Column(name = "resumed_date")
    private LocalDateTime resumedDate;

    @Column(name = "cancellation_date")
    private LocalDateTime cancellationDate;

    @Column(name = "barring_date")
    private LocalDateTime barringDate;

    @Enumerated(EnumType.STRING)
    @Column(name = STATUS, nullable = false, columnDefinition = "enum('PENDING', 'ACTIVATED', 'DEACTIVATED', 'SUSPENDED', 'CANCELED', 'BARRED')")
    private Status status;

    @Length(max = 40)
    @Column(name = "service_order_id", columnDefinition = "varchar(40)")
    private String serviceOrderId;

    @Column(name = "customer_no", columnDefinition = "bigint(20)")
    private Long customerNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_request", columnDefinition = "enum('ACTIVATION', 'SUSP_OPER', 'BARRING', 'UNBARRING', 'DEACTIVATION', 'RESUM_OPER')")
    private ServiceActionRequest actionRequest;

    @Length(max = 80)
    @Column(name = "reason_request", columnDefinition = "varchar(80)")
    private String reasonRequest;

    @Column(name = "date_request")
    private LocalDateTime dateRequest;

    @Column(name = "action_planned")
    private Boolean actionPlanned;

    @Column(name = "planned_date")
    private LocalDateTime plannedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_status", columnDefinition = "enum('PENDING', 'CANCELED', 'COMPLETED')")
    private ServiceActionStatus serviceActionStatus;

    @Column(name = "action_start")
    private LocalDateTime actionStart;

    @Column(name = "action_end")
    private LocalDateTime actionEnd;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    @NotAudited
    @OneToMany(fetch = LAZY, mappedBy = "service", cascade = CascadeType.ALL)
    private List<ServiceParameter> serviceParameters = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    @NotAudited
    @OneToMany(fetch = LAZY, mappedBy = "service", cascade = CascadeType.ALL)
    private List<ServiceActivation> serviceActivations = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    @NotAudited
    @OneToMany(fetch = LAZY, mappedBy = "service", cascade = CascadeType.ALL)
    private List<ServiceTag> serviceTags = new ArrayList<>();

    @JsonProperty
    public Boolean getOnGoingAction() {
        return !(getActionRequest() == null || ServiceActionStatus.COMPLETED.equals(getServiceActionStatus()) || ServiceActionStatus.CANCELED.equals(getServiceActionStatus()));
    }
}
