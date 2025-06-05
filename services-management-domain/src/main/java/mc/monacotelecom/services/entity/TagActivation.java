package mc.monacotelecom.services.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tag_activation")
@IdClass(TagActivationKey.class)
public class TagActivation implements Serializable {

    @Column(name = "tag_value", columnDefinition = "bigint")
    private Long tagValue;

    @Id
    @Column(name = "tag_code", nullable = false, updatable = false, insertable = false)
    private String tagCode;

    @Id
    @Column(name = "activ_code", nullable = false, updatable = false, insertable = false)
    private String actvCode;

    @MapsId
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "tag_code", referencedColumnName = "tag_code", nullable = false)
    private ProvisioningTag provisioningTag;

    @MapsId
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "activ_code", referencedColumnName = "activ_code", nullable = false)
    private ActivationCode activationCode;
}
