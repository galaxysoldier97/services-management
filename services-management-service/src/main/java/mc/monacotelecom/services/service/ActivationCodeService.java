package mc.monacotelecom.services.service;

import lombok.RequiredArgsConstructor;
import mc.monacotelecom.services.dto.ActivationCodeDTO;
import mc.monacotelecom.services.dto.ServiceActivationDTO;
import mc.monacotelecom.services.dto.request.CreateActivationCodeDTO;
import mc.monacotelecom.services.dto.request.UpdateActivationCodeDTO;
import mc.monacotelecom.services.dto.search.SearchActivationCodeDTO;
import mc.monacotelecom.services.entity.ActivationCode;
import mc.monacotelecom.services.entity.ServiceActivation;
import mc.monacotelecom.services.process.ActivationCodeProcess;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ActivationCodeService {

    private final ActivationCodeProcess activationCodeProcess;

    @Transactional(readOnly = true)
    public ActivationCodeDTO getByInternalId(Long id) {
        return activationCodeProcess.getByInternalId(id);
    }

    @Transactional(readOnly = true)
    public ActivationCodeDTO get(String code) {
        return activationCodeProcess.get(code);
    }

    @Transactional
    public void add(CreateActivationCodeDTO dto) {
        activationCodeProcess.add(dto);
    }

    @Transactional(readOnly = true)
    public PagedModel<ActivationCodeDTO> getAll(Pageable pageable, PagedResourcesAssembler<ActivationCode> assembler) {
        return activationCodeProcess.getAll(pageable, assembler);
    }

    @Transactional(readOnly = true)
    public PagedModel<ActivationCodeDTO> search(final SearchActivationCodeDTO searchActivationCodeDTO, Pageable pageable, PagedResourcesAssembler<ActivationCode> assembler) {
        return activationCodeProcess.search(searchActivationCodeDTO, pageable, assembler);
    }

    @Transactional(readOnly = true)
    public PagedModel<ServiceActivationDTO> getAllActivationCodesByAccessType(String accessType, Pageable pageable, PagedResourcesAssembler<ServiceActivation> assembler) {
        return activationCodeProcess.getAllByAccessType(accessType, pageable, assembler);
    }

    @Transactional(readOnly = true)
    public PagedModel<ServiceActivationDTO> getAllActivationCodesByTechId(String techId, String accessType, Pageable pageable, PagedResourcesAssembler<ServiceActivation> assembler) {
        return activationCodeProcess.getAllByTechId(techId, accessType, pageable, assembler);
    }

    @Transactional
    public ActivationCodeDTO update(Long id, UpdateActivationCodeDTO dto) {
        return activationCodeProcess.update(id, dto);
    }

    @Transactional
    public void delete(final Long id, final boolean force) {
        activationCodeProcess.delete(id, force);
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream export(){
        return activationCodeProcess.export();
    }

    @Transactional(readOnly = true)
    public List<String> getOrphans() {
        return activationCodeProcess.getOrphans();
    }
}
