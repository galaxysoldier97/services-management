package mc.monacotelecom.services.repository;

import mc.monacotelecom.services.entity.Service;
import mc.monacotelecom.services.enums.ServiceActivity;
import mc.monacotelecom.services.enums.ServiceCategory;
import mc.monacotelecom.services.projections.ServiceLastStatusChangeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import static mc.monacotelecom.services.repository.queries.ServiceQueries.*;

public interface ServiceRepository<T extends Service> extends JpaRepository<T, Long>, JpaSpecificationExecutor<Service>, RevisionRepository<T, Long, Integer> {

    Optional<T> findByMainRangeId(Long mainRangeId);

    Optional<T> findByNumber(String number);

    Optional<T> findByServiceOrderIdStartingWith(String serviceOrderId);

    Optional<T> findByCrmServiceIdAndServiceActivityAndServiceCategory(String newCrmServiceId, ServiceActivity serviceActivity, ServiceCategory serviceCategory);

    @Query(value = "select service_activation.activ_code " +
            "from service " +
            "left join service_activation " +
            "on service.id = service_activation.service_id " +
            "where service_activation.activ_code is null " +
            "and (:serviceId is null or service.id = :serviceId);", nativeQuery = true)
    List<String> findServicesWithNonExistentActivationCodeByService(@Param("serviceId") Long serviceId);

    @Query(value = "select service_tag.tag_code " +
            "from service " +
            "left join service_tag " +
            "on service.id = service_tag.service_id " +
            "where service_tag.tag_code is null " +
            "and (:serviceId is null or service.id = :serviceId);", nativeQuery = true)
    List<String> findServicesWithNonExistentProvisioningTagByService(@Param("serviceId") Long serviceId);

    @Query(value = "SELECT sa.service.serviceId FROM ServiceActivation sa LEFT JOIN sa.activationCode ac WHERE ac is null")
    List<Long> findServicesWithNonExistentActivationCode();

    @Query(value = "SELECT st.service.serviceId FROM ServiceTag st LEFT JOIN st.provisioningTag pt WHERE pt is null")
    List<Long> findServicesWithNonExistentProvisioningTag();



    @Query(nativeQuery = true, value = GET_CANDIDATE_SERVICES_FOR_CLEANUP)
    List<ServiceLastStatusChangeProjection> findCandidatesForCleanup(@Param("status") String status, @Param("category") String category, @Param("activity") String activity, @Param("delay") long delay);

    @Modifying
    @Query(nativeQuery = true, value = DELETE_AUDIT_FOR_SERVICE_IDS)
    void deleteServiceAuditWhereServiceIdIn(@Param("ids") List<Long> ids);

    @Modifying
    @Query(nativeQuery = true, value = DELETE_AUDIT_FOR_SERVICE_ACCESS_IDS)
    void deleteServiceAccessAuditWhereServiceIdIn(@Param("ids") List<Long> ids);

    @Modifying
    @Query(nativeQuery = true, value = DELETE_AUDIT_FOR_SERVICE_COMPONENT_IDS)
    void deleteServiceComponentAuditWhereServiceIdIn(@Param("ids") List<Long> ids);
}
