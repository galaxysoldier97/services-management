package mc.monacotelecom.services.importer;

import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.inventory.common.importer.process.NamedAbstractImporter;
import mc.monacotelecom.services.entity.Provisioning;
import mc.monacotelecom.services.importer.data.ImporterInterfaces;
import mc.monacotelecom.services.importer.data.ProvisioningLines;
import mc.monacotelecom.services.importer.data.ProvisioningLines.ActivationCodeCsvLine;
import mc.monacotelecom.services.importer.data.ProvisioningLines.ProvisioningParameterCsvLines;
import mc.monacotelecom.services.importer.data.ProvisioningLines.ProvisioningProductCsvLines;
import mc.monacotelecom.services.importer.data.ProvisioningLines.ProvisioningTagCsvLine;
import mc.monacotelecom.services.importer.helpers.ActivationCodeImporterHelper;
import mc.monacotelecom.services.importer.helpers.ProvisioningProductImporterHelper;
import mc.monacotelecom.services.importer.helpers.ProvisioningTagImporterHelper;
import mc.monacotelecom.services.importer.helpers.TechnicalParameterImporterHelper;
import mc.monacotelecom.services.importer.tags.Tag;
import mc.monacotelecom.services.mapper.ProvisioningTagMapper;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.Collections;

@Slf4j
@Service
public class ProvisioningImporter extends NamedAbstractImporter<Provisioning, ProvisioningLines> {

    private final ProvisioningTagImporterHelper provisioningTagImporterHelper;
    private final ActivationCodeImporterHelper activationCodeImporterHelper;
    private final ProvisioningProductImporterHelper provisioningProductImporterHelper;
    private final TechnicalParameterImporterHelper technicalParameterImporterHelper;
    private final Validator validator;

    protected ProvisioningImporter(final ProvisioningTagImporterHelper provisioningTagImporterHelper,
                                   final ActivationCodeImporterHelper activationCodeImporterHelper,
                                   final ProvisioningProductImporterHelper provisioningProductImporterHelper,
                                   final TechnicalParameterImporterHelper technicalParameterImporterHelper,
                                   final Validator validator) {
        super(Tag.SERVICESADMIN);
        this.provisioningTagImporterHelper = provisioningTagImporterHelper;
        this.activationCodeImporterHelper = activationCodeImporterHelper;
        this.provisioningProductImporterHelper = provisioningProductImporterHelper;
        this.technicalParameterImporterHelper = technicalParameterImporterHelper;
        this.validator = validator;
    }

    @Override
    public void onParseLine(ProvisioningLines provisioningLines) {
        var instance = provisioningLines.getInstance();

        if (instance instanceof ProvisioningTagCsvLine) {
            var provisioningTag = provisioningTagImporterHelper.prepareProvisioningTag(ProvisioningTagMapper.INSTANCE.mapProvisioningTag((ProvisioningTagCsvLine) instance), validator);
            provisioningLines.setNodes(Collections.singletonList(provisioningTag));
        } else if (instance instanceof ActivationCodeCsvLine) {
            var activationCode = activationCodeImporterHelper.prepareActivationCode((ActivationCodeCsvLine) instance);
            provisioningLines.setNodes(Collections.singletonList(activationCode));
        } else if (instance instanceof ProvisioningProductCsvLines) {
            var provisioningAction = provisioningProductImporterHelper.prepareProvisionActionAndProduct((ProvisioningProductCsvLines) instance);
            provisioningLines.setNodes(Collections.singletonList(provisioningAction));
        } else if (instance instanceof ProvisioningParameterCsvLines) {
            var technicalParameter = technicalParameterImporterHelper.prepareTechnicalParameter((ProvisioningParameterCsvLines) instance);

            provisioningLines.setNodes(Collections.singletonList(technicalParameter));
        }
    }
}
