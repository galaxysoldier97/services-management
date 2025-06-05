package mc.monacotelecom.services.repository.specification;

import mc.monacotelecom.services.entity.ServiceAccess;
import mc.monacotelecom.services.enums.Network;
import org.springframework.data.jpa.domain.Specification;


public class ServiceAccessSpecification extends ServiceSpecification {

    public static Specification<ServiceAccess> hasAccessType(Network accessType) {
        return (root, query, cb) -> cb.equal(root.get("accessType"), accessType);
    }

    public static Specification<ServiceAccess> hasAccessPointId(String accessPointId) {
        return (root, query, cb) -> cb.equal(root.get("accessPointId"), accessPointId);
    }

    public static Specification<ServiceAccess> hasParentServiceId(Long parentServiceId) {
        return (root, query, cb) -> cb.equal(root.get("parentService").get("serviceId"), parentServiceId);
    }

    public static Specification<ServiceAccess> hasEquipmentId(Long equipmentId) {
        return (root, query, cb) -> cb.equal(root.get("equipmentId"), equipmentId);
    }

    public static Specification<ServiceAccess> hasOntId(String ontId) {
        return (root, query, cb) -> cb.equal(root.get("ontId"), ontId);
    }

    public static Specification<ServiceAccess> hasTechId(final String techId) {
        return (root, query, cb) -> cb.equal(root.get("techId"), techId);
    }
}