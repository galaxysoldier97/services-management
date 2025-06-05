package mc.monacotelecom.services.importer.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import mc.monacotelecom.importer.csv.CsvColumn;
import mc.monacotelecom.importer.csv.CsvFileReader.CsvLine;
import mc.monacotelecom.services.entity.Provisioning;

import javax.validation.constraints.NotNull;

public class ProvisioningLines extends CsvLine<Provisioning> {

    public ProvisioningLines() {
        super();
        sheetMapClasses.add(ProvisioningTagCsvLine.class);
        sheetMapClasses.add(ActivationCodeCsvLine.class);
        sheetMapClasses.add(ProvisioningProductCsvLines.class);
        sheetMapClasses.add(ProvisioningParameterCsvLines.class);
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ProvisioningTagCsvLine extends ProvisioningLines implements ImporterInterfaces.IProvisioningTagCsvLines {

        @CsvColumn(0)
        @NotNull String tagCode;

        @CsvColumn(1)
        String description;

        @CsvColumn(2)
        @NotNull String activity;

        @CsvColumn(3)
        String category;

        @CsvColumn(4)
        String accessType;

        @CsvColumn(5)
        @NotNull String componentType;

        @CsvColumn(6)
        String nature;

        @CsvColumn(7)
        String persistent;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ActivationCodeCsvLine extends ProvisioningLines implements ImporterInterfaces.IActivationCodeCsvLines {


        @CsvColumn(0)
        @NotNull String tagCode;

        @CsvColumn(1)
        String activCode;

        @CsvColumn(2)
        String description;

        @CsvColumn(3)
        String nature;

        @CsvColumn(4)
        String networkComponent;

        @CsvColumn(5)
        String tagValue;

        @Override
        public String getActivationDescription() {
            return this.description;
        }

        @Override
        public String getActivationNature() {
            return this.nature;
        }

        @Override
        public String getActivationValue() {
            return this.tagValue;
        }

        @Override
        public void setActivationDescription(String v) {

        }

        @Override
        public void setActivationNature(String v) {

        }

        @Override
        public void setActivationValue(String v) {

        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ProvisioningProductCsvLines extends ProvisioningLines
            implements ImporterInterfaces.IProvisioningProductCsvLines,
            ImporterInterfaces.IProvisioningActionCsvLines {

        @CsvColumn(0)
        String productCode;

        @CsvColumn(1)
        String productAction;

        @CsvColumn(2)
        String tagCode;

        @CsvColumn(3)
        String tagAction;

        @CsvColumn(4)
        String serviceAction;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ProvisioningParameterCsvLines extends ProvisioningLines implements ImporterInterfaces.IProvisioningParameterCsvLines {

        @CsvColumn(0)
        String tagCode;

        @CsvColumn(1)
        String tagAction;

        @CsvColumn(2)
        String parameterCode;

        @CsvColumn(3)
        String parameterType;

        @CsvColumn(4)
        String parameterDescription;

        @CsvColumn(5)
        String parameterValue;
    }
}
