package mc.monacotelecom.services.entity;

import lombok.*;
import mc.monacotelecom.inventory.common.importer.domain.entity.IEntity;
import mc.monacotelecom.services.enums.ActivationNature;
import mc.monacotelecom.services.enums.NetworkComponent;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode
@Table(name = "activation_code")
@NamedEntityGraph(name = "ActivationCode.tagActivations", attributeNodes = @NamedAttributeNode("tagActivations"))
public class ActivationCode extends Provisioning implements Serializable, IEntity {

    @Column(name = "id", nullable = false, columnDefinition = "bigint auto_increment")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    @Id
    @Column(name = "activ_code", unique = true, nullable = false, columnDefinition = "varchar(255)")
    private String activCode;

    @Column(name = "description", columnDefinition = "varchar(255)")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "nature", nullable = false, columnDefinition = "enum('PROFILE', 'OPTION', 'BUCKET', 'BOOSTER', 'BARRING', 'LOCK','FORWARDING','CONFIG')")
    private ActivationNature nature;

    @Enumerated(EnumType.STRING)
    @Column(name = "network_component", nullable = false, columnDefinition = "enum('TVCAS', 'ZATTOO', 'PCRF', 'SPG', 'SCP', 'DISE', 'OLT', 'CS', 'STREAMWIDE', 'SDP', 'OPI')")
    private NetworkComponent networkComponent;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "activationCode", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<TagActivation> tagActivations;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "activationCode", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ServiceActivation> serviceActivations;

    @Override
    public String getDatabaseId() {
        return String.valueOf(internalId);
    }

    @Override
    public ActivationCode getInstance() {
        return this;
    }
}
