package mc.monacotelecom.services.repository.specification;

import mc.monacotelecom.services.entity.TagActivation;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ProvisioningTagActivity;
import mc.monacotelecom.services.enums.ProvisioningTagNature;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static mc.monacotelecom.services.entity.Constants.ACTIVITY;


public final class TagActivationSpecification {

    public static final String PROVISIONING_TAG = "provisioningTag";

    private TagActivationSpecification() {
    }

    public static Specification<TagActivation> hasNature(ProvisioningTagNature nature) {
        return (root, query, cb) -> cb.equal(root.get(PROVISIONING_TAG).get("nature"), nature );
    }

    public static Specification<TagActivation> hasActivity(ProvisioningTagActivity activity) {
        return (root, query, cb) -> cb.equal(root.get(PROVISIONING_TAG).get(ACTIVITY), activity );
    }

    public static Specification<TagActivation> hasAccessType(Network accessType) {
        return (root, query, cb) -> cb.equal(root.get(PROVISIONING_TAG).get("accessType"), accessType );
    }

    public static Specification<TagActivation> hasTagCode(List<String> tagCodes){
        return (root, query, builder) -> root.get(PROVISIONING_TAG).get("tagCode").in(tagCodes);
    }
}
