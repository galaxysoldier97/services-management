package mc.monacotelecom.services.importer;

import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.inventory.common.importer.process.NamedAbstractImporter;
import mc.monacotelecom.services.entity.ActivationCode;
import mc.monacotelecom.services.importer.data.GenericDataCsvLines.ActivationCodeCsvLine;
import mc.monacotelecom.services.importer.helpers.ActivationCodeImporterHelper;
import mc.monacotelecom.services.importer.tags.Tag;
import mc.monacotelecom.services.repository.ActivationCodeRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class ActivationCodeImporter extends NamedAbstractImporter<ActivationCode, ActivationCodeCsvLine> {

    private final ActivationCodeImporterHelper importerHelper;
    private final ActivationCodeRepository activationCodeRepository;

    protected ActivationCodeImporter(final ActivationCodeImporterHelper importerHelper,
                                     final ActivationCodeRepository activationCodeRepository) {
        super(Tag.SERVICESADMIN);
        this.importerHelper = importerHelper;
        this.activationCodeRepository = activationCodeRepository;
    }

    @Override
    public void preProcess() {
        final var deleted = this.strategy.getImportParameters().getParameter("deleted");
        if (deleted.isPresent() && Boolean.parseBoolean(deleted.get())) {
            log.info("Cleaning table ActivationCode before importing entity");
            this.activationCodeRepository.deleteAll();
        }
    }

    @Override
    public void onParseLine(ActivationCodeCsvLine parsedLine) {
        var activationCode = importerHelper.prepareActivationCode(parsedLine);
        parsedLine.setNodes(Collections.singletonList(activationCode));
    }
}
