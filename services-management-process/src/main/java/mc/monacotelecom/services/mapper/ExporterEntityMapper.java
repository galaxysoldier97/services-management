package mc.monacotelecom.services.mapper;

import mc.monacotelecom.services.entity.*;
import mc.monacotelecom.services.importer.data.GenericDataCsvLines.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.stream.Stream;

import static mc.monacotelecom.inventory.common.CommonFunctions.valueOf;

/**
 * Preparing entities before being
 * exported as file excel
 */
@Mapper(componentModel = "spring")
public interface ExporterEntityMapper {

    @Mapping(target = "persistent", qualifiedByName = "mapBoolean", source = "persistent")
    @Mapping(target = "instance", ignore = true)
    ProvisioningTagCsvLine toProvisioningTagExporter(ProvisioningTag provisioningTag);

    @Mapping(target = "tagAction", source = "tagAction")
    @Mapping(target = "tagCode", source = "tag.tagCode")
    @Mapping(target = "instance", ignore = true)
    ProvisioningActionCsvLine toProvisioningActionExporter(ProvisioningAction provisioningAction);

    @Mapping(target = "productCode", source = "productCode")
    @Mapping(target = "productAction", source = "actionRequest")
    @Mapping(target = "tagAction", source = "provisioningAction.tagAction")
    @Mapping(target = "tagCode", source = "provisioningAction.tag.tagCode")
    @Mapping(target = "instance", ignore = true)
    @Mapping(target = "serviceAction", source = "provisioningAction.serviceAction")
    ProvisioningProductCsvLine toProvisioningProductExporter(ProvisioningProduct provisioningProduct);

    default Stream<ActivationCodeCsvLine> toActivationCodeExporter(ActivationCode activationCode) {
        return activationCode.getTagActivations()
                .parallelStream()
                .map(tagActivation -> {
                    var activationCodeExport = new ActivationCodeCsvLine();
                    activationCodeExport.setActivationNature(valueOf(activationCode.getNature()));
                    activationCodeExport.setActivationDescription(activationCode.getDescription());
                    activationCodeExport.setNetworkComponent(valueOf(activationCode.getNetworkComponent()));
                    activationCodeExport.setActivCode(activationCode.getActivCode());
                    activationCodeExport.setActivationValue(valueOf(tagActivation.getTagValue()));
                    activationCodeExport.setTagCode(tagActivation.getProvisioningTag().getTagCode());
                    return activationCodeExport;
                });
    }

    default Stream<TechnicalParameterCsvLine> toTechnicalParamsExporter(TechnicalParameter technicalParameter) {
        return technicalParameter.getProvisioningActionParameters()
                .parallelStream()
                .map(provisioningActionParameter -> {
                    var technicalParamsExport = new TechnicalParameterCsvLine();
                    technicalParamsExport.setParameterDescription(technicalParameter.getDescription());
                    technicalParamsExport.setParameterCode(technicalParameter.getParameterCode());
                    technicalParamsExport.setParameterType(valueOf(technicalParameter.getParameterType()));
                    technicalParamsExport.setTagAction(valueOf(provisioningActionParameter.getProvisioningAction().getTagAction()));
                    technicalParamsExport.setTagCode(provisioningActionParameter.getProvisioningAction().getTag().getTagCode());
                    technicalParamsExport.setParameterValue(provisioningActionParameter.getParameterValue());

                    return technicalParamsExport;
                });
    }

    @Named("mapBoolean")
    static String mapBoolean(Boolean persistent) {
        return Boolean.TRUE.equals(persistent) ? "O" : "N";
    }
}
