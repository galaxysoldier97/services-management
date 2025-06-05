package mc.monacotelecom.services.repository;

import mc.monacotelecom.services.entity.ProvisioningAction;
import mc.monacotelecom.services.entity.ProvisioningActionKey;
import mc.monacotelecom.services.enums.ProvisioningTagAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProvisioningActionRepository extends JpaRepository<ProvisioningAction, ProvisioningActionKey>, JpaSpecificationExecutor<ProvisioningAction> {

    Optional<ProvisioningAction> findByTagInternalIdAndTagAction(Long tagId, ProvisioningTagAction tagAction);
    Optional<ProvisioningAction> findByTagTagCodeAndTagAction(String tagCode, ProvisioningTagAction tagAction);
    Page<ProvisioningAction> findByTagInternalId(Long tagId, Pageable pageable);
    Optional<ProvisioningAction> findByInternalId(Long internalId);
    boolean existsByInternalId(Long internalId);
    void deleteByInternalId(Long internalId);
}
