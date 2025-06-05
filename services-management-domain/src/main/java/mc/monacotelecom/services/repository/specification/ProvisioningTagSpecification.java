package mc.monacotelecom.services.repository.specification;

import mc.monacotelecom.services.entity.ProvisioningTag;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ProvisioningTagActivity;
import mc.monacotelecom.services.enums.ProvisioningTagNature;
import mc.monacotelecom.services.enums.ServiceCategory;
import org.springframework.data.jpa.domain.Specification;

import static mc.monacotelecom.services.entity.Constants.ACTIVITY;
import static mc.monacotelecom.services.entity.Constants.CATEGORY;

public final class ProvisioningTagSpecification {
    private ProvisioningTagSpecification() {
    }

    public static Specification<ProvisioningTag> hasTagCode(String tagCode) {
        return (root, query, cb) -> cb.equal(root.get("tagCode"), tagCode);
    }

    public static Specification<ProvisioningTag> hasNature(ProvisioningTagNature nature) {
        return (root, query, cb) -> cb.equal(root.get("nature"), nature);
    }

    public static Specification<ProvisioningTag> hasComponentType(String componentType){
        return (root, query, cb) -> cb.equal(root.get("componentType"), componentType);
    }

    public static Specification<ProvisioningTag> hasActivity(ProvisioningTagActivity activity) {
        return (root, query, cb) -> cb.equal(root.get(ACTIVITY), activity);
    }

    public static Specification<ProvisioningTag> hasAccessType(Network accessType) {
        return (root, query, cb) -> cb.equal(root.get("accessType"), accessType);
    }

    public static Specification<ProvisioningTag> hasCategory(ServiceCategory category) {
        return (root, query, cb) -> cb.equal(root.get(CATEGORY), category);
    }

    public static Specification<ProvisioningTag> isPersistent(Boolean persistent) {
        return (root, query, cb) -> cb.equal(root.get("persistent"), persistent);
    }
}
