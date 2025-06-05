package mc.monacotelecom.services.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mc.monacotelecom.inventory.common.importer.domain.entity.IEntity;
import mc.monacotelecom.services.enums.ProvisioningProductActionRequest;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "provisioning_product")
@IdClass(ProvisioningProductKey.class)
public class ProvisioningProduct extends Provisioning implements IEntity, Serializable {

    @Column(name = "id", columnDefinition = "bigint auto_increment", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    @Id
    @Column(name = "product_code", nullable = false, columnDefinition = "varchar(255)")
    private String productCode;

    @Id
    @Column(name = "tag_code", nullable = false, columnDefinition = "varchar(255)")
    private String tagCode;

    @Id
    @Column(name = "tag_action", nullable = false, columnDefinition = "varchar(255)")
    private String tagAction;

    @MapsId
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tag_code", referencedColumnName = "tag_code", nullable = false)
    @JoinColumn(name = "tag_action", referencedColumnName = "tag_action", nullable = false)
    private ProvisioningAction provisioningAction;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "action_request", columnDefinition = "enum('FIRST_ACTIVATION', 'ACTIVATION', 'TRANSITION', 'MIGRATION', 'DEACTIVATION', 'CHGE_NUM', 'CHGE_EQT', 'RETURN_EQT', 'CHGE_ADDR','CHGE_CLI', 'BARRING', 'UNBARRING', 'SUSP_CLI', 'RESUM_CLI', 'SUSP_OPER', 'RESUM_OPER', 'RESET_CELL', 'RETURN_ANCIL', 'CHGE_ANCIL', 'TRANSFER', 'ADD_QTY', 'DEL_QTY', 'CHGE_MSISDN', 'CHGE_OFFER')", nullable = false)
    private ProvisioningProductActionRequest actionRequest;

    @Override
    public String getDatabaseId() {
        return String.valueOf(internalId);
    }

    @Override
    public ProvisioningProduct getInstance() {
        return this;
    }
}
