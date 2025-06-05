package mc.monacotelecom.services.service;

import lombok.RequiredArgsConstructor;
import mc.monacotelecom.services.dto.TagActivationCodeDTO;
import mc.monacotelecom.services.dto.request.AddTagActivationCodeDTO;
import mc.monacotelecom.services.dto.search.SearchTagActivationCodeDTO;
import mc.monacotelecom.services.entity.TagActivation;
import mc.monacotelecom.services.process.TagActivationProcess;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TagActivationService {

    private final TagActivationProcess tagActivationProcess;

    @Transactional(readOnly = true)
    public PagedModel<TagActivationCodeDTO> search(final SearchTagActivationCodeDTO searchTagActivationCodeDTO,
                                                   Pageable pageable,
                                                   PagedResourcesAssembler<TagActivation> assembler) {
        return tagActivationProcess.search(searchTagActivationCodeDTO, pageable, assembler);
    }

    @Transactional(readOnly = true)
    public PagedModel<TagActivationCodeDTO> getAll(Pageable pageable, PagedResourcesAssembler<TagActivation> assembler) {
        return tagActivationProcess.getAll(pageable, assembler);
    }

    @Transactional
    public TagActivationCodeDTO add(AddTagActivationCodeDTO tagActivationCodeDTO) {
        return tagActivationProcess.add(tagActivationCodeDTO);
    }

    @Transactional
    public void delete(Long provisioningTagId, Long activationCodeId) {
        tagActivationProcess.delete(provisioningTagId, activationCodeId);
    }
}
