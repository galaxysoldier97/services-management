package mc.monacotelecom.services.repository;

import mc.monacotelecom.services.entity.Service;
import mc.monacotelecom.services.entity.ServiceActivation;
import mc.monacotelecom.services.entity.ServiceActivationKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceActivationRepository extends JpaRepository<ServiceActivation, ServiceActivationKey>, JpaSpecificationExecutor<ServiceActivation> {

    List<ServiceActivation> findByService(Service service);

    Page<ServiceActivation> findByServiceServiceId(Long serviceId, Pageable pageable);

    Page<ServiceActivation> findByServiceServiceIdIn(List<Long> ids, Pageable pageable);

    List<ServiceActivation> findAllByService(Service service);

    @Modifying
    @Query("DELETE FROM ServiceActivation sa WHERE sa.service = ?1")
    void deleteAllByServiceId(Service service);

    @Modifying
    @Query(value = "delete sa from service_activation sa left join activation_code ac on sa.activ_code = ac.activ_code where ac.activ_code is null", nativeQuery = true)
    void deleteOrphansByService(@Param("serviceId") Long serviceId);
}
