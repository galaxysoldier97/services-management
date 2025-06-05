package mc.monacotelecom.services.process;

import mc.monacotelecom.services.entity.*;
import mc.monacotelecom.services.enums.*;

import java.util.HashSet;
import java.util.List;

public final class ChangeTagsTestsHelper {
    public static final long SERVICE_ID = 1L;
    public static final String TAG_CODE = "TAG_CODE";
    public static final String ACTIV_CODE = "ACTIV_CODE";
    public static final String ACTIV_CODE_DESC = "ACTIV_CODE_DESC";
    public static final String TAG_DESCRIPTION = "TAG_DESCRIPTION";
    public static final String TAG_CODE_2 = "TAG_CODE_2";
    public static final String ACTIV_CODE_2 = "ACTIV_CODE_2";

    public static ServiceAccess getServiceAccess() {
        var service = new ServiceAccess();
        service.setServiceId(SERVICE_ID);
        service.setServiceCategory(ServiceCategory.ACCESS);
        service.setServiceActivity(ServiceActivity.INTERNET);
        service.setAccessType(Network.DOCSIS);
        return service;
    }

    public static ServiceComponent getServiceComponent() {
        var service = new ServiceComponent();
        service.setServiceId(SERVICE_ID);
        service.setServiceCategory(ServiceCategory.COMPONENT);
        service.setServiceActivity(ServiceActivity.INTERNET);
        return service;
    }

    public static ProvisioningTag getProvisioningTag() {
        ActivationCode activationCode = getActivationCode();

        return getProvisioningTag(activationCode);
    }

    public static ProvisioningTag getProvisioningTag(ActivationCode activationCode) {
        var tag = new ProvisioningTag();
        tag.setTagCode(TAG_CODE);
        tag.setDescription(TAG_DESCRIPTION);
        tag.setCategory(ServiceCategory.ACCESS);
        tag.setActivity(ProvisioningTagActivity.INTERNET);
        tag.setAccessType(Network.DOCSIS);
        tag.setPersistent(false);
        tag.setNature(ProvisioningTagNature.O);

        var tagActivation = new TagActivation();
        tagActivation.setProvisioningTag(tag);
        tagActivation.setActivationCode(activationCode);

        tagActivation.setTagValue(null);

        final var tagActivations = new HashSet<>(List.of(tagActivation));
        tag.setTagActivations(tagActivations);
        activationCode.setTagActivations(tagActivations);

        return tag;
    }

    public static ActivationCode getActivationCode() {
        var activationCode = new ActivationCode();
        activationCode.setActivCode(ACTIV_CODE);
        activationCode.setDescription(ACTIV_CODE_DESC);
        activationCode.setNetworkComponent(NetworkComponent.SPG);
        activationCode.setNature(ActivationNature.OPTION);
        return activationCode;
    }

    public static ServiceTag getServiceTag(Service service, ProvisioningTag tag) {
        var serviceTag = new ServiceTag();
        serviceTag.setProvisioningTag(tag);
        serviceTag.setTagCode(tag.getTagCode());
        serviceTag.setService(service);

        return serviceTag;
    }

    public static ServiceActivation getServiceActivation(Service service, ActivationCode activationCode) {
        return getServiceActivation(service, activationCode, false);
    }

    public static ServiceActivation getServiceActivation(Service service, ActivationCode activationCode, boolean isMissing) {
        var serviceActivation = new ServiceActivation();
        serviceActivation.setActivCode(activationCode.getActivCode());
        serviceActivation.setService(service);
        if (!isMissing) {
            serviceActivation.setActivationCode(activationCode);
        }
        return serviceActivation;
    }
}
