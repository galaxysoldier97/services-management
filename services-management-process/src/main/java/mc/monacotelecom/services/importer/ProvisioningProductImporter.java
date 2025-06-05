package mc.monacotelecom.services.importer;

import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.inventory.common.importer.process.NamedAbstractImporter;
import mc.monacotelecom.services.entity.ProvisioningProduct;
import mc.monacotelecom.services.importer.data.GenericDataCsvLines;
import mc.monacotelecom.services.importer.helpers.ProvisioningProductImporterHelper;
import mc.monacotelecom.services.importer.tags.Tag;
import mc.monacotelecom.services.repository.ProvisioningProductRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
public class ProvisioningProductImporter extends NamedAbstractImporter<ProvisioningProduct, GenericDataCsvLines.ProvisioningProductCsvLine> {

    private final ProvisioningProductImporterHelper importerHelper;
    private final ProvisioningProductRepository provisioningProductRepository;


    protected ProvisioningProductImporter(final ProvisioningProductImporterHelper importerHelper,
                                          final ProvisioningProductRepository provisioningProductRepository) {
        super(Tag.SERVICESADMIN);
        this.importerHelper = importerHelper;
        this.provisioningProductRepository = provisioningProductRepository;
    }

    @Override
    public void preProcess() {
        final var deleted = this.strategy.getImportParameters().getParameter("deleted");
        if (deleted.isPresent() && Boolean.parseBoolean(deleted.get())) {
            log.info("Cleaning table ProvisioningProduct before importing entity");
            this.provisioningProductRepository.deleteAll();
        }
    }

    @Override
    public void onParseLine(GenericDataCsvLines.ProvisioningProductCsvLine parsedLine) {

        var provisioningProduct = importerHelper.prepareProvisioningProduct(parsedLine, Optional.empty());

        parsedLine.setNodes(Collections.singletonList(provisioningProduct));
    }
}
