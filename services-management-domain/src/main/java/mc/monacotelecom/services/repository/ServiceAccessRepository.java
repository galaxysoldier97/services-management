package mc.monacotelecom.services.repository;

import mc.monacotelecom.services.entity.ServiceAccess;
import mc.monacotelecom.services.entity.projection.ServiceAccessUnmProjection;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ServiceActivity;
import mc.monacotelecom.services.enums.ServiceCategory;
import mc.monacotelecom.services.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ServiceAccessRepository extends JpaRepository<ServiceAccess, Long>, JpaSpecificationExecutor<ServiceAccess>, RevisionRepository<ServiceAccess, Long, Integer> {

    Optional<ServiceAccess> findFirstByAccessTypeOrderByTechIdDesc(Network accessType);

    Optional<ServiceAccess> findByTechIdAndAccessTypeAndStatusNotIn(String techId, Network accessType, Collection<Status> status);

    List<ServiceAccess> findByAccessTypeAndStatus(Network accessType, Status status);

    Optional<ServiceAccess> findByAccessPointId(String accessPointId);

    Optional<ServiceAccess> findByOntId(String ontId);

    boolean existsByTechId(String techId);

    List<ServiceAccessUnmProjection> findByCrmServiceIdIn(List<String> crmServiceIds);

    Optional<ServiceAccess> findBySubscriptionIdAndServiceCategoryAndServiceActivityAndAccessType(Long subscriptionId, ServiceCategory serviceCategory, ServiceActivity serviceActivity, Network accessType);
}
