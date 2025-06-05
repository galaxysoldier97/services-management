package mc.monacotelecom.services.repository;

import mc.monacotelecom.services.entity.ProvisioningTag;
import mc.monacotelecom.services.entity.Service;
import mc.monacotelecom.services.entity.ServiceTag;
import mc.monacotelecom.services.entity.ServiceTagKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ServiceTagRepository extends JpaRepository<ServiceTag, ServiceTagKey>, JpaSpecificationExecutor<ServiceTag> {

    @Modifying
    @Query("DELETE FROM ServiceTag st WHERE st.service = ?1 AND st.provisioningTag IN (?2)")
    void deleteAllByServiceAndTags(Service service, Collection<ProvisioningTag> provisioningTags);

    @Query("SELECT st FROM ServiceTag st WHERE st.service = ?1 AND st.provisioningTag IN (?2)")
    List<ServiceTag> findAllByServiceAndTags(Service service, Collection<ProvisioningTag> provisioningTags);

    List<ServiceTag> findAllByService(Service service);

    @Modifying
    @Query("DELETE FROM ServiceTag st WHERE st.service = ?1")
    void deleteAllByService(Service service);

    @Modifying
    @Query(value = "delete st from service_tag st left join provisioning_tag pt on st.tag_code = pt.tag_code where pt.tag_code is null", nativeQuery = true)
    void deleteOrphansByService(@Param("serviceId") Long serviceId);
}
