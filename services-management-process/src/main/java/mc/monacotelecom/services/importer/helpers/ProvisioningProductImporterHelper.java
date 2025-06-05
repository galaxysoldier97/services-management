package mc.monacotelecom.services.importer.helpers;

import lombok.RequiredArgsConstructor;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.services.entity.ProvisioningAction;
import mc.monacotelecom.services.entity.ProvisioningProduct;
import mc.monacotelecom.services.entity.ProvisioningTag;
import mc.monacotelecom.services.enums.ProvisioningProductActionRequest;
import mc.monacotelecom.services.enums.ProvisioningTagAction;
import mc.monacotelecom.services.exceptions.SvcValidationException;
import mc.monacotelecom.services.importer.data.ImporterInterfaces;
import mc.monacotelecom.services.importer.data.ProvisioningLines;
import mc.monacotelecom.services.importer.data.ProvisioningLines.ProvisioningProductCsvLines;
import mc.monacotelecom.services.mapper.ProvisioningActionMapper;
import mc.monacotelecom.services.repository.ProvisioningActionRepository;
import mc.monacotelecom.services.repository.ProvisioningProductRepository;
import mc.monacotelecom.services.repository.ProvisioningTagRepository;
import mc.monacotelecom.services.translation.TranslationMessages;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static mc.monacotelecom.services.translation.TranslationMessages.PROVISIONING_ACTION_NOT_FOUND_TAG_CODE_ACTION;

@Component
@RequiredArgsConstructor
public class ProvisioningProductImporterHelper {

    private final ProvisioningTagRepository provisioningTagRepository;
    private final ProvisioningProductRepository provisioningProductRepository;
    private final ProvisioningActionRepository provisioningActionRepository;
    private final LocalizedMessageBuilder localizedMessageBuilder;
    /**
     * Prepares and validates data before importing
     * a {@link ProvisioningProduct}
     *
     * @param parsedLine
     * @param provisioningActionOpt
     * @return
     */
    public ProvisioningProduct prepareProvisioningProduct(ImporterInterfaces.IProvisioningProductCsvLines parsedLine, Optional<ProvisioningAction> provisioningActionOpt) {
        // Finding the old provisioning product if it exists
        var provisioningProduct = provisioningProductRepository.findByProductCodeAndActionRequestAndProvisioningActionTagTagCodeAndProvisioningActionTagAction(parsedLine.getProductCode(),
                        ProvisioningProductActionRequest.valueOf(parsedLine.getProductAction()),
                        parsedLine.getTagCode(),
                        ProvisioningTagAction.valueOf(parsedLine.getTagAction()))
                .orElseGet((ProvisioningProduct::new));

        var provisioningAction = provisioningActionOpt.orElseGet(() -> provisioningActionRepository.findByTagTagCodeAndTagAction(parsedLine.getTagCode(), ProvisioningTagAction.valueOf(parsedLine.getTagAction()))
                .orElseThrow(() -> new SvcValidationException(localizedMessageBuilder, PROVISIONING_ACTION_NOT_FOUND_TAG_CODE_ACTION, parsedLine.getTagCode(), parsedLine.getTagAction())));

        provisioningProduct.setProvisioningAction(provisioningAction);
        provisioningProduct.setProductCode(parsedLine.getProductCode());
        provisioningProduct.setTagCode(provisioningAction.getTagCode());
        provisioningProduct.setTagAction(provisioningAction.getTagAction().name());
        provisioningProduct.setActionRequest(ProvisioningProductActionRequest.valueOf(parsedLine.getProductAction()));

        return provisioningProduct;
    }

    /**
     * Prepares and validates data before importing
     * a {@link ProvisioningAction}
     *
     * @param provisioningAction
     * @return
     */
    public ProvisioningAction prepareProvisioningAction(ProvisioningAction provisioningAction) {

        var provisioningTag = provisioningTagRepository.findById(provisioningAction.getTagCode())
                .orElseThrow(() -> new SvcValidationException(localizedMessageBuilder, TranslationMessages.PROVISIONING_TAG_NOT_FOUND_CODE, provisioningAction.getTagCode()));

        Optional<ProvisioningAction> optionalProvisioningAction = provisioningActionRepository.findByTagTagCodeAndTagAction(provisioningAction.getTagCode(), provisioningAction.getTagAction());
        optionalProvisioningAction.ifPresent(existing -> provisioningAction.setInternalId(existing.getInternalId()));

        provisioningAction.setTag(provisioningTag);
        return provisioningAction;
    }

    /**
     * Prepares and validates data before importing
     * a {@link ProvisioningAction} and a {@link ProvisioningProduct}
     *
     * @param parsedLine
     * @return
     */
    public ProvisioningAction prepareProvisionActionAndProduct(ProvisioningProductCsvLines parsedLine) {

        var provisioningAction = prepareProvisioningAction(ProvisioningActionMapper.INSTANCE.mapProvisioningAction(parsedLine));
        var provisioningProduct = prepareProvisioningProduct(parsedLine, Optional.of(provisioningAction));
        provisioningAction.getProvisioningProducts().add(provisioningProduct);
        return provisioningAction;
    }
}
