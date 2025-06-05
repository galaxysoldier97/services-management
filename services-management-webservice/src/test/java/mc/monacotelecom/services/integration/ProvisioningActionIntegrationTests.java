package mc.monacotelecom.services.integration;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Sql({"/sql/clean.sql", "/sql/provisioning_action_data.sql"})
class ProvisioningActionIntegrationTests extends BaseIntegrationTest {

    private static final String ROUTE_PROVISIONING_ACTIONS = "/private/auth/provisioningactions";

    @Test
    void getAll_success() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTIONS + "?page=0&size=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].tagActionId").value(1))
                .andExpect(jsonPath("$.content[0].provisioningTag.tagId").value(1))
                .andExpect(jsonPath("$.content[0].provisioningTag.tagCode").value("HAXACC"))
                .andExpect(jsonPath("$.content[0].provisioningTag.description").value("Mobile Access "))
                .andExpect(jsonPath("$.content[0].provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$.content[0].provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$.content[0].provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$.content[0].provisioningTag.nature").value("A"))
                .andExpect(jsonPath("$.content[0].provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$.content[0].provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.content[0].provisioningTag.links", hasSize(0)))
                .andExpect(jsonPath("$.content[0].tagAction").value("PURCHASE"))
                .andExpect(jsonPath("$.content[0].provActionParameters", hasSize(1)))
                .andExpect(jsonPath("$.content[0].provActionParameters[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$.content[0].provActionParameters[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$.content[0].provActionParameters[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$.content[0].provActionParameters[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$.content[0].provActionParameters[0].technicalParameter.links", hasSize(0)))
                .andExpect(jsonPath("$.content[0].provActionParameters[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$.content[0].provActionParameters[0].links", hasSize(0)))
                .andExpect(jsonPath("$.content[0].serviceAction").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.content[0].links", hasSize(0)))
                .andExpect(jsonPath("$.content[1].tagActionId").value(2))
                .andExpect(jsonPath("$.content[1].provisioningTag.tagId").value(1))
                .andExpect(jsonPath("$.content[1].provisioningTag.tagCode").value("HAXACC"))
                .andExpect(jsonPath("$.content[1].provisioningTag.description").value("Mobile Access "))
                .andExpect(jsonPath("$.content[1].provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$.content[1].provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$.content[1].provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$.content[1].provisioningTag.nature").value("A"))
                .andExpect(jsonPath("$.content[1].provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$.content[1].provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.content[1].provisioningTag.links", hasSize(0)))
                .andExpect(jsonPath("$.content[1].tagAction").value("CANCEL"))
                .andExpect(jsonPath("$.content[1].provActionParameters", hasSize(0)))
                .andExpect(jsonPath("$.content[1].serviceAction").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.content[1].links", hasSize(0)));
    }

    @Test
    void getById_success() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTIONS + "/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagActionId").value(1))
                .andExpect(jsonPath("$.provisioningTag.tagId").value(1))
                .andExpect(jsonPath("$.provisioningTag.tagCode").value("HAXACC"))
                .andExpect(jsonPath("$.provisioningTag.description").value("Mobile Access "))
                .andExpect(jsonPath("$.provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$.provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$.provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$.provisioningTag.nature").value("A"))
                .andExpect(jsonPath("$.provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$.provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.tagAction").value("PURCHASE"))
                .andExpect(jsonPath("$.provActionParameters", hasSize(1)))
                .andExpect(jsonPath("$.provActionParameters[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$.provActionParameters[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$.provActionParameters[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$.provActionParameters[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$.provActionParameters[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$.serviceAction").value(IsNull.nullValue()));
    }

    @Test
    void getById_missing() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTIONS + "/99"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":400,\"error\":\"Provisioning action with ID '99' was not found\"}"));
    }

    @Test
    void add_success() throws Exception {
        mockMvc.perform(post(ROUTE_PROVISIONING_ACTIONS)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"tagId\": 3,\"tagAction\": \"CANCEL\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagActionId").value(4))
                .andExpect(jsonPath("$.provisioningTag.tagId").value(3))
                .andExpect(jsonPath("$.provisioningTag.tagCode").value("HECARD"))
                .andExpect(jsonPath("$.provisioningTag.description").value("Sim Card"))
                .andExpect(jsonPath("$.provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$.provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$.provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$.provisioningTag.nature").value("E"))
                .andExpect(jsonPath("$.provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$.provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.tagAction").value("CANCEL"))
                .andExpect(jsonPath("$.provActionParameters", hasSize(0)))
                .andExpect(jsonPath("$.serviceAction").value(IsNull.nullValue()));
    }

    @Test
    void add_successWithServiceAction() throws Exception {
        mockMvc.perform(post(ROUTE_PROVISIONING_ACTIONS)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"tagId\": 3,\"tagAction\": \"CANCEL\", \"serviceAction\": \"ACTIVATION\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagActionId").value(4))
                .andExpect(jsonPath("$.provisioningTag.tagId").value(3))
                .andExpect(jsonPath("$.provisioningTag.tagCode").value("HECARD"))
                .andExpect(jsonPath("$.provisioningTag.description").value("Sim Card"))
                .andExpect(jsonPath("$.provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$.provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$.provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$.provisioningTag.nature").value("E"))
                .andExpect(jsonPath("$.provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$.provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.tagAction").value("CANCEL"))
                .andExpect(jsonPath("$.provActionParameters", hasSize(0)))
                .andExpect(jsonPath("$.serviceAction").value("ACTIVATION"));
    }

    @Test
    void add_failureForBadAction() throws Exception {
        mockMvc.perform(post(ROUTE_PROVISIONING_ACTIONS)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"tagId\": 3,\"tagAction\": \"UNKNOWN\"}"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void add_failureForBadServiceAction() throws Exception {
        mockMvc.perform(post(ROUTE_PROVISIONING_ACTIONS)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"tagId\": 3,\"tagAction\": \"CANCEL\", \"serviceAction\": \"UNKNOWN\"}"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void add_failureForExisting() throws Exception {
        mockMvc.perform(post(ROUTE_PROVISIONING_ACTIONS)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"tagId\": 1,\"tagAction\": \"CANCEL\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"Provisioning action with tag code 'HAXACC' and tag action 'CANCEL' already exists\"}"));
    }

    @Test
    void update_success() throws Exception {
        mockMvc.perform(put(ROUTE_PROVISIONING_ACTIONS + "/3")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"serviceAction\": \"DEACTIVATION\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagActionId").value(3))
                .andExpect(jsonPath("$.provisioningTag.tagId").value(3))
                .andExpect(jsonPath("$.provisioningTag.tagCode").value("HECARD"))
                .andExpect(jsonPath("$.provisioningTag.description").value("Sim Card"))
                .andExpect(jsonPath("$.provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$.provisioningTag.componentType").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$.provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$.provisioningTag.nature").value("E"))
                .andExpect(jsonPath("$.provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$.provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.tagAction").value("PURCHASE"))
                .andExpect(jsonPath("$.provActionParameters", hasSize(0)))
                .andExpect(jsonPath("$.serviceAction").value("DEACTIVATION"));
    }

    @Test
    void update_failureForBadServiceAction() throws Exception {
        mockMvc.perform(put(ROUTE_PROVISIONING_ACTIONS + "/3")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"tagId\": 3,\"tagAction\": \"CANCEL\", \"serviceAction\": \"UNKNOWN\"}"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(delete(ROUTE_PROVISIONING_ACTIONS + "/2"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_missing() throws Exception {
        mockMvc.perform(delete(ROUTE_PROVISIONING_ACTIONS + "/99"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"error\":\"Provisioning action with ID '99' was not found\"}"));
    }

    @Test
    void delete_failureIfUsedByProvisioningProduct() throws Exception {
        mockMvc.perform(delete(ROUTE_PROVISIONING_ACTIONS + "/1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"Provisioning action with ID '1' cannot be removed because it is used by provisioning product(s) with ID(s) [1]\"}"));
    }

    @Test
    void delete_usedByProvisioningProductWithForce() throws Exception {
        mockMvc.perform(delete(ROUTE_PROVISIONING_ACTIONS + "/1?force=true"))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertTrue(provisioningActionRepository.findByInternalId(1L).isEmpty());
        assertTrue(provisioningProductRepository.findByInternalId(1L).isEmpty());
    }

    @Test
    void getProvisioningActionParameterById_success() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTIONS + "/1/parameters/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$.technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$.technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$.technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$.parameterValue").value("TEST"));
    }

    @Test
    void getProvisioningActionParameterById_missing() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTIONS + "/1/parameters/2"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Provisioning action parameter with action ID '1' and parameter id '2' was not found\"}"));
    }

    @Test
    void getProvisioningActionParameterById_noParameters() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTIONS + "/2/parameters/2"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Provisioning action parameter with action ID '2' and parameter id '2' was not found\"}"));
    }

    @Test
    void addProvisioningActionParameter_success() throws Exception {
        mockMvc.perform(post(ROUTE_PROVISIONING_ACTIONS + "/1/parameters")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"technicalParameter\": { \"parameterId\": 2}, \"parameterValue\": \"SOMETHING\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technicalParameter.parameterId").value(2))
                .andExpect(jsonPath("$.technicalParameter.parameterCode").value("PARAM2"))
                .andExpect(jsonPath("$.technicalParameter.description").value("Technical Parameter 2"))
                .andExpect(jsonPath("$.technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$.parameterValue").value("SOMETHING"));
    }

    @Test
    void addProvisioningActionParameter_failureForMissingProvisioningAction() throws Exception {
        mockMvc.perform(post(ROUTE_PROVISIONING_ACTIONS + "/99/parameters")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"technicalParameter\": { \"parameterId\": 2}, \"parameterValue\": \"SOMETHING\"}"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Provisioning action with ID '99' was not found\"}"));
    }

    @Test
    void addProvisioningActionParameter_failureForMissingTechnicalParameter() throws Exception {
        mockMvc.perform(post(ROUTE_PROVISIONING_ACTIONS + "/1/parameters")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"technicalParameter\": { \"parameterId\": 99}, \"parameterValue\": \"SOMETHING\"}"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Technical parameter with ID '99' was not found\"}"));
    }

    @Test
    void addProvisioningActionParameter_successWithoutValue() throws Exception {
        mockMvc.perform(post(ROUTE_PROVISIONING_ACTIONS + "/1/parameters")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"technicalParameter\": { \"parameterId\": 2}}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technicalParameter.parameterId").value(2))
                .andExpect(jsonPath("$.technicalParameter.parameterCode").value("PARAM2"))
                .andExpect(jsonPath("$.technicalParameter.description").value("Technical Parameter 2"))
                .andExpect(jsonPath("$.technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$.parameterValue").value(IsNull.nullValue()));
    }

    @Test
    void updateProvisioningActionParameter_success() throws Exception {
        mockMvc.perform(put(ROUTE_PROVISIONING_ACTIONS + "/1/parameters/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"parameterValue\": \"SOMETHING\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$.technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$.technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$.technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$.parameterValue").value("SOMETHING"));
    }

    @Test
    void updateProvisioningActionParameter_missingProvisioningAction() throws Exception {
        mockMvc.perform(put(ROUTE_PROVISIONING_ACTIONS + "/99/parameters/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"parameterValue\": \"SOMETHING\"}"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Provisioning action with ID '99' was not found\"}"));
    }

    @Test
    void updateProvisioningActionParameter_missingTechnicalParameter() throws Exception {
        mockMvc.perform(put(ROUTE_PROVISIONING_ACTIONS + "/1/parameters/99")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"parameterValue\": \"SOMETHING\"}"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Technical parameter with ID '99' was not found\"}"));
    }

    @Test
    void deleteProvisioningActionParameter_success() throws Exception {
        mockMvc.perform(delete(ROUTE_PROVISIONING_ACTIONS + "/1/parameters/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProvisioningActionParameter_missingProvisioningAction() throws Exception {
        mockMvc.perform(delete(ROUTE_PROVISIONING_ACTIONS + "/99/parameters/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Provisioning action with ID '99' was not found\"}"));
    }

    @Test
    void deleteProvisioningActionParameter_missingTechnicalParameter() throws Exception {
        mockMvc.perform(delete(ROUTE_PROVISIONING_ACTIONS + "/1/parameters/99"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Technical parameter with ID '99' was not found\"}"));
    }

    @Test
    void getAllProvisioningActionForProvisioningTag_success() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTIONS + "/provisioningtag/1?size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].tagActionId").value(1))
                .andExpect(jsonPath("$.content[0].provisioningTag.tagId").value(1))
                .andExpect(jsonPath("$.content[0].provisioningTag.tagCode").value("HAXACC"))
                .andExpect(jsonPath("$.content[0].provisioningTag.description").value("Mobile Access "))
                .andExpect(jsonPath("$.content[0].provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$.content[0].provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$.content[0].provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$.content[0].provisioningTag.nature").value("A"))
                .andExpect(jsonPath("$.content[0].provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$.content[0].provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.content[0].provisioningTag.links", hasSize(0)))
                .andExpect(jsonPath("$.content[0].tagAction").value("PURCHASE"))
                .andExpect(jsonPath("$.content[0].provActionParameters", hasSize(1)))
                .andExpect(jsonPath("$.content[0].provActionParameters[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$.content[0].provActionParameters[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$.content[0].provActionParameters[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$.content[0].provActionParameters[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$.content[0].provActionParameters[0].technicalParameter.links", hasSize(0)))
                .andExpect(jsonPath("$.content[0].provActionParameters[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$.content[0].provActionParameters[0].links", hasSize(0)))
                .andExpect(jsonPath("$.content[0].serviceAction").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.content[0].links", hasSize(0)));
    }

    @Test
    void getAllProvisioningActionForProvisioningTag_missingProvisioningTag() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTIONS + "/provisioningtag/99?size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }
}
