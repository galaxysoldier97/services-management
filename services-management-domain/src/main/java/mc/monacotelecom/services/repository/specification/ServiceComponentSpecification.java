package mc.monacotelecom.services.repository.specification;

import mc.monacotelecom.services.entity.ServiceComponent;
import org.springframework.data.jpa.domain.Specification;

public final class ServiceComponentSpecification extends ServiceSpecification {

    public static Specification<ServiceComponent> hasAccessServiceId(Long accessServiceId) {
        return (root, query, cb) -> cb.equal(root.get("serviceAccess").get("serviceId"), accessServiceId);
    }

    public static Specification<ServiceComponent> hasAccessCrmServiceId(String accessCrmServiceId) {
        return (root, query, cb) -> cb.equal(root.get("serviceAccess").get("crmServiceId"), accessCrmServiceId);
    }

    public static Specification<ServiceComponent> hasComponentType(String componentType) {
        return (root, query, cb) -> cb.equal(root.get("componentType"), componentType);
    }

    public static Specification<ServiceComponent> hasTechId(final String techId) {
        return (root, query, cb) -> cb.equal(root.get("techId"), techId);
    }
}
