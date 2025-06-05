package mc.monacotelecom.services.integration.importer;

import mc.monacotelecom.services.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;

import static mc.monacotelecom.services.enums.ProvisioningProductActionRequest.ACTIVATION;
import static mc.monacotelecom.services.enums.ProvisioningProductActionRequest.DEACTIVATION;
import static mc.monacotelecom.services.enums.ProvisioningTagAction.CANCEL;
import static mc.monacotelecom.services.enums.ProvisioningTagAction.PURCHASE;
import static mc.monacotelecom.services.enums.TechnicalParameterType.STATIC;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("/sql/clean.sql")
class ProvisioningImporterIntegrationTest extends BaseIntegrationTest {

    @Test
    void import_success() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "TRP-SRV-Import ProvisioningMulti.xlsx", "text/plain",
                new ClassPathResource("data/TRP-SRV-Import ProvisioningMulti.xlsx").getInputStream());

        mockMvc.perform(multipart("/private/auth/import/Provisioning/start?sync=true")
                        .file(file))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.importStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.errors", hasSize(0)));

        //verify existance of entities

        var provisioningTag = provisioningTagRepository.findById("IPFEVENT");
        var provisioningTag2 = provisioningTagRepository.findById("IPFEVENTLINK");
        var provisioningTag3 = provisioningTagRepository.findById("IPFEVENTPLUS");
        var provisioningTag4 = provisioningTagRepository.findById("IRFVOIPEVENT");
        var provisioningTag5 = provisioningTagRepository.findById("IRFVOIPSECEVENT");

        var activationCode = activationCodeRepository.findById("HOG_Z14");
        var activationCode2 = activationCodeRepository.findById("HOG_Z14");

        var provisioningAction = provisioningActionRepository.findByTagTagCodeAndTagAction("IPFEVENT", PURCHASE);
        var provisioningAction2 = provisioningActionRepository.findByTagTagCodeAndTagAction("IPFEVENT", CANCEL);
        var provisioningAction3 = provisioningActionRepository.findByTagTagCodeAndTagAction("IPFEVENTLINK", PURCHASE);

        var provisioningProduct = provisioningProductRepository
                .findByProductCodeAndActionRequestAndProvisioningActionTagTagCodeAndProvisioningActionTagAction("FIBER_FTTH_INTERNET_ACCESS_EVENT", ACTIVATION, "IPFEVENT", PURCHASE);
        var provisioningProduct2 = provisioningProductRepository
                .findByProductCodeAndActionRequestAndProvisioningActionTagTagCodeAndProvisioningActionTagAction("FIBER_FTTH_INTERNET_ACCESS_EVENT", DEACTIVATION, "IPFEVENT", CANCEL);
        var provisioningProduct3 = provisioningProductRepository
                .findByProductCodeAndActionRequestAndProvisioningActionTagTagCodeAndProvisioningActionTagAction("FIBER_FTTH_INTERNET_ACCESS_EVENTLINK", ACTIVATION, "IPFEVENTLINK", PURCHASE);

        var technicalParams = technicalParameterRepository.findByParameterCodeAndParameterType("slaProfile", STATIC);
        var technicalParams2 = technicalParameterRepository.findByParameterCodeAndParameterType("slaProfileRouter", STATIC);
        var technicalParams3 = technicalParameterRepository.findByParameterCodeAndParameterType("subscriberProfile", STATIC);

        var tagActivation = tagActivationRepository.findByTagCodeAndActvCode("IPFEVENT", "HOG_Z14");
        var tagActivation2 = tagActivationRepository.findByTagCodeAndActvCode("IPFEVENTLINK", "HOG_Z14");

        var provisioningActionParam = provisioningActionParameterRepository.findByProvisioningActionInternalIdAndTechnicalParameterInternalId(1L, technicalParams.get().getInternalId());

        assertAll(() -> assertTrue(provisioningTag.isPresent()),
                () -> assertTrue(provisioningTag2.isPresent()),
                () -> assertTrue(provisioningTag3.isPresent()),
                () -> assertTrue(provisioningTag4.isPresent()),
                () -> assertTrue(provisioningTag5.isPresent()),

                () -> assertTrue(activationCode.isPresent()),
                () -> assertTrue(activationCode2.isPresent()),

                () -> assertTrue(provisioningAction.isPresent()),
                () -> assertTrue(provisioningAction2.isPresent()),
                () -> assertTrue(provisioningAction3.isPresent()),

                () -> assertTrue(provisioningProduct.isPresent()),
                () -> assertTrue(provisioningProduct2.isPresent()),
                () -> assertTrue(provisioningProduct3.isPresent()),

                () -> assertTrue(technicalParams.isPresent()),
                () -> assertTrue(technicalParams2.isPresent()),
                () -> assertTrue(technicalParams3.isPresent()),

                () -> assertTrue(tagActivation.isPresent()),
                () -> assertTrue(tagActivation2.isPresent()),

                () -> assertTrue(provisioningActionParam.isPresent()),

                () -> assertTrue(provisioningTag.get().getPersistent()),
                () -> assertFalse(provisioningTag4.get().getPersistent()),
                () -> assertFalse(provisioningTag5.get().getPersistent())
        );
    }

    @Test
    void import_with_error() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "TRP-SRV-Import ProvisioningMulti-errors.xlsx", "text/plain",
                new ClassPathResource("data/TRP-SRV-Import ProvisioningMulti-errors.xlsx").getInputStream());

        mockMvc.perform(multipart("/private/auth/import/Provisioning/start?sync=true")
                        .file(file))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.importStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.errors", hasSize(4)))
                .andExpect(jsonPath("$.errors[0].group").value("ProvisioningTag"))
                .andExpect(jsonPath("$.errors[0].error").value("java.lang.IllegalArgumentException No enum constant mc.monacotelecom.services.enums.ProvisioningTagActivity.SIP"))
                .andExpect(jsonPath("$.errors[1].group").value("ProvisioningActivCode"))
                .andExpect(jsonPath("$.errors[1].error").value("mc.monacotelecom.services.exceptions.SvcValidationException Provisioning tag with code 'IRFVOIPSECEVENT' was not found"))
                .andExpect(jsonPath("$.errors[2].group").value("ProvisioningProduct"))
                .andExpect(jsonPath("$.errors[2].error").value("mc.monacotelecom.services.exceptions.SvcValidationException Provisioning tag with code 'IRFVOIPSECEVENT' was not found"))
                .andExpect(jsonPath("$.errors[3].group").value("ProvisioningParameter"))
                .andExpect(jsonPath("$.errors[3].error").value("mc.monacotelecom.services.exceptions.SvcValidationException Provisioning action with tag code 'IRFVOIPSECEVENT' and tag action 'PURCHASE' was not found"));

        var provisioningTag = provisioningTagRepository.findById("IPFEVENT");
        var provisioningTag2 = provisioningTagRepository.findById("IPFEVENTLINK");

        var activationCode = activationCodeRepository.findById("HOG_Z14");
        var activationCode2 = activationCodeRepository.findById("HOG_Z14");

        var provisioningAction = provisioningActionRepository.findByTagTagCodeAndTagAction("IPFEVENT", PURCHASE);
        var provisioningAction2 = provisioningActionRepository.findByTagTagCodeAndTagAction("IPFEVENT", CANCEL);

        var provisioningProduct = provisioningProductRepository
                .findByProductCodeAndActionRequestAndProvisioningActionTagTagCodeAndProvisioningActionTagAction("FIBER_FTTH_INTERNET_ACCESS_EVENT", ACTIVATION, "IPFEVENT", PURCHASE);
        var provisioningProduct2 = provisioningProductRepository
                .findByProductCodeAndActionRequestAndProvisioningActionTagTagCodeAndProvisioningActionTagAction("FIBER_FTTH_INTERNET_ACCESS_EVENT", DEACTIVATION, "IPFEVENT", CANCEL);

        var technicalParams = technicalParameterRepository.findByParameterCodeAndParameterType("subscriberProfile", STATIC);
        var technicalParams2 = technicalParameterRepository.findByParameterCodeAndParameterType("slaProfileRouter", STATIC);

        assertAll(() -> assertTrue(provisioningTag.isPresent()),
                () -> assertTrue(provisioningTag2.isPresent()),

                () -> assertTrue(activationCode.isPresent()),
                () -> assertTrue(activationCode2.isPresent()),

                () -> assertTrue(provisioningAction.isPresent()),
                () -> assertTrue(provisioningAction2.isPresent()),

                () -> assertTrue(provisioningProduct.isPresent()),
                () -> assertTrue(provisioningProduct2.isPresent()),

                () -> assertTrue(technicalParams.isPresent()),
                () -> assertTrue(technicalParams2.isPresent())
        );
    }
}

