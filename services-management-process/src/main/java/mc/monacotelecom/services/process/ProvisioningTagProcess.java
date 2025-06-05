package mc.monacotelecom.services.process;

import lombok.RequiredArgsConstructor;
import mc.monacotelecom.inventory.common.CommonFunctions;
import mc.monacotelecom.inventory.common.exporter.process.ExcelGenerator;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.services.assembler.ProvisioningProductResourceAssembler;
import mc.monacotelecom.services.assembler.ProvisioningTagResourceAssembler;
import mc.monacotelecom.services.dto.*;
import mc.monacotelecom.services.dto.request.CreateProvisioningTagDTO;
import mc.monacotelecom.services.dto.request.DecompositionRequestDTO;
import mc.monacotelecom.services.dto.request.GetParametersOnActionDTO;
import mc.monacotelecom.services.dto.request.UpdateProvisioningTagDTO;
import mc.monacotelecom.services.dto.search.SearchProvisioningTagDTO;
import mc.monacotelecom.services.entity.ProvisioningActionParameter;
import mc.monacotelecom.services.entity.ProvisioningProduct;
import mc.monacotelecom.services.entity.ProvisioningTag;
import mc.monacotelecom.services.enums.ProvisioningTagAction;
import mc.monacotelecom.services.exceptions.SvcNotFoundException;
import mc.monacotelecom.services.exceptions.SvcValidationException;
import mc.monacotelecom.services.importer.data.GenericDataCsvLines.ProvisioningTagCsvLine;
import mc.monacotelecom.services.mapper.ExporterEntityMapper;
import mc.monacotelecom.services.repository.ProvisioningActionParameterRepository;
import mc.monacotelecom.services.repository.ProvisioningActionRepository;
import mc.monacotelecom.services.repository.ProvisioningProductRepository;
import mc.monacotelecom.services.repository.ProvisioningTagRepository;
import mc.monacotelecom.services.repository.specification.ProvisioningTagSpecification;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static mc.monacotelecom.services.translation.TranslationMessages.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
@RequiredArgsConstructor
public class ProvisioningTagProcess {

    private final ProvisioningTagRepository provisioningTagRepository;
    private final ProvisioningTagResourceAssembler provisioningTagsResourceAssembler = ProvisioningTagResourceAssembler.of(ProvisioningTagProcess.class);
    private final ProvisioningProductRepository provisioningProductRepository;
    private final ProvisioningActionRepository provisioningActionRepository;
    private final ProvisioningActionParameterRepository provisioningActionParameterRepository;
    private final ProvisioningProductResourceAssembler provisioningProductResourceAssembler = ProvisioningProductResourceAssembler.of(ProvisioningTagProcess.class);
    private final LocalizedMessageBuilder localizedMessageBuilder;
    private final ExcelGenerator excelGenerator;
    private final ExporterEntityMapper exporterEntityMapper;

