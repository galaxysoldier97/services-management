package mc.monacotelecom.services.integration.importer;

import mc.monacotelecom.services.entity.ProvisioningActionParameter;
import mc.monacotelecom.services.enums.TechnicalParameterType;
import mc.monacotelecom.services.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static mc.monacotelecom.services.enums.TechnicalParameterType.STATIC;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql({"/sql/clean.sql", "/sql/technical_parameter_data.sql"})
class TechnicalParameterImportIntegrationTests extends BaseIntegrationTest {

    @Test
    void import_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "TRP-SRV-Import Technical Parameter.xlsx", "text/plain",
                new ClassPathResource("data/TRP-SRV-Import Technical Parameter.xlsx").getInputStream());

        mockMvc.perform(multipart("/private/auth/import/TechnicalParameter/start?sync=true")
                        .file(file))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.importStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.fileName").value("TRP-SRV-Import Technical Parameter.xlsx"))
                .andExpect(jsonPath("$.start").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.end").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors[0].error").value("mc.monacotelecom.services.exceptions.SvcValidationException Provisioning action with tag code 'IRFVOIP' and tag action 'MIGRATION' was not found"))
                .andExpect(jsonPath("$.errors[0].line").value("3"))
                .andExpect(jsonPath("$.errors[0].group").value("ActionParameter"))
                .andExpect(jsonPath("$.errors[1].error").value("mc.monacotelecom.services.exceptions.SvcValidationException Technical parameter type cannot be empty"))
                .andExpect(jsonPath("$.errors[1].line").value("6"))
                .andExpect(jsonPath("$.errors[1].group").value("ActionParameter"));

        // Creation
        var technicalParameterOpt = technicalParameterRepository.findByParameterCodeAndParameterType("accessPointId", STATIC);
        assertTrue(technicalParameterOpt.isPresent());
        var technicalParameter = technicalParameterOpt.get();
        assertTrue(provisioningActionParameterRepository.findByProvisioningActionInternalIdAndTechnicalParameterInternalId(1L, technicalParameter.getInternalId()).isPresent());
        assertEquals("accessPointId", technicalParameter.getParameterCode());
        assertEquals(STATIC, technicalParameter.getParameterType());
        assertEquals("something", technicalParameter.getProvisioningActionParameters().get(0).getParameterValue());

        // Update existing parameter to add on another provisioning action (old one still referenced)
        var technicalParameterOptSecond = technicalParameterRepository.findByParameterCodeAndParameterType("PARAM1", TechnicalParameterType.CONTEXT);
        assertTrue(technicalParameterOptSecond.isPresent());
        var technicalParameterSecond = technicalParameterOptSecond.get();
        assertTrue(provisioningActionParameterRepository.findByProvisioningActionInternalIdAndTechnicalParameterInternalId(2L, technicalParameterSecond.getInternalId()).isPresent());
        assertEquals("PARAM1", technicalParameterSecond.getParameterCode());
        assertEquals(TechnicalParameterType.CONTEXT, technicalParameterSecond.getParameterType());
        assertEquals("kangaroo", technicalParameterSecond.getProvisioningActionParameters().get(0).getParameterValue());

        // Parameter left untouched completely (because of an error)
        var technicalParameterOpt2 = technicalParameterRepository.findByParameterCodeAndParameterType("PARAM2", TechnicalParameterType.CONTEXT);
        assertTrue(technicalParameterOpt2.isPresent());
        var technicalParameter2 = technicalParameterOpt2.get();
        Optional<ProvisioningActionParameter> provParamUntouchedOpt1 = provisioningActionParameterRepository.findByProvisioningActionInternalIdAndTechnicalParameterInternalId(1L, technicalParameter2.getInternalId());
        assertTrue(provParamUntouchedOpt1.isPresent(), "PARAM2/HAXACC should have been left untouched");
        var provParamUntouched1 = provParamUntouchedOpt1.get();
        assertNull(provParamUntouched1.getParameterValue());
        assertEquals("PARAM2", technicalParameter2.getParameterCode());
        assertEquals(TechnicalParameterType.CONTEXT, technicalParameter2.getParameterType());
        Optional<ProvisioningActionParameter> provParamUntouchedOpt2 = provisioningActionParameterRepository.findByProvisioningActionInternalIdAndTechnicalParameterInternalId(2L, technicalParameter2.getInternalId());
        assertTrue(provParamUntouchedOpt2.isPresent(), "PARAM2/HAXACC2 should have been left untouched");
        var provParamUntouched2 = provParamUntouchedOpt2.get();
        assertNull(provParamUntouched2.getParameterValue());

        // Erroneous because missing provisioning action
        assertFalse(technicalParameterRepository.findByParameterCode("IRFVOIP").isPresent());

        assertTrue(allImportsCompleted());
    }

    @Test
    void import_deleteBeforeImport() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "TRP-SRV-Import Technical Parameter.xlsx", "text/plain",
                new ClassPathResource("data/TRP-SRV-Import Technical Parameter.xlsx").getInputStream());

        mockMvc.perform(multipart("/private/auth/import/TechnicalParameter/start?deleted=true&sync=true")
                        .file(file))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.importStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.fileName").value("TRP-SRV-Import Technical Parameter.xlsx"))
                .andExpect(jsonPath("$.start").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.end").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors[0].error").value("mc.monacotelecom.services.exceptions.SvcValidationException Provisioning action with tag code 'IRFVOIP' and tag action 'MIGRATION' was not found"))
                .andExpect(jsonPath("$.errors[0].line").value("3"))
                .andExpect(jsonPath("$.errors[0].group").value("ActionParameter"))
                .andExpect(jsonPath("$.errors[1].error").value("mc.monacotelecom.services.exceptions.SvcValidationException Technical parameter type cannot be empty"))
                .andExpect(jsonPath("$.errors[1].line").value("6"))
                .andExpect(jsonPath("$.errors[1].group").value("ActionParameter"));

        var oldTechnicalParameter = technicalParameterRepository.findByParameterCodeAndParameterType("PARAM4", STATIC);
        var newTechnicalParameter = technicalParameterRepository.findByParameterCodeAndParameterType("PARAM1", TechnicalParameterType.CONTEXT);


        assertTrue(oldTechnicalParameter.isEmpty());
        assertTrue(newTechnicalParameter.isPresent());
    }

    @Test
    void import_failureWithoutFile() throws Exception {
        mockMvc.perform(multipart("/private/auth/import/TechnicalParameter/start?sync=true"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"File is mandatory for import 'TechnicalParameterImporter'\"}"));

        assertTrue(allImportsCompleted());
    }
}
