package mc.monacotelecom.services.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.services.dto.ProvisioningProductDTO;
import mc.monacotelecom.services.dto.request.CreateProvisioningProductDTO;
import mc.monacotelecom.services.dto.request.UpdateProvisioningProductDTO;
import mc.monacotelecom.services.dto.search.SearchProvisioningProductDTO;
import mc.monacotelecom.services.enums.ProvisioningProductActionRequest;
import mc.monacotelecom.services.enums.ProvisioningTagAction;
import mc.monacotelecom.services.process.ProvisioningProductProcess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProvisioningProductService {

    private final ProvisioningProductProcess provisioningProductProcess;

    @Transactional(readOnly = true)
    public ProvisioningProductDTO getById(Long actionId, Long productId) {
        return provisioningProductProcess.getById(actionId, productId);
    }

    @Transactional(readOnly = true)
    public ProvisioningProductDTO get(String productCode, ProvisioningProductActionRequest actionRequest, String tagCode, ProvisioningTagAction tagAction) {
        return provisioningProductProcess.get(productCode, actionRequest, tagCode, tagAction);
    }

    @Transactional(readOnly = true)
    public Page<ProvisioningProductDTO> search(Pageable pageable, SearchProvisioningProductDTO dto) {
        return provisioningProductProcess.search(pageable, dto);
    }

    @Transactional(readOnly = true)
    public Page<ProvisioningProductDTO> getAllProvisioningProductsForProvisioningAction(Long tagActionId, Pageable pageable) {
        return provisioningProductProcess.getAllProvisioningProductsForProvisioningAction(tagActionId, pageable);
    }

    @Transactional
    public ProvisioningProductDTO add(Long actionId, CreateProvisioningProductDTO dto) {
        return provisioningProductProcess.add(actionId, dto);
    }

    @Transactional
    public ProvisioningProductDTO update(Long actionId, Long productId, UpdateProvisioningProductDTO dto) {
        return provisioningProductProcess.update(actionId, productId, dto);
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream export(){
        return provisioningProductProcess.export();
    }

    @Transactional
    public void delete(Long actionId, Long productId) {
        provisioningProductProcess.delete(actionId, productId);
    }
}
