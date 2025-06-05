package mc.monacotelecom.services.repository.specification;

import mc.monacotelecom.services.entity.ProvisioningProduct;
import mc.monacotelecom.services.enums.ProvisioningProductActionRequest;
import mc.monacotelecom.services.enums.ProvisioningTagAction;
import mc.monacotelecom.services.enums.ServiceAction;
import org.springframework.data.jpa.domain.Specification;

public final class ProvisioningProductSpecification {
    private ProvisioningProductSpecification() {
    }

    public static Specification<ProvisioningProduct> hasCode(String code) {
        return (root, query, cb) -> cb.equal(root.get("productCode"), code);
    }

    public static Specification<ProvisioningProduct> hasAction(ProvisioningProductActionRequest action) {
        return (root, query, cb) -> cb.equal(root.get("actionRequest"), action);
    }

    public static Specification<ProvisioningProduct> hasTagCode(String tagCode) {
        return (root, query, cb) -> cb.equal(root.get("tagCode"), tagCode);
    }

    public static Specification<ProvisioningProduct> hasTagAction(ProvisioningTagAction tagAction) {
        return (root, query, cb) -> cb.equal(root.get("tagAction"), tagAction.name());
    }

    public static Specification<ProvisioningProduct> hasServiceAction(ServiceAction serviceAction) {
        return (root, query, cb) -> cb.equal(root.get("provisioningAction").get("serviceAction"), serviceAction);
    }
}