    public ProvisioningTagDTO getById(Long id) {
        ProvisioningTag provisioningTag = provisioningTagRepository.findByInternalId(id)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, PROVISIONING_TAG_NOT_FOUND_ID, id));
        return provisioningTagsResourceAssembler.toModel(provisioningTag);
    }

    public ProvisioningTagDTO get(String code) {
        ProvisioningTag provisioningTag = provisioningTagRepository.findById(code)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, PROVISIONING_TAG_NOT_FOUND_CODE, code));
        return provisioningTagsResourceAssembler.toModel(provisioningTag);
    }

    public void add(CreateProvisioningTagDTO dto) {
        if (provisioningTagRepository.existsById(dto.getTagCode())) {
            throw new SvcValidationException(localizedMessageBuilder, PROVISIONING_TAG_ALREADY_EXIST, dto.getTagCode());
        }

        provisioningTagRepository.save(provisioningTagsResourceAssembler.toEntity(dto));
    }

    public ProvisioningTagDTO update(Long id, UpdateProvisioningTagDTO dto) {
        var provisioningTag = provisioningTagRepository.findByInternalId(id)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, PROVISIONING_TAG_NOT_FOUND_ID, id));

        final BiConsumer<UpdateProvisioningTagDTO, ProvisioningTag> patcher = (dto1, tag1) -> {
            Optional.ofNullable(dto1.getNature()).ifPresent(tag1::setNature);
            Optional.ofNullable(dto1.getActivity()).ifPresent(tag1::setActivity);
            Optional.ofNullable(dto1.getDescription()).ifPresent(tag1::setDescription);
            Optional.ofNullable(dto1.getCategory()).ifPresent(tag1::setCategory);
            Optional.ofNullable(dto1.getAccessType()).ifPresent(tag1::setAccessType);
            Optional.ofNullable(dto1.getComponentType()).ifPresent(tag1::setComponentType);
            Optional.ofNullable(dto1.getPersistent()).ifPresent(tag1::setPersistent);
        };

        patcher.accept(dto, provisioningTag);

        return provisioningTagsResourceAssembler.toModel(provisioningTagRepository.save(provisioningTag));
    }

    public PagedModel<ProvisioningTagDTO> getAll(Pageable pageable, PagedResourcesAssembler<ProvisioningTag> assembler) {
        Page<ProvisioningTag> provisioningTags = provisioningTagRepository.findAll(pageable);
        return assembler.toModel(provisioningTags, provisioningTagsResourceAssembler, linkTo(ProvisioningTagProcess.class).slash("/provisioningtags").withSelfRel());
    }

    public ParametersOnActionResponseDTO getAllParametersOnAction(GetParametersOnActionDTO dto) {
        ParametersOnActionResponseDTO parametersOnActionResponseDTO = new ParametersOnActionResponseDTO();
        List<ProvisioningActionParameterDTO> provisioningActionParameterDTOS = new ArrayList<>();
        List<ProvisioningActionParameter> provisioningActionParameters = new ArrayList<>();
        if (dto.getParametersOnActionDTO().isEmpty()) {
            throw new SvcValidationException(localizedMessageBuilder, TAG_ACTION_CODE_LIST_EMPTY);
        } else {
            if (dto.getParametersOnActionDTO().stream().map(GetParametersOnActionDTO.ParametersOnActionDTO::getTagCode).anyMatch(Objects::isNull)) {
                throw new SvcValidationException(localizedMessageBuilder, TAG_ACTION_CODE_NULL);
            }

            for (GetParametersOnActionDTO.ParametersOnActionDTO pa : dto.getParametersOnActionDTO()) {
                final var provisioningAction = provisioningActionRepository.
                        findByTagTagCodeAndTagAction(pa.getTagCode(), pa.getTagAction())
                        .orElseThrow(() -> new SvcValidationException(localizedMessageBuilder, PROVISIONING_ACTION_EXPECTED));

                provisioningActionParameters = provisioningActionParameterRepository
                        .findByProvisioningActionInternalId(provisioningAction.getInternalId());

                if (provisioningActionParameters.isEmpty()) {
                    continue;
                }
                for (ProvisioningActionParameter p : provisioningActionParameters) {
                    ProvisioningActionParameterDTO provisioningActionParameterDTO = new ProvisioningActionParameterDTO();
                    provisioningActionParameterDTO.setParameterValue(p.getParameterValue());
                    TechnicalParameterDTO technicalParameterDTO = new TechnicalParameterDTO();
                    technicalParameterDTO.setParameterId(p.getTechnicalParameter().getInternalId());
                    technicalParameterDTO.setDescription(p.getTechnicalParameter().getDescription());
                    technicalParameterDTO.setParameterCode(p.getTechnicalParameter().getParameterCode());
                    technicalParameterDTO.setParameterType(p.getTechnicalParameter().getParameterType());
                    provisioningActionParameterDTO.setTechnicalParameter(technicalParameterDTO);
                    provisioningActionParameterDTOS.add(provisioningActionParameterDTO);
                    parametersOnActionResponseDTO.setProvisioningActionParameterDTO(provisioningActionParameterDTOS);
                }
            }
        }
        if (provisioningActionParameters.isEmpty()) {
            parametersOnActionResponseDTO.setProvisioningActionParameterDTO(provisioningActionParameterDTOS);
            return parametersOnActionResponseDTO;
        }
        return parametersOnActionResponseDTO;
    }

    public void delete(final Long id, final boolean force) {
        var tag = provisioningTagRepository.findByInternalId(id)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, PROVISIONING_TAG_NOT_FOUND_ID, id));

        if (!force) {
            if (Objects.nonNull(tag.getChildTag())) {
                throw new SvcValidationException(localizedMessageBuilder, PROVISIONING_TAG_NOT_DELETABLE_CHILD, tag.getTagCode(), tag.getChildTag().getTagCode());
            }

            if (!tag.getServiceTags().isEmpty()) {
                throw new SvcValidationException(localizedMessageBuilder, PROVISIONING_TAG_NOT_DELETABLE_SERVICES, tag.getTagCode());
            }

            if (!tag.getTagActivations().isEmpty()) {
                throw new SvcValidationException(localizedMessageBuilder, PROVISIONING_TAG_NOT_DELETABLE_ACTIVATIONS, tag.getTagCode());
            }
        }

        // Patching the parent to remove the parent-child relationship
        Optional.ofNullable(tag.getParentTag()).ifPresent(parent -> {
            parent.setChildTag(null);
            provisioningTagRepository.save(parent);
        });

        provisioningTagRepository.delete(tag);
    }

    public PagedModel<ProvisioningTagDTO> search(final SearchProvisioningTagDTO searchProvisioningTagDTO, Pageable pageable, PagedResourcesAssembler<ProvisioningTag> assembler) {
        final Specification<ProvisioningTag> specification = prepareSpecification(searchProvisioningTagDTO);
        Page<ProvisioningTag> provisioningTags = provisioningTagRepository.findAll(specification, pageable);
        return assembler.toModel(provisioningTags, provisioningTagsResourceAssembler, linkTo(ProvisioningTagProcess.class).slash("/provisioningtags").withSelfRel());
    }

    public Specification<ProvisioningTag> prepareSpecification(final SearchProvisioningTagDTO searchProvisioningTagDTO) {
        Specification<ProvisioningTag> specification = null;

        specification = StringUtils.isNotBlank(searchProvisioningTagDTO.getTagCode()) ? Specification.where(ProvisioningTagSpecification.hasTagCode(searchProvisioningTagDTO.getTagCode())) : specification;
        specification = Objects.nonNull(searchProvisioningTagDTO.getAccessType()) ? CommonFunctions.addSpecification(specification, ProvisioningTagSpecification.hasAccessType(searchProvisioningTagDTO.getAccessType())) : specification;
        specification = Objects.nonNull(searchProvisioningTagDTO.getActivity()) ? CommonFunctions.addSpecification(specification, ProvisioningTagSpecification.hasActivity(searchProvisioningTagDTO.getActivity())) : specification;
        specification = Objects.nonNull(searchProvisioningTagDTO.getNature()) ? CommonFunctions.addSpecification(specification, ProvisioningTagSpecification.hasNature(searchProvisioningTagDTO.getNature())) : specification;
        specification = Objects.nonNull(searchProvisioningTagDTO.getComponentType()) ? CommonFunctions.addSpecification(specification, ProvisioningTagSpecification.hasComponentType(searchProvisioningTagDTO.getComponentType())) : specification;
        specification = Objects.nonNull(searchProvisioningTagDTO.getCategory()) ? CommonFunctions.addSpecification(specification, ProvisioningTagSpecification.hasCategory(searchProvisioningTagDTO.getCategory())) : specification;
        specification = Objects.nonNull(searchProvisioningTagDTO.getPersistent()) ? CommonFunctions.addSpecification(specification, ProvisioningTagSpecification.isPersistent(searchProvisioningTagDTO.getPersistent())) : specification;

        return specification;
    }

    public List<ProvisioningProductDTO> decomposeProductsRequest(DecompositionRequestDTO dto) {
        List<DecompositionRequestDTO.ProvisioningProductDTO> productRequests = dto.getProvisioningProductRequests();

        List<ProvisioningProduct> provisioningProducts = new ArrayList<>();
        StringBuilder errorMessage = new StringBuilder();

        productRequests.forEach(productRequest -> {

            errorMessage.append(productRequest.getProductCode())
                    .append(" - ")
                    .append(productRequest.getProductAction()).append(", ");

            List<ProvisioningProduct> products = provisioningProductRepository.findByProductCodeAndActionRequest(productRequest.getProductCode(), productRequest.getProductAction());
            provisioningProducts.addAll(products);
        });

        if (provisioningProducts.isEmpty()) {
            final var message = errorMessage.length() > 1 ? errorMessage.subSequence(0, errorMessage.length() - 2) : errorMessage;
            throw new SvcNotFoundException(localizedMessageBuilder, PROVISIONING_PRODUCT_NOT_FOUND, message.toString());
        }
        List<ProvisioningProduct> provisioningProductsRemoved = new ArrayList<>();

        for (ProvisioningProduct provisioningProduct : provisioningProducts) {

            if (ProvisioningTagAction.PURCHASE.equals(provisioningProduct.getProvisioningAction().getTagAction())) {
                for (ProvisioningProduct provisioningProductBis : provisioningProducts) {

                    if (provisioningProductBis.getProductCode().equals(provisioningProduct.getProductCode())
                            && ProvisioningTagAction.CANCEL.equals(provisioningProductBis.getProvisioningAction().getTagAction())
                            && provisioningProductBis.getProvisioningAction().getTag().getTagCode().equals(provisioningProduct.getProvisioningAction().getTag().getTagCode())) {
                        provisioningProductsRemoved.add(provisioningProduct);
                        provisioningProductsRemoved.add(provisioningProductBis);
                    }
                }
            }
        }

        provisioningProducts.removeAll(provisioningProductsRemoved);
        return provisioningProducts.stream().map(provisioningProductResourceAssembler::toModel).collect(Collectors.toList());
    }

    public ByteArrayInputStream export() {
        final var toExport = provisioningTagRepository.findAll()
                .stream().map(exporterEntityMapper::toProvisioningTagExporter);

        return excelGenerator.writeToExcel(toExport, ProvisioningTagCsvLine.class, "export.provisioningTag");
    }

    /**
     * Retrieve provisioning tags not declared in the provisioning model but expected by existing services
     */
    public List<String> getOrphans() {
        return provisioningTagRepository.findMissingTags();
    }
}
