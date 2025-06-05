package mc.monacotelecom.services.service;

import lombok.RequiredArgsConstructor;
import mc.monacotelecom.services.dto.ProvisioningActionParameterDTO;
import mc.monacotelecom.services.dto.ProvisioningActionParameterDetailsDTO;
import mc.monacotelecom.services.dto.request.AddProvisioningActionParameterDTO;
import mc.monacotelecom.services.dto.search.SearchProvisioningActionDTO;
import mc.monacotelecom.services.entity.ProvisioningActionParameter;
import mc.monacotelecom.services.process.ProvisioningActionParameterProcess;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ProvisioningActionParameterService {

    private final ProvisioningActionParameterProcess provisioningActionParameterProcess;

    @Transactional(readOnly = true)
    public PagedModel<ProvisioningActionParameterDetailsDTO> search(final SearchProvisioningActionDTO searchProvisioningActionDTO,
                                                                        Pageable pageable,
                                                                        PagedResourcesAssembler<ProvisioningActionParameter> assembler) {
        return provisioningActionParameterProcess.search(searchProvisioningActionDTO, pageable, assembler);
    }

    @Transactional(readOnly = true)
    public ProvisioningActionParameterDTO getById(Long provisioningActionId, Long parameterId) {
        return provisioningActionParameterProcess.getById(provisioningActionId, parameterId);
    }

    @Transactional(readOnly = true)
    public PagedModel<ProvisioningActionParameterDetailsDTO> getAll(Pageable pageable, PagedResourcesAssembler<ProvisioningActionParameter> assembler) {
        return provisioningActionParameterProcess.getAll(pageable, assembler);
    }

    @Transactional
    public ProvisioningActionParameterDetailsDTO add(AddProvisioningActionParameterDTO provisioningActionParameterDTO) {
        return provisioningActionParameterProcess.add(provisioningActionParameterDTO);
    }

    @Transactional
    public ProvisioningActionParameterDTO add(Long provisioningActionId, ProvisioningActionParameterDTO provisioningActionParameterDTO) {
        return provisioningActionParameterProcess.add(provisioningActionId, provisioningActionParameterDTO);
    }

    @Transactional
    public ProvisioningActionParameterDTO update(Long provisioningActionId, Long parameterId, ProvisioningActionParameterDTO provisioningActionParameterDTO) {
        return provisioningActionParameterProcess.update(provisioningActionId, parameterId, provisioningActionParameterDTO);
    }

    @Transactional
    public void delete(Long provisioningActionId, Long parameterId) {
        provisioningActionParameterProcess.delete(provisioningActionId, parameterId);
    }
}