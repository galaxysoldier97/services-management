package mc.monacotelecom.services.importer.data;

import mc.monacotelecom.importer.csv.CsvFileReader;
import mc.monacotelecom.services.entity.ProvisioningAction;
import mc.monacotelecom.services.entity.ProvisioningTag;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ProvisioningTagActivity;
import mc.monacotelecom.services.enums.ProvisioningTagNature;
import mc.monacotelecom.services.enums.ServiceCategory;
import mc.monacotelecom.services.exceptions.SvcValidationException;
import mc.monacotelecom.services.importer.data.GenericDataCsvLines.ProvisioningTagCsvLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;

public interface GenericDataCsvImportMappers {

    @Mapper(componentModel = "spring")
    interface ProvisioningTagMapper extends CsvFileReader.CsvImportMapper<ProvisioningTag, ProvisioningTagCsvLine> {

        @Mapping(source = "line", target = "persistent", qualifiedByName = "getPersistent")
        @Mapping(source = "line", target = "activity", qualifiedByName = "getActivity")
        @Mapping(source = "line", target = "category", qualifiedByName = "getCategory")
        @Mapping(source = "line", target = "accessType", qualifiedByName = "getAccessType")
        @Mapping(source = "line", target = "nature", qualifiedByName = "getNature")
        ProvisioningTag toNode(ProvisioningTagCsvLine line) throws SvcValidationException;

        @Named("getPersistent")
        static Boolean getPersistent(ProvisioningTagCsvLine provisioningTagCsvLine) {
            return "O".equals(provisioningTagCsvLine.getPersistent());
        }

        @Named("getActivity")
        static ProvisioningTagActivity getActivity(ProvisioningTagCsvLine provisioningTagCsvLine) {
            return Optional.ofNullable(provisioningTagCsvLine.getActivity())
                    .map(x -> ProvisioningTagActivity.valueOf(x.toUpperCase()))
                    .orElse(null);
        }

        @Named("getCategory")
        static ServiceCategory getCategory(ProvisioningTagCsvLine provisioningTagCsvLine) {
            return Optional.ofNullable(provisioningTagCsvLine.getCategory())
                    .map(x -> ServiceCategory.valueOf(x.toUpperCase()))
                    .orElse(null);
        }

        @Named("getAccessType")
        static Network getAccessType(ProvisioningTagCsvLine provisioningTagCsvLine) {
            return Optional.ofNullable(provisioningTagCsvLine.getAccessType())
                    .map(x -> Network.valueOf(x.toUpperCase()))
                    .orElse(null);
        }

        @Named("getNature")
        static ProvisioningTagNature getNature(ProvisioningTagCsvLine provisioningTagCsvLine) {
            return Optional.ofNullable(provisioningTagCsvLine.getNature())
                    .map(x -> ProvisioningTagNature.valueOf(x.toUpperCase()))
                    .orElse(null);
        }
    }

    @Mapper(componentModel = "spring")
    interface ProvisioningActionMapper extends CsvFileReader.CsvImportMapper<ProvisioningAction, GenericDataCsvLines.ProvisioningActionCsvLine> {

        default ProvisioningTag toProvisioningTag(GenericDataCsvLines.ProvisioningActionCsvLine provisioningActionCsvLine) {
            ProvisioningTag provisioningTag = new ProvisioningTag();
            provisioningTag.setTagCode(provisioningActionCsvLine.getTagCode());
            return provisioningTag;
        }

        @Override
        @Mapping(target = "tag", expression = "java(toProvisioningTag(provisioningActionCsvLine))")
        @Mapping(target = "tagAction", source = "tagAction")
        ProvisioningAction toNode(GenericDataCsvLines.ProvisioningActionCsvLine provisioningActionCsvLine);
    }

}
