package mc.monacotelecom.services.integration.importer;

import mc.monacotelecom.services.enums.ProvisioningTagAction;
import mc.monacotelecom.services.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;

import static mc.monacotelecom.services.enums.ProvisioningProductActionRequest.ACTIVATION;
import static mc.monacotelecom.services.enums.ProvisioningProductActionRequest.DEACTIVATION;
import static mc.monacotelecom.services.enums.ProvisioningTagAction.CANCEL;
import static mc.monacotelecom.services.enums.ProvisioningTagAction.PURCHASE;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql({"/sql/clean.sql", "/sql/provisioning_product_data.sql"})
class ProvisioningProductImportIntegrationTests extends BaseIntegrationTest {

    @Test
    void import_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "TRP-SRV-Import Provisioning Product.xlsx", "text/plain",
                new ClassPathResource("data/TRP-SRV-Import Provisioning Product.xlsx").getInputStream());

        mockMvc.perform(multipart("/private/auth/import/ProvisioningProduct/start?sync=true")
                        .file(file))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.importStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.fileName").value("TRP-SRV-Import Provisioning Product.xlsx"))
                .andExpect(jsonPath("$.start").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.end").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error").value("mc.monacotelecom.services.exceptions.SvcValidationException Provisioning action with tag code 'IEFBOXONT' and tag action 'PURCHASE' was not found"))
                .andExpect(jsonPath("$.errors[0].line").value("4"))
                .andExpect(jsonPath("$.errors[0].group").value("ProductProvisioning"));

        // Creation (activation)
        var provProductOpt = provisioningProductRepository
                .findByProductCodeAndActionRequestAndProvisioningActionTagTagCodeAndProvisioningActionTagAction("FIBER_FTTH_ACCESS", ACTIVATION, "HAXACC", PURCHASE);
        assertTrue(provProductOpt.isPresent());
        var provProduct = provProductOpt.get();
        assertEquals("FIBER_FTTH_ACCESS", provProduct.getProductCode());
        assertEquals(ACTIVATION, provProduct.getActionRequest());
        assertEquals("HAXACC", provProduct.getProvisioningAction().getTag().getTagCode());
        assertEquals(PURCHASE, provProduct.getProvisioningAction().getTagAction());

        // Creation (deactivation)
        var provProductOpt2 = provisioningProductRepository
                .findByProductCodeAndActionRequestAndProvisioningActionTagTagCodeAndProvisioningActionTagAction("FIBER_FTTH_ACCESS", DEACTIVATION, "HAXACC", CANCEL);
        assertTrue(provProductOpt2.isPresent());
        var provProduct2 = provProductOpt2.get();
        assertEquals("FIBER_FTTH_ACCESS", provProduct2.getProductCode());
        assertEquals(DEACTIVATION, provProduct2.getActionRequest());
        assertEquals("HAXACC", provProduct2.getProvisioningAction().getTag().getTagCode());
        assertEquals(ProvisioningTagAction.CANCEL, provProduct2.getProvisioningAction().getTagAction());

        // Erroneous: missing provisioning action
        assertFalse(provisioningProductRepository
                .findByProductCodeAndActionRequestAndProvisioningActionTagTagCodeAndProvisioningActionTagAction("FIBER_FTTH_BOX_ONT_INCLUDED", ACTIVATION, "IEFBOXONT", PURCHASE).isPresent());

        // Creation with existing combination product/action but different tag
        var provProductOpt3 = provisioningProductRepository
                .findByProductCodeAndActionRequestAndProvisioningActionTagTagCodeAndProvisioningActionTagAction("PAY_AS_YOU_GO_1", ACTIVATION, "HECARD", CANCEL);
        assertTrue(provProductOpt3.isPresent());
        var provProduct3 = provProductOpt3.get();
        assertEquals("PAY_AS_YOU_GO_1", provProduct3.getProductCode());
        assertEquals(ACTIVATION, provProduct3.getActionRequest());
        assertEquals("HECARD", provProduct3.getProvisioningAction().getTag().getTagCode());
        assertEquals(ProvisioningTagAction.CANCEL, provProduct3.getProvisioningAction().getTagAction());

        // Update: set parent
        var provProductOpt4 = provisioningProductRepository
                .findByProductCodeAndActionRequestAndProvisioningActionTagTagCodeAndProvisioningActionTagAction("PAY_AS_YOU_GO_1", DEACTIVATION, "HAXACC", CANCEL);
        assertTrue(provProductOpt4.isPresent());
        var provProduct4 = provProductOpt4.get();
        assertEquals("PAY_AS_YOU_GO_1", provProduct4.getProductCode());
        assertEquals(DEACTIVATION, provProduct4.getActionRequest());
        assertEquals("HAXACC", provProduct4.getProvisioningAction().getTag().getTagCode());
        assertEquals(ProvisioningTagAction.CANCEL, provProduct4.getProvisioningAction().getTagAction());

        assertTrue(allImportsCompleted());
    }

    @Test
    void import_cancelledBeforeImport() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "TRP-SRV-Import Provisioning Product.xlsx", "text/plain",
                new ClassPathResource("data/TRP-SRV-Import Provisioning Product.xlsx").getInputStream());

        mockMvc.perform(multipart("/private/auth/import/ProvisioningProduct/start?deleted=true&sync=true")
                        .file(file))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.importStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.fileName").value("TRP-SRV-Import Provisioning Product.xlsx"))
                .andExpect(jsonPath("$.start").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.end").value("2020-05-31T20:35:24"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error").value("mc.monacotelecom.services.exceptions.SvcValidationException Provisioning action with tag code 'IEFBOXONT' and tag action 'PURCHASE' was not found"))
                .andExpect(jsonPath("$.errors[0].line").value("4"))
                .andExpect(jsonPath("$.errors[0].group").value("ProductProvisioning"));

        var oldProvisioningProduct = provisioningProductRepository
                .findByProductCodeAndActionRequestAndProvisioningActionTagTagCodeAndProvisioningActionTagAction("PAY_AS_YOU_GO_2", DEACTIVATION, "HAXACC", CANCEL);
        var newProvisioningProduct = provisioningProductRepository
                .findByProductCodeAndActionRequestAndProvisioningActionTagTagCodeAndProvisioningActionTagAction("PAY_AS_YOU_GO_1", DEACTIVATION, "HAXACC", CANCEL);

        assertTrue(oldProvisioningProduct.isEmpty());
        assertTrue(newProvisioningProduct.isPresent());
    }

    @Test
    void import_failureWithoutFile() throws Exception {
        mockMvc.perform(multipart("/private/auth/import/ProvisioningProduct/start?sync=true"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"File is mandatory for import 'ProvisioningProductImporter'\"}"));

        assertTrue(allImportsCompleted());
    }
}
