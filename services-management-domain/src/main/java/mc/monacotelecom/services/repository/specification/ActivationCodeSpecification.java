package mc.monacotelecom.services.repository.specification;

import mc.monacotelecom.services.entity.ActivationCode;
import mc.monacotelecom.services.entity.TagActivation;
import mc.monacotelecom.services.enums.ActivationNature;
import mc.monacotelecom.services.enums.NetworkComponent;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;

public final class ActivationCodeSpecification {
    private ActivationCodeSpecification() {}

    public static Specification<ActivationCode> hasActivCode(String activCode) {
        return (root, query, cb) -> cb.equal(root.get("activCode"), activCode);
    }

    public static Specification<ActivationCode> hasNetworkComponent(NetworkComponent networkComponent) {
        return (root, query, cb) -> cb.equal(root.get("networkComponent"), networkComponent);
    }

    public static Specification<ActivationCode> hasNature(ActivationNature nature) {
        return (root, query, cb) -> cb.equal(root.get("nature"), nature);
    }

    public static Specification<ActivationCode> hasProductCodeLike(String productCode) {
        return (root, query, cb) -> {
            Join<ActivationCode, TagActivation> activationCodeProvisioningTags = root.join("tagActivations")
                    .join("provisioningTag")
                    .join("provisioningActions")
                    .join("provisioningProducts");
            query.distinct(true);
            return cb.like(activationCodeProvisioningTags.get("productCode"), "%" + productCode + "%");
        };
    }

    public static Specification<ActivationCode> hasTagCodeLike(String tagCode) {
        return (root, query, cb) -> {
            Join<ActivationCode, TagActivation> activationCodeProvisioningTags = root.join("tagActivations")
                    .join("provisioningTag");
            query.distinct(true);
            return cb.like(activationCodeProvisioningTags.get("tagCode"), "%" + tagCode + "%");
        };
    }
}
