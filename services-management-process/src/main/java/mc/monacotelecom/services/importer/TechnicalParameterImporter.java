package mc.monacotelecom.services.importer;

import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.inventory.common.importer.process.NamedAbstractImporter;
import mc.monacotelecom.services.entity.TechnicalParameter;
import mc.monacotelecom.services.importer.data.GenericDataCsvLines;
import mc.monacotelecom.services.importer.helpers.TechnicalParameterImporterHelper;
import mc.monacotelecom.services.importer.tags.Tag;
import mc.monacotelecom.services.repository.TechnicalParameterRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class TechnicalParameterImporter extends NamedAbstractImporter<TechnicalParameter, GenericDataCsvLines.TechnicalParameterCsvLine> {

    private final TechnicalParameterImporterHelper importerHelper;
    private final TechnicalParameterRepository technicalParameterRepository;

    protected TechnicalParameterImporter(final TechnicalParameterImporterHelper importerHelper,
                                         final TechnicalParameterRepository technicalParameterRepository) {
        super(Tag.SERVICESADMIN);
        this.importerHelper = importerHelper;
        this.technicalParameterRepository = technicalParameterRepository;
    }

    @Override
    public void preProcess() {
        final var deleted = this.strategy.getImportParameters().getParameter("deleted");
        if (deleted.isPresent() && Boolean.parseBoolean(deleted.get())) {
            log.info("Cleaning table TechnicalParameter before importing entity");
            this.technicalParameterRepository.deleteAll();
        }
    }

    @Override
    public void onParseLine(GenericDataCsvLines.TechnicalParameterCsvLine parsedLine) {
        var technicalParameter = importerHelper.prepareTechnicalParameter(parsedLine);

        parsedLine.setNodes(Collections.singletonList(technicalParameter));
    }
}
