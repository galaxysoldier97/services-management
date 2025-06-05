package mc.monacotelecom.services.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mc.monacotelecom.services.enums.ProvisioningTagAction;
import mc.monacotelecom.services.enums.TechnicalParameterType;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "provisioning_action_parameter")
@IdClass(ProvisioningActionParameterKey.class)
public class ProvisioningActionParameter implements Serializable {

    @Id
    @Column(name = "tag_code", nullable = false, updatable = false, insertable = false)
    private String tagCode;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "tag_action", nullable = false, updatable = false, insertable = false)
    private ProvisioningTagAction tagAction;

    @Id
    @Column(name = "parameter_code", nullable = false, updatable = false, insertable = false)
    private String parameterCode;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "parameter_type", nullable = false, updatable = false, insertable = false)
    private TechnicalParameterType parameterType;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_code", referencedColumnName = "tag_code", nullable = false)
    @JoinColumn(name = "tag_action", referencedColumnName = "tag_action", nullable = false)
    private ProvisioningAction provisioningAction;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parameter_code", referencedColumnName = "parameter_code", nullable = false)
    @JoinColumn(name = "parameter_type", referencedColumnName = "parameter_type", nullable = false)
    private TechnicalParameter technicalParameter;

    @Column(name = "parameter_value", length = 80, columnDefinition = "varchar(80)")
    private String parameterValue;
}
