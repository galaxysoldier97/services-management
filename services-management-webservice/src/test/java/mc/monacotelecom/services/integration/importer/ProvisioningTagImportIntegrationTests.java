package mc.monacotelecom.services.integration.importer;

import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ProvisioningTagActivity;
import mc.monacotelecom.services.enums.ProvisioningTagNature;
import mc.monacotelecom.services.enums.ServiceCategory;
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

@Sql({"/sql/clean.sql", "/sql/provisioning_tag_data.sql"})
class ProvisioningTagImportIntegrationTests extends BaseIntegrationTest {

    @Test
    void import_success() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "TRP-SRV-Import Provisioning Tag.xlsx", "text/plain",
                new ClassPathResource("data/TRP-SRV-Import Provisioning Tag.xlsx").getInputStream());

        mockMvc.perform(multipart("/private/auth/import/ProvisioningTag/start?sync=true")
                        .file(file))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.importStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.fileName").value("TRP-SRV-Import Provisioning Tag.xlsx"))
                .andExpect(jsonPath("$.start").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.end").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.errors", hasSize(3)))
                .andExpect(jsonPath("$.errors[0].error").value("mc.monacotelecom.services.exceptions.SvcValidationException Provisioning Tag access type must not be null for ACCESS category"))
                .andExpect(jsonPath("$.errors[0].line").value("4"))
                .andExpect(jsonPath("$.errors[0].group").value("FTTH"))
                .andExpect(jsonPath("$.errors[1].group").value("FTTH"))
                .andExpect(jsonPath("$.errors[1].error").value("mc.monacotelecom.services.exceptions.SvcValidationException activity must not be null"))
                .andExpect(jsonPath("$.errors[1].line").value("7"))
                .andExpect(jsonPath("$.errors[1].group").value("FTTH"))
                .andExpect(jsonPath("$.errors[2].error").value("mc.monacotelecom.services.exceptions.SvcValidationException nature must not be null"))
                .andExpect(jsonPath("$.errors[2].line").value("8"))
                .andExpect(jsonPath("$.errors[2].group").value("FTTH"));

        // Creation
        var provTagOpt = provisioningTagRepository.findById("IAFACC");
        assertTrue(provTagOpt.isPresent());
        var provTag = provTagOpt.get();
        assertAll(
                () -> assertEquals("IAFACC", provTag.getTagCode()),
                () -> assertEquals("Accès FTTH", provTag.getDescription()),
                () -> assertEquals(ProvisioningTagActivity.INTERNET, provTag.getActivity()),
                () -> assertEquals(ServiceCategory.ACCESS, provTag.getCategory()),
                () -> assertEquals(Network.FTTH, provTag.getAccessType()),
                () -> assertEquals(ProvisioningTagNature.A, provTag.getNature()),
                () -> assertEquals(Boolean.TRUE, provTag.getPersistent()),
                () -> assertNull(provTag.getComponentType())
        );

        // Update
        var provTagOpt2 = provisioningTagRepository.findById("HPXBRDBAND1");
        assertTrue(provTagOpt2.isPresent());
        var provTag2 = provTagOpt2.get();
        assertAll(
                () -> assertEquals("HPXBRDBAND1", provTag2.getTagCode()),
                () -> assertEquals("Another description", provTag2.getDescription()),
                () -> assertEquals(ProvisioningTagActivity.MOBILE, provTag2.getActivity()),
                () -> assertEquals(ServiceCategory.ACCESS, provTag2.getCategory()),
                () -> assertEquals(Network.BBHB, provTag2.getAccessType()),
                () -> assertEquals(ProvisioningTagNature.P, provTag2.getNature()),
                () -> assertEquals(Boolean.TRUE, provTag2.getPersistent()),
                () -> assertNull(provTag2.getComponentType())
        );

        // New entry and test if no component type is mapped in excel file
        var provTagOpt3 = provisioningTagRepository.findById("HPXBRDBAND2");
        assertTrue(provTagOpt3.isPresent());
        var provTag3 = provTagOpt3.get();
        assertAll(
                () -> assertEquals("HPXBRDBAND2", provTag3.getTagCode()),
                () -> assertEquals("Yet another description", provTag3.getDescription()),
                () -> assertEquals(ProvisioningTagActivity.MOBILE, provTag3.getActivity()),
                () -> assertEquals(ServiceCategory.COMPONENT, provTag3.getCategory()),
                () -> assertNull(provTag3.getAccessType()),
                () -> assertEquals(ProvisioningTagNature.P, provTag3.getNature()),
                () -> assertEquals(Boolean.TRUE, provTag3.getPersistent()),
                () -> assertEquals("TEAMS", provTag3.getComponentType())
        );

        // Erroneous because no access type for ACCESS Tag
        assertFalse(provisioningTagRepository.findById("NOACCTYPE").isPresent(), "Tag without access type should not exist");
        assertFalse(provisioningTagRepository.findById("NOACTIVITY").isPresent(), "Tag without activity should not exist");
        assertFalse(provisioningTagRepository.findById("NONATURE").isPresent(), "Tag without nature should not exist");

        assertTrue(allImportsCompleted());
    }

    @Test
    void import_deleteBeforeImport() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "TRP-SRV-Import Provisioning Tag.xlsx", "text/plain",
                new ClassPathResource("data/TRP-SRV-Import Provisioning Tag.xlsx").getInputStream());

        mockMvc.perform(multipart("/private/auth/import/ProvisioningTag/start?deleted=true&sync=true")
                        .file(file))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.importStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.fileName").value("TRP-SRV-Import Provisioning Tag.xlsx"))
                .andExpect(jsonPath("$.start").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.end").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.errors", hasSize(3)))
                .andExpect(jsonPath("$.errors[0].error").value("mc.monacotelecom.services.exceptions.SvcValidationException Provisioning Tag access type must not be null for ACCESS category"))
                .andExpect(jsonPath("$.errors[0].line").value("4"))
                .andExpect(jsonPath("$.errors[0].group").value("FTTH"))
                .andExpect(jsonPath("$.errors[1].group").value("FTTH"))
                .andExpect(jsonPath("$.errors[1].error").value("mc.monacotelecom.services.exceptions.SvcValidationException activity must not be null"))
                .andExpect(jsonPath("$.errors[1].line").value("7"))
                .andExpect(jsonPath("$.errors[1].group").value("FTTH"))
                .andExpect(jsonPath("$.errors[2].error").value("mc.monacotelecom.services.exceptions.SvcValidationException nature must not be null"))
                .andExpect(jsonPath("$.errors[2].line").value("8"))
                .andExpect(jsonPath("$.errors[2].group").value("FTTH"));

        var oldProvisioningTag = provisioningTagRepository.findById("HAXACC");
        var newProvisioningTag = provisioningTagRepository.findById("IAFACC");

        assertTrue(oldProvisioningTag.isEmpty());
        assertTrue(newProvisioningTag.isPresent());
    }

    @Test
    void import_failureWithoutFile() throws Exception {
        mockMvc.perform(multipart("/private/auth/import/ProvisioningTag/start?sync=true"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"File is mandatory for import 'ProvisioningTagImporter'\"}"));

        assertTrue(allImportsCompleted());
    }
}
