package mc.monacotelecom.services.service;

import lombok.RequiredArgsConstructor;
import mc.monacotelecom.services.dto.ParametersOnActionResponseDTO;
import mc.monacotelecom.services.dto.ProvisioningProductDTO;
import mc.monacotelecom.services.dto.ProvisioningTagDTO;
import mc.monacotelecom.services.dto.request.CreateProvisioningTagDTO;
import mc.monacotelecom.services.dto.request.DecompositionRequestDTO;
import mc.monacotelecom.services.dto.request.GetParametersOnActionDTO;
import mc.monacotelecom.services.dto.request.UpdateProvisioningTagDTO;
import mc.monacotelecom.services.dto.search.SearchProvisioningTagDTO;
import mc.monacotelecom.services.entity.ProvisioningTag;
import mc.monacotelecom.services.process.ProvisioningTagProcess;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProvisioningTagService {

    private final ProvisioningTagProcess provisioningTagProcess;

    @Transactional(readOnly = true)
    public ProvisioningTagDTO getById(Long id) {
        return provisioningTagProcess.getById(id);
    }

    @Transactional(readOnly = true)
    public ProvisioningTagDTO get(String code) {
        return provisioningTagProcess.get(code);
    }

    @Transactional
    public void add(CreateProvisioningTagDTO dto) {
        provisioningTagProcess.add(dto);
    }

    @Transactional
    public ProvisioningTagDTO update(Long id, UpdateProvisioningTagDTO provisioningTagDTO) {
        return provisioningTagProcess.update(id, provisioningTagDTO);
    }

    @Transactional(readOnly = true)
    public PagedModel<ProvisioningTagDTO> getAll(Pageable pageable, PagedResourcesAssembler<ProvisioningTag> assembler) {
        return provisioningTagProcess.getAll(pageable, assembler);
    }

    @Transactional(readOnly = true)
    public ParametersOnActionResponseDTO getAllParametersOnAction(GetParametersOnActionDTO getParametersOnActionDTO) {
        return provisioningTagProcess.getAllParametersOnAction(getParametersOnActionDTO);
    }

    @Transactional
    public void delete(final Long id, final boolean force) {
        provisioningTagProcess.delete(id, force);
    }

    @Transactional(readOnly = true)
    public PagedModel<ProvisioningTagDTO> search(final SearchProvisioningTagDTO searchProvisioningTagDTO, Pageable pageable, PagedResourcesAssembler<ProvisioningTag> assembler) {
        return provisioningTagProcess.search(searchProvisioningTagDTO, pageable, assembler);
    }

    @Transactional
    public List<ProvisioningProductDTO> decomposeProductsRequest(DecompositionRequestDTO dto) {
        return provisioningTagProcess.decomposeProductsRequest(dto);
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream export(){
        return provisioningTagProcess.export();
    }

    @Transactional(readOnly = true)
    public List<String> getOrphans() {
        return provisioningTagProcess.getOrphans();
    }
}
