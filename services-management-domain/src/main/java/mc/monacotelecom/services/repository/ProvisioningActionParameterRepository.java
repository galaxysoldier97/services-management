package mc.monacotelecom.services.repository;

import mc.monacotelecom.services.entity.ProvisioningActionParameter;
import mc.monacotelecom.services.entity.ProvisioningActionParameterKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProvisioningActionParameterRepository extends JpaRepository<ProvisioningActionParameter, ProvisioningActionParameterKey>, JpaSpecificationExecutor<ProvisioningActionParameter> {
    Optional<ProvisioningActionParameter> findByProvisioningActionInternalIdAndTechnicalParameterInternalId(Long tagActionId, Long parameterId);
    List<ProvisioningActionParameter> findByProvisioningActionInternalId(Long tagActionId);
}
