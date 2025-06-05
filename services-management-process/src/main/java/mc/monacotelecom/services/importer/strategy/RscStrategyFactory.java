package mc.monacotelecom.services.importer.strategy;

import lombok.EqualsAndHashCode;
import mc.monacotelecom.importer.ImportStrategy;
import mc.monacotelecom.importer.csv.CsvImportStrategyBuilder;
import mc.monacotelecom.inventory.common.importer.process.strategy.CommonImportStrategyFactory;
import mc.monacotelecom.services.entity.*;
import mc.monacotelecom.services.importer.data.GenericDataCsvImportMappers.ProvisioningActionMapper;
import mc.monacotelecom.services.importer.data.GenericDataCsvImportMappers.ProvisioningTagMapper;
import mc.monacotelecom.services.importer.data.GenericDataCsvLines.*;
import mc.monacotelecom.services.importer.data.ProvisioningLines;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Component
@EqualsAndHashCode(callSuper = true)
public class RscStrategyFactory extends CommonImportStrategyFactory {

    private final ProvisioningTagMapper provisioningTagMapper;
    private final ProvisioningActionMapper provisioningActionMapper;
    private final ConversionService conversionService;

    public RscStrategyFactory(final ProvisioningTagMapper provisioningTagMapper,
                              final ProvisioningActionMapper provisioningActionMapper,
                              final @Qualifier("mvcConversionService") ConversionService conversionService) {
        this.provisioningTagMapper = provisioningTagMapper;
        this.provisioningActionMapper = provisioningActionMapper;
        this.conversionService = conversionService;
    }

    @PostConstruct
    void setup() {
        var strategies = new ArrayList<ImportStrategy<?, ?>>();

        strategies.add(new CsvImportStrategyBuilder<ProvisioningTag, ProvisioningTagCsvLine>() {
        }.withConversionService(conversionService).withRowMapper(provisioningTagMapper).withPostProcessCondition(ImportStrategy.PostProcessCondition.ON_EACH_LINE).build());

        strategies.add(new CsvImportStrategyBuilder<ProvisioningAction, ProvisioningActionCsvLine>() {
        }.withConversionService(conversionService).withRowMapper(provisioningActionMapper).withPostProcessCondition(ImportStrategy.PostProcessCondition.ON_EACH_LINE).build());

        strategies.add(new CsvImportStrategyBuilder<ActivationCode, ActivationCodeCsvLine>() {
        }.withConversionService(conversionService).build());

        strategies.add(new CsvImportStrategyBuilder<ProvisioningProduct, ProvisioningProductCsvLine>() {
        }.withConversionService(conversionService).build());

        strategies.add(new CsvImportStrategyBuilder<TechnicalParameter, TechnicalParameterCsvLine>() {
        }.withConversionService(conversionService).build());

        strategies.add(new CsvImportStrategyBuilder<Provisioning, ProvisioningLines>() {
        }.withConversionService(conversionService).build());

        this.setStrategies(strategies);
    }
}
