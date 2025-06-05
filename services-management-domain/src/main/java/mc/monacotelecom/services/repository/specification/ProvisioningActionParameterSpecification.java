package mc.monacotelecom.services.repository.specification;

import mc.monacotelecom.services.entity.ProvisioningActionParameter;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ProvisioningTagActivity;
import mc.monacotelecom.services.enums.ProvisioningTagNature;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static mc.monacotelecom.services.entity.Constants.ACTIVITY;


public final class ProvisioningActionParameterSpecification {

    public static final String PROVISIONING_ACTION = "provisioningAction";

    private ProvisioningActionParameterSpecification() {
    }

    public static Specification<ProvisioningActionParameter> hasNature(ProvisioningTagNature nature) {
        return (root, query, cb) -> cb.equal(root.get(PROVISIONING_ACTION).get("tag").get("nature"), nature );
    }

    public static Specification<ProvisioningActionParameter> hasActivity(ProvisioningTagActivity activity) {
        return (root, query, cb) -> cb.equal(root.get(PROVISIONING_ACTION).get("tag").get(ACTIVITY), activity );
    }

    public static Specification<ProvisioningActionParameter> hasAccessType(Network accessType) {
        return (root, query, cb) -> cb.equal(root.get(PROVISIONING_ACTION).get("tag").get("accessType"), accessType );
    }

    public static Specification<ProvisioningActionParameter> hasTagCode(List<String> tagCodes) {
        return (root, query, builder) -> root.get(PROVISIONING_ACTION).get("tag").get("tagCode").in(tagCodes);
    }
}
