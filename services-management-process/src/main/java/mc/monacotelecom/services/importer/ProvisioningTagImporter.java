package mc.monacotelecom.services.importer;

import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.importer.ImportMapper;
import mc.monacotelecom.inventory.common.importer.process.NamedAbstractImporter;
import mc.monacotelecom.services.entity.ProvisioningTag;
import mc.monacotelecom.services.importer.helpers.ProvisioningProductImporterHelper;
import mc.monacotelecom.services.importer.helpers.ProvisioningTagImporterHelper;
import mc.monacotelecom.services.importer.tags.Tag;
import mc.monacotelecom.services.repository.ProvisioningTagRepository;
import org.springframework.stereotype.Service;

import javax.validation.Validator;

@Slf4j
@Service
public class ProvisioningTagImporter extends NamedAbstractImporter<ProvisioningTag, ImportMapper.MappedLine<ProvisioningTag>> {

    private final ProvisioningTagImporterHelper importerHelper;
    private final ProvisioningTagRepository provisioningTagRepository;
    private final Validator validator;

    protected ProvisioningTagImporter(final ProvisioningTagImporterHelper importerHelper,
                                      final ProvisioningTagRepository provisioningTagRepository,
                                      final Validator validator) {
        super(Tag.SERVICESADMIN);
        this.importerHelper = importerHelper;
        this.provisioningTagRepository = provisioningTagRepository;
        this.validator = validator;
    }

    @Override
    public void preProcess() {
        final var deleted = this.strategy.getImportParameters().getParameter("deleted");
        if (deleted.isPresent() && Boolean.parseBoolean(deleted.get())) {
            log.info("Cleaning table ProvisioningTag before importing entity");
            this.provisioningTagRepository.deleteAll();
        }
    }

    @Override
    public void onParseLine(ImportMapper.MappedLine<ProvisioningTag> parsedLine) {
        parsedLine.getNodes().forEach(provisioningTag -> {
            importerHelper.prepareProvisioningTag(provisioningTag, validator);
            validate(provisioningTag, parsedLine.getSaveDepth().get());
        });
    }
}
