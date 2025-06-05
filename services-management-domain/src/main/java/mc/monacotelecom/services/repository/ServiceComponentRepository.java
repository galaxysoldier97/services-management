package mc.monacotelecom.services.repository;

import mc.monacotelecom.services.entity.ServiceComponent;
import mc.monacotelecom.services.enums.ServiceActivity;
import mc.monacotelecom.services.enums.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface ServiceComponentRepository extends JpaRepository<ServiceComponent, Long>, JpaSpecificationExecutor<ServiceComponent>, RevisionRepository<ServiceComponent,Long,Integer> {

    Optional<ServiceComponent> findBySubscriptionIdAndServiceCategoryAndServiceActivityAndComponentType(Long subscriptionId, ServiceCategory serviceCategory, ServiceActivity serviceActivity, String componentType);

}
