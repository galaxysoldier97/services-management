package mc.monacotelecom.services.integration.importer;

import mc.monacotelecom.services.enums.ServiceAction;
import mc.monacotelecom.services.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;

import static mc.monacotelecom.services.enums.ProvisioningTagAction.CANCEL;
import static mc.monacotelecom.services.enums.ProvisioningTagAction.PURCHASE;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql({"/sql/clean.sql", "/sql/provisioning_action_data.sql"})
class ProvisioningActionImportIntegrationTests extends BaseIntegrationTest {

    @Test
    void import_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "TRP-SRV-Import Provisioning Action.xlsx", "text/plain",
                new ClassPathResource("data/TRP-SRV-Import Provisioning Action.xlsx").getInputStream());

        mockMvc.perform(multipart("/private/auth/import/ProvisioningAction/start?sync=true")
                .file(file))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.importStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.fileName").value("TRP-SRV-Import Provisioning Action.xlsx"))
                .andExpect(jsonPath("$.start").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.end").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error").value("mc.monacotelecom.services.exceptions.SvcValidationException Provisioning tag with code 'IPFLINK' was not found"))
                .andExpect(jsonPath("$.errors[0].line").value("4"))
                .andExpect(jsonPath("$.errors[0].group").value("ProvisioningAction"));

        // Creation with service action
        var provActionOpt = provisioningActionRepository.findByTagTagCodeAndTagAction("HODATA1", PURCHASE);
        assertTrue(provActionOpt.isPresent());
        var provAction = provActionOpt.get();
        assertEquals("HODATA1", provAction.getTag().getTagCode());
        assertEquals(PURCHASE, provAction.getTagAction());
        assertEquals(ServiceAction.ACTIVATION, provAction.getServiceAction());

        // Creation without service action
        var provActionOpt2 = provisioningActionRepository.findByTagTagCodeAndTagAction("HODATA1", CANCEL);
        assertTrue(provActionOpt2.isPresent());
        var provAction2 = provActionOpt2.get();
        assertEquals("HODATA1", provAction2.getTag().getTagCode());
        assertEquals(CANCEL, provAction2.getTagAction());
        assertNull(provAction2.getServiceAction());

        // Erroneous
        assertFalse(provisioningActionRepository.findByTagTagCodeAndTagAction("IPFLINK", PURCHASE).isPresent());

        // Update without service action
        var provActionOpt3 = provisioningActionRepository.findByTagTagCodeAndTagAction("HAXACC", PURCHASE);
        assertTrue(provActionOpt3.isPresent());
        var provAction3 = provActionOpt3.get();
        assertEquals("HAXACC", provAction3.getTag().getTagCode());
        assertEquals(PURCHASE, provAction3.getTagAction());
        assertNull(provAction3.getServiceAction());

        // Update with service action added
        var provActionOpt4 = provisioningActionRepository.findByTagTagCodeAndTagAction("HAXACC", CANCEL);
        assertTrue(provActionOpt4.isPresent());
        var provAction4 = provActionOpt4.get();
        assertEquals("HAXACC", provAction4.getTag().getTagCode());
        assertEquals(CANCEL, provAction4.getTagAction());
        assertEquals(ServiceAction.DEACTIVATION, provAction4.getServiceAction());

        // Update with service action removed
        var provActionOpt5 = provisioningActionRepository.findByTagTagCodeAndTagAction("HECARD", PURCHASE);
        assertTrue(provActionOpt5.isPresent());
        var provAction5 = provActionOpt5.get();
        assertEquals("HECARD", provAction5.getTag().getTagCode());
        assertEquals(PURCHASE, provAction5.getTagAction());
        assertNull(provAction5.getServiceAction());

        assertTrue(allImportsCompleted());
    }

    @Test
    void import_cancelledBeforeImport() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "TRP-SRV-Import Provisioning Action.xlsx", "text/plain",
                new ClassPathResource("data/TRP-SRV-Import Provisioning Action.xlsx").getInputStream());

        mockMvc.perform(multipart("/private/auth/import/ProvisioningAction/start?deleted=true&sync=true")
                .file(file))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.importStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.fileName").value("TRP-SRV-Import Provisioning Action.xlsx"))
                .andExpect(jsonPath("$.start").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.end").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error").value("mc.monacotelecom.services.exceptions.SvcValidationException Provisioning tag with code 'IPFLINK' was not found"))
                .andExpect(jsonPath("$.errors[0].line").value("4"))
                .andExpect(jsonPath("$.errors[0].group").value("ProvisioningAction"));

        var oldProvisioning = provisioningActionRepository.findByTagTagCodeAndTagAction("HPXBRDBAND1", PURCHASE);
        var newProvisioning = provisioningActionRepository.findByTagTagCodeAndTagAction("HAXACC", PURCHASE);

        assertTrue(oldProvisioning.isEmpty());
        assertTrue(newProvisioning.isPresent());
    }

    @Test
    void import_failureWithoutFile() throws Exception {
        mockMvc.perform(multipart("/private/auth/import/ProvisioningAction/start?sync=true"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"File is mandatory for import 'ProvisioningActionImporter'\"}"));

        assertTrue(allImportsCompleted());
    }
}
