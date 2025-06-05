package mc.monacotelecom.services.repository;

import mc.monacotelecom.services.entity.ServiceParameter;
import mc.monacotelecom.services.entity.ServiceParameterKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;


public interface ServiceParameterRepository extends JpaRepository<ServiceParameter, ServiceParameterKey>, JpaSpecificationExecutor<ServiceParameter> {
    Optional<ServiceParameter> findByTechnicalParameterInternalIdAndServiceId(Long technicalParameterId, Long serviceId);

    boolean existsByServiceIdAndParameterCodeAndParameterType(Long serviceId, String parameterCode, String parameterType);

    void deleteAllByServiceId( Long serviceId);

    Optional<ServiceParameter> findByServiceIdAndParameterCodeAndParameterType(Long serviceId, String parameterCode, String parameterType);
    List<ServiceParameter> findByServiceId(Long serviceId);

}
