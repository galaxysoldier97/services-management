package mc.monacotelecom.services.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import mc.monacotelecom.inventory.common.importer.domain.entity.IEntity;
import mc.monacotelecom.services.enums.TechnicalParameterType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "technical_parameter")
@NamedEntityGraph(name = "TechnicalParameter.provisioningActionParameters", attributeNodes = @NamedAttributeNode("provisioningActionParameters"))
@IdClass(TechnicalParameterKey.class)
public class TechnicalParameter extends Provisioning implements IEntity, Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint auto_increment")
    private Long internalId;

    @Id
    @Column(name = "parameter_code", nullable = false, columnDefinition = "varchar(255)")
    private String parameterCode;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "parameter_type", nullable = false, columnDefinition = "enum('CONTEXT', 'STATIC')")
    private TechnicalParameterType parameterType;

    @Column(name = "description", columnDefinition = "tinytext")
    private String description;

    @OneToMany(mappedBy = "technicalParameter", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ServiceParameter> serviceParameters;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "technicalParameter", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProvisioningActionParameter> provisioningActionParameters;

    @Override
    public String getDatabaseId() {
        return parameterCode + parameterType;
    }

    @Override
    public TechnicalParameter getInstance() {
        return this;
    }
}
