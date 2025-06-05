package mc.monacotelecom.services.importer;

import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.importer.ImportMapper;
import mc.monacotelecom.inventory.common.importer.process.NamedAbstractImporter;
import mc.monacotelecom.services.entity.ProvisioningAction;
import mc.monacotelecom.services.importer.helpers.ProvisioningProductImporterHelper;
import mc.monacotelecom.services.importer.tags.Tag;
import mc.monacotelecom.services.repository.ProvisioningActionRepository;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ProvisioningActionImporter extends NamedAbstractImporter<ProvisioningAction, ImportMapper.MappedLine<ProvisioningAction>> {

    private final ProvisioningProductImporterHelper importerHelper;
    private final ProvisioningActionRepository provisioningActionRepository;


    protected ProvisioningActionImporter(final ProvisioningProductImporterHelper importerHelper,
                                         final ProvisioningActionRepository provisioningActionRepository) {
        super(Tag.SERVICESADMIN);
        this.importerHelper = importerHelper;
        this.provisioningActionRepository = provisioningActionRepository;
    }

    @Override
    public void preProcess() {
        final var deleted = this.strategy.getImportParameters().getParameter("deleted");
        if (deleted.isPresent() && Boolean.parseBoolean(deleted.get())) {
            log.info("Cleaning table ProvisioningAction before importing entity");
            this.provisioningActionRepository.deleteAll();
        }
    }

    @Override
    public void onParseLine(ImportMapper.MappedLine<ProvisioningAction> parsedLine) {
        parsedLine.getNodes().forEach(importerHelper::prepareProvisioningAction);
    }
}
