package mc.monacotelecom.services.importer.data;

public interface ImporterInterfaces {

    interface IProvisioningTagCsvLines {
        String getTagCode();
        String getDescription();
        String getActivity();
        String getCategory();
        String getAccessType();
        String getNature();
        String getPersistent();
        String getComponentType();
    }

    interface IActivationCodeCsvLines {


        String getTagCode();
        String getActivCode();
        String getActivationDescription();
        String getActivationNature();
        String getNetworkComponent();
        String getActivationValue();
        void setTagCode(String v);
        void setActivCode(String v);
        void setActivationDescription(String v);
        void setActivationNature(String v);
        void setNetworkComponent(String v);
        void setActivationValue(String v);
    }

    interface IProvisioningActionCsvLines {

        String getTagCode();

        String getTagAction();

        String getServiceAction();
    }

    interface IProvisioningProductCsvLines {

        String getProductCode();
        String getProductAction();
        String getTagCode();
        String getTagAction();
        String getServiceAction();
    }

    interface IProvisioningParameterCsvLines {

        String getTagCode();
        String getTagAction();
        String getParameterCode();
        String getParameterType();
        String getParameterDescription();
        String getParameterValue();
    }
}
