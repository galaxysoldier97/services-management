package mc.monacotelecom.services.integration.importer;

import mc.monacotelecom.services.enums.ActivationNature;
import mc.monacotelecom.services.enums.NetworkComponent;
import mc.monacotelecom.services.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql({"/sql/clean.sql", "/sql/activation_code_data.sql"})
class ActivationCodeImportIntegrationTests extends BaseIntegrationTest {

    @Test
    void import_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "TRP-SRV-Import Activation Code.xlsx", "text/plain",
                new ClassPathResource("data/TRP-SRV-Import Activation Code.xlsx").getInputStream());

        mockMvc.perform(multipart("/private/auth/import/ActivationCode/start?sync=true")
                .file(file))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.importStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.fileName").value("TRP-SRV-Import Activation Code.xlsx"))
                .andExpect(jsonPath("$.start").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.end").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error").value("mc.monacotelecom.services.exceptions.SvcValidationException Provisioning tag with code 'IPFBASIC' was not found"))
                .andExpect(jsonPath("$.errors[0].line").value("3"))
                .andExpect(jsonPath("$.errors[0].group").value("FTTH"));

        // New activation code
        var activationCodeOpt = activationCodeRepository.findById("IOFS_MGT");
        assertTrue(activationCodeOpt.isPresent());
        var activationCode = activationCodeOpt.get();
        assertEquals("IOFS_MGT", activationCode.getActivCode());
        assertEquals(ActivationNature.OPTION, activationCode.getNature());
        assertEquals(NetworkComponent.OLT, activationCode.getNetworkComponent());
        assertEquals("Option de service : Management", activationCode.getDescription());
        assertEquals(1, activationCode.getTagActivations().size());
        assertEquals("IEFBOX", activationCode.getTagActivations().stream().findFirst().get().getProvisioningTag().getTagCode());

        // Updated activation code (different tag activation)
        var activationCodeOpt2 = activationCodeRepository.findById("GPRSLOCK");
        assertTrue(activationCodeOpt2.isPresent());
        var activationCode2 = activationCodeOpt2.get();
        assertEquals("GPRSLOCK", activationCode2.getActivCode());
        assertEquals(ActivationNature.OPTION, activationCode2.getNature());
        assertEquals(NetworkComponent.SDP, activationCode2.getNetworkComponent());
        assertEquals("Something", activationCode2.getDescription());
        assertEquals(2, activationCode2.getTagActivations().size(), "Activation code should now be associated to both old and new tags");
        final var existingTagActivation = activationCode2.getTagActivations().stream()
                .filter(x -> x.getProvisioningTag().getTagCode().equals("IEFBOX")).findFirst();
        assertTrue(existingTagActivation.isPresent());
        assertNull(existingTagActivation.get().getTagValue());
        final var newTagActivation = activationCode2.getTagActivations().stream()
                .filter(x -> x.getProvisioningTag().getTagCode().equals("IEFBOX2")).findFirst();
        assertTrue(newTagActivation.isPresent());
        assertEquals((Long) 123L, newTagActivation.get().getTagValue());

        // Updated activation code (same tag activation)
        var activationCodeOpt3 = activationCodeRepository.findById("ODBIC");
        assertTrue(activationCodeOpt3.isPresent());
        var activationCode3 = activationCodeOpt3.get();
        assertEquals("ODBIC", activationCode3.getActivCode());
        assertEquals(ActivationNature.BARRING, activationCode3.getNature());
        assertEquals(NetworkComponent.SPG, activationCode3.getNetworkComponent());
        assertEquals("Something else", activationCode3.getDescription());
        assertEquals(1, activationCode3.getTagActivations().size());
        assertEquals("IEFBOX2", activationCode3.getTagActivations().stream().findFirst().get().getProvisioningTag().getTagCode());
        assertEquals((Long) 456L, activationCode3.getTagActivations().stream().findFirst().get().getTagValue());

        // Erroneous
        assertFalse(activationCodeRepository.findById("IPFBASIC").isPresent());

        assertTrue(allImportsCompleted());
    }

    @Test
    void import_cancelledBeforeImport() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "TRP-SRV-Import Activation Code.xlsx", "text/plain",
                new ClassPathResource("data/TRP-SRV-Import Activation Code.xlsx").getInputStream());

        mockMvc.perform(multipart("/private/auth/import/ActivationCode/start?deleted=true&sync=true")
                .file(file))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.importStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.fileName").value("TRP-SRV-Import Activation Code.xlsx"))
                .andExpect(jsonPath("$.start").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.end").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error").value("mc.monacotelecom.services.exceptions.SvcValidationException Provisioning tag with code 'IPFBASIC' was not found"))
                .andExpect(jsonPath("$.errors[0].line").value("3"))
                .andExpect(jsonPath("$.errors[0].group").value("FTTH"));

        var oldAccessCode = activationCodeRepository.findById("HPX_126");
        var newAccessCode = activationCodeRepository.findById("IOFS_MGT");

        assertTrue(oldAccessCode.isEmpty());
        assertTrue(newAccessCode.isPresent());
    }

    @Test
    void import_failureWithoutFile() throws Exception {
        mockMvc.perform(multipart("/private/auth/import/ActivationCode/start?sync=true"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"File is mandatory for import 'ActivationCodeImporter'\"}"));

        assertTrue(allImportsCompleted());
    }
}
