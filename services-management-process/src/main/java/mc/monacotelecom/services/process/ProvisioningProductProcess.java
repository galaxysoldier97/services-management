package mc.monacotelecom.services.process;

import lombok.RequiredArgsConstructor;
import mc.monacotelecom.inventory.common.CommonFunctions;
import mc.monacotelecom.inventory.common.exporter.process.ExcelGenerator;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.services.assembler.ProvisioningProductResourceAssembler;
import mc.monacotelecom.services.dto.ProvisioningProductDTO;
import mc.monacotelecom.services.dto.request.CreateProvisioningProductDTO;
import mc.monacotelecom.services.dto.request.UpdateProvisioningProductDTO;
import mc.monacotelecom.services.dto.search.SearchProvisioningProductDTO;
import mc.monacotelecom.services.entity.ProvisioningProduct;
import mc.monacotelecom.services.enums.ProvisioningProductActionRequest;
import mc.monacotelecom.services.enums.ProvisioningTagAction;
import mc.monacotelecom.services.exceptions.SvcNotFoundException;
import mc.monacotelecom.services.exceptions.SvcValidationException;
import mc.monacotelecom.services.importer.data.GenericDataCsvLines.ProvisioningProductCsvLine;
import mc.monacotelecom.services.mapper.ExporterEntityMapper;
import mc.monacotelecom.services.mapper.ProvisioningActionMapper;
import mc.monacotelecom.services.mapper.ProvisioningProductMapper;
import mc.monacotelecom.services.repository.ProvisioningActionRepository;
import mc.monacotelecom.services.repository.ProvisioningProductRepository;
import mc.monacotelecom.services.repository.specification.ProvisioningProductSpecification;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Objects;

import static mc.monacotelecom.services.translation.TranslationMessages.*;


@Component
@RequiredArgsConstructor
public class ProvisioningProductProcess {

    private final ProvisioningProductRepository provisioningProductRepository;
    private final ProvisioningActionRepository provisioningActionRepository;
    private final ProvisioningProductResourceAssembler provisioningProductResourceAssembler = ProvisioningProductResourceAssembler.of(ProvisioningProductProcess.class);
    private final LocalizedMessageBuilder localizedMessageBuilder;
    private final ExcelGenerator excelGenerator;
    private final ExporterEntityMapper exporterEntityMapper;

    public ProvisioningProductDTO getById(Long provisioningActionId, Long provisioningProductId) {
        var provisioningProduct = provisioningProductRepository.findByProvisioningActionInternalIdAndInternalId(provisioningActionId, provisioningProductId)
                .orElseThrow(() -> new SvcValidationException(localizedMessageBuilder, PROVISIONING_PRODUCT_NOT_FOUND_ID_ACTION, provisioningProductId, provisioningActionId));
        return provisioningProductResourceAssembler.toModel(provisioningProduct);
    }

    public ProvisioningProductDTO get(final String productCode,
                                      final ProvisioningProductActionRequest actionRequest,
                                      final String tagCode,
                                      final ProvisioningTagAction tagAction) {
        var provisioningProduct = provisioningProductRepository
                .findByProductCodeAndActionRequestAndProvisioningActionTagTagCodeAndProvisioningActionTagAction(productCode, actionRequest, tagCode, tagAction)
                .orElseThrow(() -> new SvcValidationException(localizedMessageBuilder, PROVISIONING_PRODUCT_NOT_FOUND_CODE_ACTION_TAGCODE_TAGACTION, productCode, actionRequest, tagCode, tagAction));
        return provisioningProductResourceAssembler.toModel(provisioningProduct);
    }

    public Page<ProvisioningProductDTO> search(Pageable pageable, SearchProvisioningProductDTO dto) {
        final Specification<ProvisioningProduct> specification = prepareSpecification(dto);
        Page<ProvisioningProduct> provisioningProducts = provisioningProductRepository.findAll(specification, pageable);
        return provisioningProducts.map(provisioningProductResourceAssembler::toModel);
    }

