package mc.monacotelecom.services.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import mc.monacotelecom.inventory.common.importer.domain.entity.IEntity;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ProvisioningTagActivity;
import mc.monacotelecom.services.enums.ProvisioningTagNature;
import mc.monacotelecom.services.enums.ServiceCategory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

import static mc.monacotelecom.services.entity.Constants.ACTIVITY;
import static mc.monacotelecom.services.entity.Constants.CATEGORY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "provisioning_tag")
public class ProvisioningTag extends Provisioning implements Serializable, IEntity {

    @Column(name = "id", nullable = false, columnDefinition = "bigint auto_increment")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    @Id
    @Column(name = "tag_code", unique = true, nullable = false, columnDefinition = "varchar(255)")
    private String tagCode;

    @Column(name = "description", columnDefinition = "tinytext")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = ACTIVITY, nullable = false, columnDefinition = "enum('MOBILE', 'INTERNET', 'TV', 'TELEPHONY', 'NDD', 'MEVO', 'OX')")
    private ProvisioningTagActivity activity;

    @Enumerated(EnumType.STRING)
    @Column(name = CATEGORY, columnDefinition = "enum('ACCESS','COMPONENT')")
    private ServiceCategory category;

    @Column(name = "component_type", columnDefinition = "varchar(30)")
    private String componentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_type", columnDefinition = "ENUM('DOCSIS', 'EMTA', 'FTTH', 'FIREWALL', 'STB_HD', 'STB_ANDROID', 'STB_STICKTV', 'STB_ONT', 'STB_DTH', 'EXTENSOR_WIFI', 'EXTENSOR_WIFI_PLUS', 'EXTENSOR_WIFI_B2B', 'ALARMS', 'CENTRAL_TELEFONICA', 'SONDAS_DE_MONITOREO', 'MOBILE','FREEDHOME','ZATTOO','BBHB')")
    private Network accessType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "nature", nullable = false, columnDefinition = "enum('A', 'C', 'P', 'O', 'E', 'R', 'U')")
    private ProvisioningTagNature nature;

    @Column(name = "persistent", columnDefinition = "bit(1)")
    private Boolean persistent;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "provisioningTag", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<TagActivation> tagActivations;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "provisioningTag", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ServiceTag> serviceTags;

    @OneToOne(mappedBy = "parentTag", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private ProvisioningTag childTag;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="parent_tag_code", referencedColumnName = "tag_code")
    @JsonIgnore
    private ProvisioningTag parentTag;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ProvisioningAction> provisioningActions;

    @Override
    public String getDatabaseId() {
        return String.valueOf(internalId);
    }

    @Override
    public ProvisioningTag getInstance() {
        return this;
    }
}
