package mc.monacotelecom.services.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import mc.monacotelecom.inventory.common.importer.domain.entity.IEntity;
import mc.monacotelecom.services.enums.ProvisioningTagAction;
import mc.monacotelecom.services.enums.ServiceAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "provisioning_action")
@IdClass(ProvisioningActionKey.class)
public class ProvisioningAction extends Provisioning implements IEntity, Serializable {
    public ProvisioningAction(String tagCode, ProvisioningTagAction tagAction) {
        this.tagCode = tagCode;
        this.tagAction = tagAction;
    }

    @Column(name = "id", nullable = false, columnDefinition = "bigint auto_increment")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    @Id
    @Column(name = "tag_code", nullable = false, updatable = false, insertable = false)
    private String tagCode;

    @MapsId
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_code", referencedColumnName = "tag_code", nullable = false)
    private ProvisioningTag tag;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "tag_action", columnDefinition = "enum('PURCHASE', 'CANCEL', 'MIGRATION', 'TRANSITION', 'TRANSFER')", nullable = false)
    private ProvisioningTagAction tagAction;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_action", columnDefinition = "enum('ACTIVATION', 'MIGRATION', 'DEACTIVATION', 'CHGE_ADDR', 'CHGE_CUST', 'BARRING', 'UNBARRING', 'SUSP_CLI', 'RESUM_CLI', 'SUSP_OPE', 'RESUM_OPE')")
    private ServiceAction serviceAction;

    @OneToMany(mappedBy = "provisioningAction", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ProvisioningProduct> provisioningProducts = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "provisioningAction", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ProvisioningActionParameter> provisioningActionParameters;

    @Override
    public String getDatabaseId() {
        return String.valueOf(internalId);
    }

    @Override
    public ProvisioningAction getInstance() {
        return this;
    }
}
