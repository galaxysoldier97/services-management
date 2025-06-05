package mc.monacotelecom.services.importer.helpers;

import lombok.RequiredArgsConstructor;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.services.entity.ActivationCode;
import mc.monacotelecom.services.entity.TagActivation;
import mc.monacotelecom.services.enums.ActivationNature;
import mc.monacotelecom.services.enums.NetworkComponent;
import mc.monacotelecom.services.exceptions.SvcValidationException;
import mc.monacotelecom.services.importer.data.ImporterInterfaces;
import mc.monacotelecom.services.importer.data.ImporterInterfaces.IActivationCodeCsvLines;
import mc.monacotelecom.services.repository.ActivationCodeRepository;
import mc.monacotelecom.services.repository.ProvisioningTagRepository;
import mc.monacotelecom.services.repository.TagActivationRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static mc.monacotelecom.services.translation.TranslationMessages.PROVISIONING_TAG_NOT_FOUND_CODE;

@Component
@RequiredArgsConstructor
public class ActivationCodeImporterHelper {

    private final TagActivationRepository tagActivationRepository;
    private final ProvisioningTagRepository provisioningTagRepository;
    private final ActivationCodeRepository activationCodeRepository;
    private final LocalizedMessageBuilder localizedMessageBuilder;
    /**
     * Prepares and validates data before importing
     * a {@link ActivationCode}
     *
     * @param parsedLine
     * @return
     */
    public ActivationCode prepareActivationCode(IActivationCodeCsvLines parsedLine) {
        var provisioningTag = provisioningTagRepository.findById(parsedLine.getTagCode())
                .orElseThrow(() -> new SvcValidationException(localizedMessageBuilder, PROVISIONING_TAG_NOT_FOUND_CODE, parsedLine.getTagCode()));

        Optional<ActivationCode> optionalActivationCode = activationCodeRepository.findById(parsedLine.getActivCode());

        ActivationCode activationCode;
        activationCodeInit(parsedLine);
        if (optionalActivationCode.isPresent()) {
            activationCode = optionalActivationCode.get();

            Optional<TagActivation> optionalTagActivation = tagActivationRepository
                    .findByTagCodeAndActvCode(provisioningTag.getTagCode(), activationCode.getActivCode());

            if (optionalTagActivation.isPresent()) {
                for (TagActivation t : activationCode.getTagActivations()) {
                    if (t.getActivationCode().getActivCode().equals(activationCode.getActivCode()) && t.getProvisioningTag().getTagCode().equals(provisioningTag.getTagCode())) {
                        Optional.ofNullable(parsedLine.getActivationValue())
                                .filter(StringUtils::isNotBlank)
                                .map(Long::valueOf).ifPresent(t::setTagValue);
                        break;
                    }
                }

            } else {
                TagActivation tagActivation = new TagActivation();
                tagActivation.setProvisioningTag(provisioningTag);
                tagActivation.setActivationCode(activationCode);
                tagActivation.setActvCode(activationCode.getActivCode());
                tagActivation.setTagCode(provisioningTag.getTagCode());

                Optional.ofNullable(parsedLine.getActivationValue())
                        .filter(StringUtils::isNotBlank)
                        .map(Long::valueOf).ifPresent(tagActivation::setTagValue);
                activationCode.getTagActivations().add(tagActivation);
            }

            activationCode.setDescription(parsedLine.getActivationDescription());
            activationCode.setNature(ActivationNature.valueOf(parsedLine.getActivationNature()));
            activationCode.setNetworkComponent(NetworkComponent.valueOf(parsedLine.getNetworkComponent()));

        } else {
            activationCode = new ActivationCode();
            activationCode.setActivCode(parsedLine.getActivCode());
            activationCode.setDescription(parsedLine.getActivationDescription());
            activationCode.setNature(ActivationNature.valueOf(parsedLine.getActivationNature()));
            activationCode.setNetworkComponent(NetworkComponent.valueOf(parsedLine.getNetworkComponent()));

            TagActivation tagActivation = new TagActivation();
            tagActivation.setActvCode(activationCode.getActivCode());
            tagActivation.setTagCode(provisioningTag.getTagCode());
            tagActivation.setProvisioningTag(provisioningTag);
            tagActivation.setActivationCode(activationCode);

            Optional.ofNullable(parsedLine.getActivationValue())
                    .filter(StringUtils::isNotBlank)
                    .map(Long::valueOf).ifPresent(tagActivation::setTagValue);

            Set<TagActivation> tagActivations = new HashSet<>();
            tagActivations.add(tagActivation);
            activationCode.setTagActivations(tagActivations);
        }

        return activationCode;
    }

    private void activationCodeInit(IActivationCodeCsvLines parsedLine) {
        Optional.ofNullable(parsedLine.getActivationNature())
                .filter(StringUtils::isNotBlank)
                .ifPresent(value -> parsedLine.setActivationNature(parsedLine.getActivationNature().toUpperCase()));

        Optional.ofNullable(parsedLine.getNetworkComponent())
                .filter(StringUtils::isNotBlank)
                .ifPresent(value -> parsedLine.setNetworkComponent(parsedLine.getNetworkComponent().toUpperCase()));
    }
}