    public Specification<ProvisioningProduct> prepareSpecification(final SearchProvisioningProductDTO dto) {
        Specification<ProvisioningProduct> specification = null;

        specification = StringUtils.isNotBlank(dto.getCode()) ? Specification.where(ProvisioningProductSpecification.hasCode(dto.getCode())) : null;
        specification = Objects.nonNull(dto.getAction()) ? CommonFunctions.addSpecification(specification, ProvisioningProductSpecification.hasAction(dto.getAction())) : specification;
        specification = StringUtils.isNotBlank(dto.getTagCode()) ? Specification.where(ProvisioningProductSpecification.hasTagCode(dto.getTagCode())) : specification;
        specification = Objects.nonNull(dto.getTagAction()) ? CommonFunctions.addSpecification(specification, ProvisioningProductSpecification.hasTagAction(dto.getTagAction())) : specification;
        specification = Objects.nonNull(dto.getServiceAction()) ? CommonFunctions.addSpecification(specification, ProvisioningProductSpecification.hasServiceAction(dto.getServiceAction())) : specification;

        return specification;
    }

    public Page<ProvisioningProductDTO> getAllProvisioningProductsForProvisioningAction(Long tagActionId, Pageable pageable) {
        return provisioningProductRepository.findByProvisioningActionInternalId(tagActionId, pageable)
                .map(provisioningProductResourceAssembler::toModel);
    }

    public ProvisioningProductDTO add(Long provisioningActionId, CreateProvisioningProductDTO createProvisioningProductDTO) {
        var provisioningAction = provisioningActionRepository.findByInternalId(provisioningActionId)
                .orElseThrow(() -> new SvcValidationException(localizedMessageBuilder, PROVISIONING_ACTION_NOT_FOUND_ID, provisioningActionId));

        ProvisioningProductDTO provisioningProductDTO = new ProvisioningProductDTO();
        provisioningProductDTO.setProductCode(createProvisioningProductDTO.getProductCode());
        provisioningProductDTO.setProductAction(createProvisioningProductDTO.getProductAction());
        provisioningProductDTO.setProvAction(ProvisioningActionMapper.INSTANCE.toDto(provisioningAction));

        var provisioningProduct = ProvisioningProductMapper.INSTANCE.toEntity(provisioningProductDTO);
        provisioningProduct.setProvisioningAction(provisioningAction);
        provisioningProduct.setTagAction(provisioningAction.getTagAction().name());
        provisioningProduct.setTagCode(provisioningAction.getTagCode());
        provisioningProduct = provisioningProductRepository.save(provisioningProduct);
        return provisioningProductResourceAssembler.toModel(provisioningProduct);
    }

    public ProvisioningProductDTO update(Long actionId, Long productId, UpdateProvisioningProductDTO dto) {
        if (!provisioningActionRepository.existsByInternalId(actionId)) {
            throw new SvcValidationException(localizedMessageBuilder, PROVISIONING_ACTION_NOT_FOUND_ID, actionId);
        }

        var provisioningProduct = provisioningProductRepository.findByInternalId(productId)
                .orElseThrow(() -> new SvcValidationException(localizedMessageBuilder, PROVISIONING_PRODUCT_NOT_FOUND_ID, productId));

        return provisioningProductResourceAssembler.toModel(provisioningProductRepository.save(provisioningProduct));
    }

    public void delete(Long provisioningActionId, Long provisioningProductId) {
        if (!provisioningActionRepository.existsByInternalId(provisioningActionId)) {
            throw new SvcNotFoundException(localizedMessageBuilder, PROVISIONING_ACTION_NOT_FOUND_ID, provisioningActionId);
        }

        var provisioningProduct = provisioningProductRepository.findByInternalId(provisioningProductId)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, PROVISIONING_PRODUCT_NOT_FOUND_ID, provisioningProductId));
        provisioningProductRepository.delete(provisioningProduct);
    }

    public ByteArrayInputStream export() {
        final var toExport = provisioningProductRepository.findAll()
                .stream()
                .map(exporterEntityMapper::toProvisioningProductExporter);

        return excelGenerator.writeToExcel(toExport, ProvisioningProductCsvLine.class, "export.provisioningProduct");
    }
}
