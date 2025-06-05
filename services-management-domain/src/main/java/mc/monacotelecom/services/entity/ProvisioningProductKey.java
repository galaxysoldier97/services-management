package mc.monacotelecom.services.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mc.monacotelecom.services.enums.ProvisioningProductActionRequest;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvisioningProductKey implements Serializable {

    private String tagCode;
    private String tagAction;
    private String productCode;
    private ProvisioningProductActionRequest actionRequest;
}
