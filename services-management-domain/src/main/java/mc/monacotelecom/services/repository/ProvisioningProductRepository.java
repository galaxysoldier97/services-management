package mc.monacotelecom.services.repository;

import mc.monacotelecom.services.entity.ProvisioningProduct;
import mc.monacotelecom.services.entity.ProvisioningProductKey;
import mc.monacotelecom.services.enums.ProvisioningProductActionRequest;
import mc.monacotelecom.services.enums.ProvisioningTagAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProvisioningProductRepository extends JpaRepository<ProvisioningProduct, ProvisioningProductKey>, JpaSpecificationExecutor<ProvisioningProduct> {

    List<ProvisioningProduct> findByProductCodeAndActionRequest(String productCode, ProvisioningProductActionRequest actionRequest);

    Optional<ProvisioningProduct> findByProductCodeAndActionRequestAndProvisioningActionTagTagCodeAndProvisioningActionTagAction(String productCode, ProvisioningProductActionRequest actionRequest, String tagCode, ProvisioningTagAction tagAction);
    Optional<ProvisioningProduct> findByProvisioningActionInternalIdAndInternalId(Long provActionId, Long id);
    Page<ProvisioningProduct> findByProvisioningActionInternalId(Long provActionId, Pageable pageable);
    Optional<ProvisioningProduct> findByInternalId(Long internalId);
}
