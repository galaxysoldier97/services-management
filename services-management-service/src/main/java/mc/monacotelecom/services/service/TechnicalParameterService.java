package mc.monacotelecom.services.service;

import lombok.RequiredArgsConstructor;
import mc.monacotelecom.services.dto.TechnicalParameterDTO;
import mc.monacotelecom.services.dto.request.UpdateTechnicalParameterDTO;
import mc.monacotelecom.services.dto.search.SearchTechnicalParamDTO;
import mc.monacotelecom.services.entity.TechnicalParameter;
import mc.monacotelecom.services.enums.TechnicalParameterType;
import mc.monacotelecom.services.process.TechnicalParameterProcess;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;


@Service
@RequiredArgsConstructor
public class TechnicalParameterService {

    private final TechnicalParameterProcess technicalParameterProcess;

    @Transactional(readOnly = true)
    public TechnicalParameterDTO getByInternalId(Long id) {
        return technicalParameterProcess.getByInternalId(id);
    }

    @Transactional(readOnly = true)
    public TechnicalParameterDTO get(String code, TechnicalParameterType type) {
        return technicalParameterProcess.get(code, type);
    }

    @Transactional
    public void add(TechnicalParameterDTO technicalParameterDTO) {
        technicalParameterProcess.add(technicalParameterDTO);
    }

    @Transactional
    public TechnicalParameterDTO update(Long id, UpdateTechnicalParameterDTO dto) {
        return technicalParameterProcess.update(id, dto);
    }

    @Transactional(readOnly = true)
    public PagedModel<TechnicalParameterDTO> search(final SearchTechnicalParamDTO searchTechnicalParamDTO, Pageable pageable,
                                                        PagedResourcesAssembler<TechnicalParameter> assembler) {
        return technicalParameterProcess.search(searchTechnicalParamDTO, pageable, assembler);
    }

    @Transactional
    public void delete(Long id) {
        technicalParameterProcess.delete(id);
    }

    @Transactional(readOnly = true)
    public PagedModel<TechnicalParameterDTO> getAll(Pageable pageable, PagedResourcesAssembler<TechnicalParameter> assembler) {
        return technicalParameterProcess.getAll(pageable, assembler);
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream export(){
        return technicalParameterProcess.export();
    }
}
