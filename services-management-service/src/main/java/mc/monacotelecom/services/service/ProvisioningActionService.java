package mc.monacotelecom.services.service;

import lombok.RequiredArgsConstructor;
import mc.monacotelecom.services.dto.ProvisioningActionDTO;
import mc.monacotelecom.services.dto.request.CreateProvisioningActionDTO;
import mc.monacotelecom.services.dto.request.UpdateProvisioningActionDTO;
import mc.monacotelecom.services.enums.ProvisioningTagAction;
import mc.monacotelecom.services.process.ProvisioningActionProcess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
public class ProvisioningActionService {

    private final ProvisioningActionProcess provisioningActionProcess;

    @Transactional(readOnly = true)
    public ProvisioningActionDTO getById(Long tagActionId) {
        return provisioningActionProcess.getById(tagActionId);
    }

    @Transactional(readOnly = true)
    public ProvisioningActionDTO get(Long tagId, ProvisioningTagAction tagAction) {
        return provisioningActionProcess.get(tagId, tagAction);
    }

    @Transactional(readOnly = true)
    public Page<ProvisioningActionDTO> getAll(Pageable pageable) {
        return provisioningActionProcess.getAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProvisioningActionDTO> getAllProvisioningActionForProvisioningTag(Long provisioningTagId, Pageable pageable) {
        return provisioningActionProcess.getAllProvisioningActionForProvisioningTag(provisioningTagId, pageable);
    }

    @Transactional
    public void add(CreateProvisioningActionDTO dto) {
        provisioningActionProcess.add(dto);
    }

    @Transactional
    public ProvisioningActionDTO update(Long id, UpdateProvisioningActionDTO createProvisioningActionDTO) {
        return provisioningActionProcess.update(id, createProvisioningActionDTO);
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream export() {
        return provisioningActionProcess.export();
    }

    @Transactional
    public void delete(final Long tagActionId, final boolean force) {
        provisioningActionProcess.delete(tagActionId, force);
    }
}
