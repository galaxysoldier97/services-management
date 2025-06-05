 package mc.monacotelecom.services.repository.specification;

 import mc.monacotelecom.services.entity.Service;
 import mc.monacotelecom.services.enums.ServiceActivity;
 import mc.monacotelecom.services.enums.Status;
 import org.springframework.data.jpa.domain.Specification;

 import java.time.LocalDateTime;

 import static mc.monacotelecom.services.entity.Constants.STATUS;

 public class ServiceSpecification {
    protected ServiceSpecification() {
    }

    public static <T extends Service> Specification<T> hasServiceId(Long serviceId) {
        return (root, query, cb) -> cb.equal(root.get("serviceId"), serviceId);
    }

    public static <T extends Service> Specification<T> hasNumber(String number) {
        return (root, query, cb) -> cb.equal(root.get("number"), number);
    }

    public static <T extends Service> Specification<T> hasMainRangeId(Long mainRangeId) {
        return (root, query, cb) -> cb.equal(root.get("mainRangeId"), mainRangeId);
    }

    public static <T extends Service> Specification<T> hasCustomerNo(Long customerNo) {
        return (root, query, cb) -> cb.equal(root.get("customerNo"), customerNo);
    }

    public static <T extends Service> Specification<T> hasCrmServiceId(String crmServiceId) {
        return (root, query, cb) -> cb.equal(root.get("crmServiceId"), crmServiceId);
    }

    public static <T extends Service> Specification<T> hasServiceActivity(ServiceActivity serviceActivity) {
        return (root, query, cb) -> cb.equal(root.get("serviceActivity"), serviceActivity);
    }

    public static <T extends Service> Specification<T> hasStatus(Status status){
        return (root, query, cb) -> cb.equal(root.get(STATUS), status);
    }

    public static <T extends Service> Specification<T> hasActivationDate (LocalDateTime activationDate) {
        return (root, query, cb) -> cb.and(
                cb.greaterThanOrEqualTo(root.get("activationDate"), activationDate.toLocalDate().atStartOfDay()),
                cb.lessThan(root.get("activationDate"), activationDate.toLocalDate().atStartOfDay().plusDays(1))
        );
    }

    public static <T extends Service> Specification<T> hasSubscriptionId(Long subscriptionId) {
        return (root, query, cb) -> cb.equal(root.get("subscriptionId"), subscriptionId);
    }
}
