package mc.monacotelecom.services.repository.specification;

import mc.monacotelecom.inventory.common.recycling.domain.BaseJobSpecification;
import mc.monacotelecom.services.entity.JobConfiguration;
import mc.monacotelecom.services.enums.ServiceActivity;
import mc.monacotelecom.services.enums.ServiceCategory;
import mc.monacotelecom.services.enums.Status;
import org.springframework.data.jpa.domain.Specification;

import static mc.monacotelecom.services.entity.Constants.*;

public class JobSpecification extends BaseJobSpecification {

    public static Specification<JobConfiguration> hasCategory(ServiceCategory category) {
        return (root, query, cb) -> cb.equal(root.get(CATEGORY), category);
    }

    public static Specification<JobConfiguration> hasActivity(ServiceActivity activity) {
        return (root, query, cb) -> cb.equal(root.get(ACTIVITY), activity);
    }

    public static Specification<JobConfiguration> hasStatus(Status status) {
        return (root, query, cb) -> cb.equal(root.get(STATUS), status);
    }

}
