package mc.monacotelecom.services.repository;

import mc.monacotelecom.services.entity.TechnicalParameter;
import mc.monacotelecom.services.entity.TechnicalParameterKey;
import mc.monacotelecom.services.enums.TechnicalParameterType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TechnicalParameterRepository  extends JpaRepository<TechnicalParameter, TechnicalParameterKey>, JpaSpecificationExecutor<TechnicalParameter> {
    Optional<TechnicalParameter> findByInternalId(Long internalId);
    boolean existsByInternalId(Long internalId);

    @EntityGraph(value = "TechnicalParameter.provisioningActionParameters")
    Optional<TechnicalParameter> findByParameterCode(String parameterCode);

    @EntityGraph(value = "TechnicalParameter.provisioningActionParameters")
    Optional<TechnicalParameter> findByParameterCodeAndParameterType(String parameterCode, TechnicalParameterType parameterType);
}
