package mc.monacotelecom.services.importer.helpers;

import lombok.RequiredArgsConstructor;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.services.entity.ProvisioningTag;
import mc.monacotelecom.services.enums.ServiceCategory;
import mc.monacotelecom.services.exceptions.SvcValidationException;
import mc.monacotelecom.services.repository.ProvisioningTagRepository;
import org.springframework.stereotype.Component;

import javax.validation.Validator;
import java.util.Optional;

import static mc.monacotelecom.services.translation.TranslationMessages.PROVISIONING_TAG_ACCESSTYPE_NULL;

@Component
@RequiredArgsConstructor
public class ProvisioningTagImporterHelper {

    private final ProvisioningTagRepository provisioningTagRepository;
    private final LocalizedMessageBuilder localizedMessageBuilder;
    /**
     * Prepares and validates data before importing
     * a {@link ProvisioningTag}
     *
     * @param provisioningTag
     * @param validator
     * @return
     */
    public ProvisioningTag prepareProvisioningTag(ProvisioningTag provisioningTag, Validator validator) {

        if (provisioningTag.getAccessType() == null && ServiceCategory.ACCESS.equals(provisioningTag.getCategory())) {
            throw new SvcValidationException(localizedMessageBuilder, PROVISIONING_TAG_ACCESSTYPE_NULL);
        }

        validator.validate(provisioningTag).stream()
                .findFirst()
                .ifPresent(error -> {
                    throw new SvcValidationException(localizedMessageBuilder, error.getPropertyPath() + " " + error.getMessage());
                });

        Optional<ProvisioningTag> optionalProvisioningTag = provisioningTagRepository.findById(provisioningTag.getTagCode());
        optionalProvisioningTag.ifPresent(existing -> provisioningTag.setInternalId(existing.getInternalId()));
        return provisioningTag;
    }
}
