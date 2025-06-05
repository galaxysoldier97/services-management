package mc.monacotelecom.services.importer.helpers;

import lombok.RequiredArgsConstructor;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.services.entity.ProvisioningActionParameter;
import mc.monacotelecom.services.entity.TechnicalParameter;
import mc.monacotelecom.services.enums.ProvisioningTagAction;
import mc.monacotelecom.services.enums.TechnicalParameterType;
import mc.monacotelecom.services.exceptions.SvcValidationException;
import mc.monacotelecom.services.importer.data.ImporterInterfaces;
import mc.monacotelecom.services.importer.data.ImporterInterfaces.IProvisioningParameterCsvLines;
import mc.monacotelecom.services.repository.ProvisioningActionParameterRepository;
import mc.monacotelecom.services.repository.ProvisioningActionRepository;
import mc.monacotelecom.services.repository.TechnicalParameterRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static mc.monacotelecom.services.translation.TranslationMessages.PROVISIONING_ACTION_NOT_FOUND_TAG_CODE_ACTION;
import static mc.monacotelecom.services.translation.TranslationMessages.TECHNICAL_PARAMETER_TYPE_EMPTY;

@Component
@RequiredArgsConstructor
public class TechnicalParameterImporterHelper {

    private final ProvisioningActionRepository provisioningActionRepository;
    private final TechnicalParameterRepository technicalParameterRepository;
    private final ProvisioningActionParameterRepository provisioningActionParameterRepository;
    private final LocalizedMessageBuilder localizedMessageBuilder;
    /**
     * Prepares and validates data before importing
     * a {@link TechnicalParameter}
     *
     * @param parsedLine
     * @return
     */
    public TechnicalParameter prepareTechnicalParameter(IProvisioningParameterCsvLines parsedLine) {
        var provisioningAction = provisioningActionRepository.findByTagTagCodeAndTagAction(parsedLine.getTagCode(), ProvisioningTagAction.valueOf(parsedLine.getTagAction()))
                .orElseThrow(() -> new SvcValidationException(localizedMessageBuilder, PROVISIONING_ACTION_NOT_FOUND_TAG_CODE_ACTION, parsedLine.getTagCode(), parsedLine.getTagAction()));

        if (StringUtils.isBlank(parsedLine.getParameterType())) {
            throw new SvcValidationException(localizedMessageBuilder, TECHNICAL_PARAMETER_TYPE_EMPTY);
        }

        Optional<TechnicalParameter> optionalTechnicalParameter = technicalParameterRepository
                .findByParameterCodeAndParameterType(parsedLine.getParameterCode(), TechnicalParameterType.valueOf(parsedLine.getParameterType()));

        TechnicalParameter technicalParameter;
        if (optionalTechnicalParameter.isPresent()) {
            technicalParameter = optionalTechnicalParameter.get();
            technicalParameter.setDescription(parsedLine.getParameterDescription());
            technicalParameter.setParameterType(TechnicalParameterType.valueOf(parsedLine.getParameterType()));

        } else {
            technicalParameter = new TechnicalParameter();
            technicalParameter.setParameterCode(parsedLine.getParameterCode());
            technicalParameter.setParameterType(TechnicalParameterType.valueOf(parsedLine.getParameterType()));
            technicalParameter.setDescription(parsedLine.getParameterDescription());
            technicalParameter.setProvisioningActionParameters(new ArrayList<>());
        }

        Optional<ProvisioningActionParameter> optionalProvisioningActionParameter = provisioningActionParameterRepository
                .findByProvisioningActionInternalIdAndTechnicalParameterInternalId(provisioningAction.getInternalId(), technicalParameter.getInternalId());

        if (optionalProvisioningActionParameter.isPresent()) {
            ProvisioningActionParameter provisioningActionParameter = optionalProvisioningActionParameter.get();
            provisioningActionParameter.setParameterValue(parsedLine.getParameterValue());
            List<ProvisioningActionParameter> provisioningActionParameters = new ArrayList<>();
            provisioningActionParameters.add(provisioningActionParameter);
            technicalParameter.setProvisioningActionParameters(provisioningActionParameters);
            // Note: it's currently impossible to change the provisioning tag / provisioning action of an existing parameter
        } else {
            var provisioningActionParameter = new ProvisioningActionParameter(provisioningAction.getTagCode(), provisioningAction.getTagAction(), technicalParameter.getParameterCode(), technicalParameter.getParameterType(),
                    provisioningAction, technicalParameter, parsedLine.getParameterValue());

            List<ProvisioningActionParameter> provisioningActionParameters = new ArrayList<>();
            provisioningActionParameters.add(provisioningActionParameter);
            technicalParameter.setProvisioningActionParameters(provisioningActionParameters);
        }

        return technicalParameter;
    }
}
