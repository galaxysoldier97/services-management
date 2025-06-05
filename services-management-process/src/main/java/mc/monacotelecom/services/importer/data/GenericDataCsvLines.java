package mc.monacotelecom.services.importer.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import mc.monacotelecom.importer.csv.CsvColumn;
import mc.monacotelecom.importer.csv.CsvFileReader.CsvLine;
import mc.monacotelecom.inventory.common.exporter.annotations.Exported;
import mc.monacotelecom.services.entity.*;

import javax.validation.constraints.NotNull;

public interface GenericDataCsvLines {

    @Data
    @EqualsAndHashCode(callSuper = true)
    class ProvisioningTagCsvLine extends CsvLine<ProvisioningTag> implements ImporterInterfaces.IProvisioningTagCsvLines {

        @Exported
        @CsvColumn(0)
        @NotNull
        String tagCode;

        @Exported(order = 1)
        @CsvColumn(1)
        String description;

        @Exported(order = 2)
        @CsvColumn(2)
        @NotNull
        String activity;

        @Exported(order = 3)
        @CsvColumn(3)
        String category;

        @Exported(order = 4)
        @CsvColumn(4)
        String accessType;

        @Exported(order = 5)
        @CsvColumn(7)
        String componentType;

        @Exported(order = 6)
        @CsvColumn(5)
        @NotNull
        String nature;

        @Exported(order = 7)
        @CsvColumn(6)
        String persistent;

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    class ActivationCodeCsvLine extends CsvLine<ActivationCode> implements ImporterInterfaces.IActivationCodeCsvLines {

        @Exported
        @CsvColumn(0)
        @NotNull
        String tagCode;

        @Exported
        @CsvColumn(1)
        String activCode;

        @Exported
        @CsvColumn(2)
        String activationDescription;

        @Exported
        @CsvColumn(3)
        String activationNature;

        @Exported
        @CsvColumn(4)
        String networkComponent;

        @Exported
        @CsvColumn(5)
        String activationValue;

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    class ProvisioningActionCsvLine extends CsvLine<ProvisioningAction> implements ImporterInterfaces.IProvisioningActionCsvLines {

        @Exported
        @CsvColumn(0)
        String tagCode;

        @Exported
        @CsvColumn(1)
        String tagAction;

        @Exported
        @CsvColumn(2)
        String serviceAction;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    class ProvisioningProductCsvLine extends CsvLine<ProvisioningProduct> implements ImporterInterfaces.IProvisioningProductCsvLines {

        @Exported
        @CsvColumn(0)
        String productCode;

        @Exported
        @CsvColumn(1)
        String productAction;

        @Exported
        @CsvColumn(2)
        String tagCode;

        @Exported
        @CsvColumn(3)
        String tagAction;

        @Exported
        @CsvColumn(4)
        String serviceAction;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    class TechnicalParameterCsvLine extends CsvLine<TechnicalParameter> implements ImporterInterfaces.IProvisioningParameterCsvLines {

        @Exported
        @CsvColumn(0)
        String tagCode;

        @Exported
        @CsvColumn(1)
        String tagAction;

        @Exported
        @CsvColumn(2)
        String parameterCode;

        @Exported
        @CsvColumn(3)
        String parameterType;

        @Exported
        @CsvColumn(4)
        String parameterDescription;

        @Exported
        @CsvColumn(5)
        String parameterValue;
    }

}
