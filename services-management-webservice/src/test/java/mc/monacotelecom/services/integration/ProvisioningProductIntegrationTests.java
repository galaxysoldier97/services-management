package mc.monacotelecom.services.integration;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Sql({"/sql/clean.sql", "/sql/provisioning_action_data.sql"})
class ProvisioningProductIntegrationTests extends BaseIntegrationTest {

    private static final String ROUTE_PROVISIONING_ACTIONS = "/private/auth/provisioningactions";

    @Test
    void getProvisioningProductById_success() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTIONS + "/1/provisioningsproduct/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provisioningProductId").value(1))
                .andExpect(jsonPath("$.productCode").value("PAY_AS_YOU_GO_1"))
                .andExpect(jsonPath("$.productAction").value("ACTIVATION"))
                .andExpect(jsonPath("$.provAction.tagActionId").value(1))
                .andExpect(jsonPath("$.provAction.provisioningTag.tagId").value(1))
                .andExpect(jsonPath("$.provAction.provisioningTag.tagCode").value("HAXACC"))
                .andExpect(jsonPath("$.provAction.provisioningTag.description").value("Mobile Access "))
                .andExpect(jsonPath("$.provAction.provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$.provAction.provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$.provAction.provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$.provAction.provisioningTag.nature").value("A"))
                .andExpect(jsonPath("$.provAction.provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$.provAction.provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.provAction.tagAction").value("PURCHASE"))
                .andExpect(jsonPath("$.provAction.provActionParameters", hasSize(1)))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$.provAction.serviceAction").value(IsNull.nullValue()));
    }

    @Test
    void getProvisioningProductById_missingProvisioningAction() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTIONS + "/99/provisioningsproduct/1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":400,\"error\":\"Provisioning product with ID '1' and provisioning action '99' was not found\"}"));
    }

    @Test
    void getProvisioningProductById_missingProvisioningProduct() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTIONS + "/1/provisioningsproduct/99"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":400,\"error\":\"Provisioning product with ID '99' and provisioning action '1' was not found\"}"));
    }

    @Test
    void getAllProvisioningProducts_success() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTIONS + "/provisioningsproduct?size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].provisioningProductId").value(1))
                .andExpect(jsonPath("$.content[0].productCode").value("PAY_AS_YOU_GO_1"))
                .andExpect(jsonPath("$.content[0].productAction").value("ACTIVATION"))
                .andExpect(jsonPath("$.content[0].provAction.tagActionId").value(1))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.tagId").value(1))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.tagCode").value("HAXACC"))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.description").value("Mobile Access "))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.nature").value("A"))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.links", hasSize(0)))
                .andExpect(jsonPath("$.content[0].provAction.tagAction").value("PURCHASE"))
                .andExpect(jsonPath("$.content[0].provAction.provActionParameters", hasSize(1)))
                .andExpect(jsonPath("$.content[0].provAction.provActionParameters[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$.content[0].provAction.provActionParameters[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$.content[0].provAction.provActionParameters[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$.content[0].provAction.provActionParameters[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$.content[0].provAction.provActionParameters[0].technicalParameter.links", hasSize(0)))
                .andExpect(jsonPath("$.content[0].provAction.provActionParameters[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$.content[0].provAction.provActionParameters[0].links", hasSize(0)))
                .andExpect(jsonPath("$.content[0].provAction.serviceAction").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.content[0].provAction.links", hasSize(0)))
                .andExpect(jsonPath("$.content[0].links", hasSize(0)));
    }

    @Test
    void getAllProvisioningProductsForProvisioningAction_success() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTIONS + "/1/provisioningsproduct?size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].provisioningProductId").value(1))
                .andExpect(jsonPath("$.content[0].productCode").value("PAY_AS_YOU_GO_1"))
                .andExpect(jsonPath("$.content[0].productAction").value("ACTIVATION"))
                .andExpect(jsonPath("$.content[0].provAction.tagActionId").value(1))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.tagId").value(1))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.tagCode").value("HAXACC"))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.description").value("Mobile Access "))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.nature").value("A"))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.content[0].provAction.provisioningTag.links", hasSize(0)))
                .andExpect(jsonPath("$.content[0].provAction.tagAction").value("PURCHASE"))
                .andExpect(jsonPath("$.content[0].provAction.provActionParameters", hasSize(1)))
                .andExpect(jsonPath("$.content[0].provAction.provActionParameters[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$.content[0].provAction.provActionParameters[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$.content[0].provAction.provActionParameters[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$.content[0].provAction.provActionParameters[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$.content[0].provAction.provActionParameters[0].technicalParameter.links", hasSize(0)))
                .andExpect(jsonPath("$.content[0].provAction.provActionParameters[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$.content[0].provAction.provActionParameters[0].links", hasSize(0)))
                .andExpect(jsonPath("$.content[0].provAction.serviceAction").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.content[0].provAction.links", hasSize(0)))
                .andExpect(jsonPath("$.content[0].links", hasSize(0)));
    }

    @Test
    void getAllProvisioningProductsForProvisioningAction_failureWhenNone() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTIONS + "/2/provisioningsproduct?size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\":[],\"pageable\":{\"sort\":{\"sorted\":false,\"unsorted\":true,\"empty\":true},\"pageNumber\":0,\"pageSize\":1,\"offset\":0,\"paged\":true,\"unpaged\":false},\"last\":true,\"totalPages\":0,\"totalElements\":0,\"numberOfElements\":0,\"first\":true,\"number\":0,\"size\":1,\"sort\":{\"sorted\":false,\"unsorted\":true,\"empty\":true},\"empty\":true}"));
    }

    @Test
    void addProvisioningProduct_success() throws Exception {
        mockMvc.perform(post(ROUTE_PROVISIONING_ACTIONS + "/1/provisioningsproduct")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"productCode\": \"PAY_AS_YOU_GO_1\", \"productAction\": \"TRANSITION\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provisioningProductId").value(2))
                .andExpect(jsonPath("$.productCode").value("PAY_AS_YOU_GO_1"))
                .andExpect(jsonPath("$.productAction").value("TRANSITION"))
                .andExpect(jsonPath("$.provAction.tagActionId").value(1))
                .andExpect(jsonPath("$.provAction.provisioningTag.tagId").value(1))
                .andExpect(jsonPath("$.provAction.provisioningTag.tagCode").value("HAXACC"))
                .andExpect(jsonPath("$.provAction.provisioningTag.description").value("Mobile Access "))
                .andExpect(jsonPath("$.provAction.provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$.provAction.provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$.provAction.provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$.provAction.provisioningTag.nature").value("A"))
                .andExpect(jsonPath("$.provAction.provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$.provAction.provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.provAction.tagAction").value("PURCHASE"))
                .andExpect(jsonPath("$.provAction.provActionParameters", hasSize(1)))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$.provAction.serviceAction").value(IsNull.nullValue()));
    }

    @Test
    void addProvisioningProduct_missingProvisioningAction() throws Exception {
        mockMvc.perform(post(ROUTE_PROVISIONING_ACTIONS + "/99/provisioningsproduct")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"productCode\": \"PAY_AS_YOU_GO_1\", \"productAction\": \"TRANSITION\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":400,\"error\":\"Provisioning action with ID '99' was not found\"}"));
    }

    @Test
    void addProvisioningProduct_failureForBadAction() throws Exception {
        mockMvc.perform(post(ROUTE_PROVISIONING_ACTIONS + "/1/provisioningsproduct")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"productCode\": \"PAY_AS_YOU_GO_1\", \"productAction\": \"UNKNOWN\"}"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void addProvisioningProduct_successIfAlreadyExisting() throws Exception {
        mockMvc.perform(post(ROUTE_PROVISIONING_ACTIONS + "/1/provisioningsproduct")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"productCode\": \"PAY_AS_YOU_GO_1\", \"productAction\": \"ACTIVATION\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provisioningProductId").value(2))
                .andExpect(jsonPath("$.productCode").value("PAY_AS_YOU_GO_1"))
                .andExpect(jsonPath("$.productAction").value("ACTIVATION"))
                .andExpect(jsonPath("$.provAction.tagActionId").value(1))
                .andExpect(jsonPath("$.provAction.provisioningTag.tagId").value(1))
                .andExpect(jsonPath("$.provAction.provisioningTag.tagCode").value("HAXACC"))
                .andExpect(jsonPath("$.provAction.provisioningTag.description").value("Mobile Access "))
                .andExpect(jsonPath("$.provAction.provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$.provAction.provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$.provAction.provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$.provAction.provisioningTag.nature").value("A"))
                .andExpect(jsonPath("$.provAction.provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$.provAction.provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.provAction.tagAction").value("PURCHASE"))
                .andExpect(jsonPath("$.provAction.provActionParameters", hasSize(1)))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$.provAction.serviceAction").value(IsNull.nullValue()));
    }

    @Test
    void updateProvisioningProduct_success() throws Exception {
        mockMvc.perform(put(ROUTE_PROVISIONING_ACTIONS + "/1/provisioningsproduct/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"parentTagCode\": \"HECARD\", \"parentTagAction\": \"PURCHASE\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provisioningProductId").value(1))
                .andExpect(jsonPath("$.productCode").value("PAY_AS_YOU_GO_1"))
                .andExpect(jsonPath("$.productAction").value("ACTIVATION"))
                .andExpect(jsonPath("$.provAction.tagActionId").value(1))
                .andExpect(jsonPath("$.provAction.provisioningTag.tagId").value(1))
                .andExpect(jsonPath("$.provAction.provisioningTag.tagCode").value("HAXACC"))
                .andExpect(jsonPath("$.provAction.provisioningTag.description").value("Mobile Access "))
                .andExpect(jsonPath("$.provAction.provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$.provAction.provisioningTag.componentType").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.provAction.provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$.provAction.provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$.provAction.provisioningTag.nature").value("A"))
                .andExpect(jsonPath("$.provAction.provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$.provAction.provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.provAction.tagAction").value("PURCHASE"))
                .andExpect(jsonPath("$.provAction.provActionParameters", hasSize(1)))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$.provAction.provActionParameters[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$.provAction.serviceAction").value(IsNull.nullValue()));
    }

    @Test
    void updateProvisioningProduct_missingProvisioningAction() throws Exception {
        mockMvc.perform(put(ROUTE_PROVISIONING_ACTIONS + "/99/provisioningsproduct/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"parentTagCode\": \"HECARD\", \"parentTagAction\": \"PURCHASE\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":400,\"error\":\"Provisioning action with ID '99' was not found\"}"));
    }

    @Test
    void updateProvisioningProduct_missingProvisioningProduct() throws Exception {
        mockMvc.perform(put(ROUTE_PROVISIONING_ACTIONS + "/1/provisioningsproduct/99")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"parentTagCode\": \"HECARD\", \"parentTagAction\": \"PURCHASE\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"Provisioning product with ID '99' was not found\"}"));
    }

    @Test
    void updateProvisioningProduct_failureForBadAction() throws Exception {
        mockMvc.perform(put(ROUTE_PROVISIONING_ACTIONS + "/1/provisioningsproduct/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"parentTagCode\": \"MISSING\", \"parentTagAction\": \"UNKNOWN\"}"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteProvisioningProduct_success() throws Exception {
        mockMvc.perform(delete(ROUTE_PROVISIONING_ACTIONS + "/1/provisioningsproduct/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProvisioningProduct_missing() throws Exception {
        mockMvc.perform(delete(ROUTE_PROVISIONING_ACTIONS + "/1/provisioningsproduct/99"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Provisioning product with ID '99' was not found\"}"));
    }
}
