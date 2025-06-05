package mc.monacotelecom.services.process;

import lombok.RequiredArgsConstructor;
import mc.monacotelecom.inventory.common.CommonFunctions;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.services.assembler.TagActivationCodeResourceAssembler;
import mc.monacotelecom.services.dto.TagActivationCodeDTO;
import mc.monacotelecom.services.dto.request.AddTagActivationCodeDTO;
import mc.monacotelecom.services.dto.search.SearchTagActivationCodeDTO;
import mc.monacotelecom.services.entity.ActivationCode;
import mc.monacotelecom.services.entity.ProvisioningTag;
import mc.monacotelecom.services.entity.TagActivation;
import mc.monacotelecom.services.exceptions.SvcNotFoundException;
import mc.monacotelecom.services.repository.ActivationCodeRepository;
import mc.monacotelecom.services.repository.ProvisioningTagRepository;
import mc.monacotelecom.services.repository.TagActivationRepository;
import mc.monacotelecom.services.repository.specification.TagActivationSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static mc.monacotelecom.services.translation.TranslationMessages.ACTIVATION_CODE_NOT_FOUND_ID;
import static mc.monacotelecom.services.translation.TranslationMessages.PROVISIONING_TAG_NOT_FOUND_ID;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
@RequiredArgsConstructor
public class TagActivationProcess {

    private final TagActivationRepository tagActivationRepository;
    private final ActivationCodeRepository activationCodeRepository;
    private final ProvisioningTagRepository provisioningTagRepository;
    private final TagActivationCodeResourceAssembler tagActivationCodeResourceAssembler = TagActivationCodeResourceAssembler.of(TagActivationProcess.class);
    private final LocalizedMessageBuilder localizedMessageBuilder;

    public PagedModel<TagActivationCodeDTO> search(final SearchTagActivationCodeDTO dto, Pageable pageable, PagedResourcesAssembler<TagActivation> assembler) {
        final Specification<TagActivation> specification = prepareSpecification(dto);
        Page<TagActivation> tagActivations = tagActivationRepository.findAll(specification, pageable);
        return assembler.toModel(tagActivations, tagActivationCodeResourceAssembler, linkTo(TagActivationProcess.class).slash("/tagactivationcodes").withSelfRel());
    }

    public Specification<TagActivation> prepareSpecification(final SearchTagActivationCodeDTO dto) {
        Specification<TagActivation> specification = null;

        specification = Objects.nonNull(dto.getTagCodes()) ? Specification.where(TagActivationSpecification.hasTagCode(dto.getTagCodes())) : specification;
        specification = Objects.nonNull(dto.getAccessType()) ? CommonFunctions.addSpecification(specification, TagActivationSpecification.hasAccessType(dto.getAccessType())) : specification;
        specification = Objects.nonNull(dto.getActivity()) ? CommonFunctions.addSpecification(specification, TagActivationSpecification.hasActivity(dto.getActivity())) : specification;
        specification = Objects.nonNull(dto.getNature()) ? CommonFunctions.addSpecification(specification, TagActivationSpecification.hasNature(dto.getNature())) : specification;

        return specification;
    }

    public PagedModel<TagActivationCodeDTO> getAll(Pageable pageable, PagedResourcesAssembler<TagActivation> assembler) {
        Page<TagActivation> tagActivations = tagActivationRepository.findAll(pageable);
        return assembler.toModel(tagActivations, tagActivationCodeResourceAssembler, linkTo(TagActivationProcess.class).slash("/").withSelfRel());
    }

    public TagActivationCodeDTO add(AddTagActivationCodeDTO dto) {
        ActivationCode activationCode = activationCodeRepository.findByInternalId(dto.getActivId())
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, ACTIVATION_CODE_NOT_FOUND_ID, dto.getActivId()));
        ProvisioningTag provisioningTag = provisioningTagRepository.findByInternalId(dto.getTagId())
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, PROVISIONING_TAG_NOT_FOUND_ID, dto.getTagId()));
        TagActivation tagActivation = new TagActivation(dto.getTagValue(), provisioningTag.getTagCode(), activationCode.getActivCode(), provisioningTag, activationCode);
        return tagActivationCodeResourceAssembler.toModel(tagActivationRepository.save(tagActivation));
    }

    public void delete(Long provisioningTagId, Long activationCodeId) {
        ActivationCode activationCode = activationCodeRepository.findByInternalId(activationCodeId)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, ACTIVATION_CODE_NOT_FOUND_ID, activationCodeId));
        ProvisioningTag provisioningTag = provisioningTagRepository.findByInternalId(provisioningTagId)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, PROVISIONING_TAG_NOT_FOUND_ID, provisioningTagId));
        tagActivationRepository.deleteByProvisioningTagAndActivationCode(provisioningTag, activationCode);
    }
}
