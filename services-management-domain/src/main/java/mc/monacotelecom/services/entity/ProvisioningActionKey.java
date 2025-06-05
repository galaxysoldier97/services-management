package mc.monacotelecom.services.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mc.monacotelecom.services.enums.ProvisioningTagAction;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvisioningActionKey implements Serializable {
    private String tagCode;
    private ProvisioningTagAction tagAction;
}
