package mc.monacotelecom.services.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mc.monacotelecom.services.enums.EquipmentCategory;
import mc.monacotelecom.services.enums.Network;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Audited
@Entity
@Table(name = "service_access", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"equipment_id", "access_point_id"})
})
public class ServiceAccess extends Service {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_service_id", referencedColumnName = "id")
    @ToString.Exclude
    @JsonIgnore
    private ServiceAccess parentService;

    @OneToMany(mappedBy = "parentService", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private List<ServiceAccess> childServiceAccess = new ArrayList<>();

    @Column(name = "equipment_id", columnDefinition = "bigint(20)")
    private Long equipmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_category", columnDefinition = "ENUM('SIMCARD','CPE','ANCILLARY')")
    private EquipmentCategory equipmentCategory;

    @Column(name = "access_point_id", length = 40, columnDefinition = "varchar(40)")
    private String accessPointId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "access_type", columnDefinition = "ENUM('DOCSIS', 'EMTA', 'FTTH', 'FIREWALL', 'STB_HD', 'STB_ANDROID', 'STB_STICKTV', 'STB_ONT', 'STB_DTH', 'EXTENSOR_WIFI', 'EXTENSOR_WIFI_PLUS', 'EXTENSOR_WIFI_B2B', 'ALARMS', 'CENTRAL_TELEFONICA', 'SONDAS_DE_MONITOREO', 'MOBILE' ,'FREEDHOME','ZATTOO','BBHB')")
    private Network accessType;

    @Column(name = "ont_id", length = 30, columnDefinition = "VARCHAR(30)", unique = true)
    private String ontId;

    @Column(name = "tech_id", length = 40, columnDefinition = "VARCHAR(40)")
    private String techId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    @NotAudited
    @OneToMany(mappedBy = "serviceAccess", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ServiceComponent> serviceComponents = new HashSet<>();
}
