package mc.monacotelecom.services.integration;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql({"/sql/clean.sql", "/sql/provisioning_action_parameter_data.sql"})
class ProvisioningActionParameterIntegrationTests extends BaseIntegrationTest {

    private static final String ROUTE_PROVISIONING_ACTION_PARAMETERS = "/private/auth/tagparametervalues";

    @Test
    void getAll_success() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTION_PARAMETERS + "?page=0&size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes", hasSize(1)))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.tagActionId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.tagId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.tagCode").value("HAXACC"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.description").value("Mobile Access "))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.nature").value("A"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.tagAction").value("PURCHASE"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters", hasSize(1)))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.serviceAction").value(IsNull.nullValue()))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost"));
    }

    @Test
    void search_successWithTagCode() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTION_PARAMETERS + "/search?tagCodes=HAXACC&page=0&size=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes", hasSize(1)))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.tagActionId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.tagId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.tagCode").value("HAXACC"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.description").value("Mobile Access "))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.nature").value("A"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.tagAction").value("PURCHASE"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters", hasSize(1)))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.serviceAction").value(IsNull.nullValue()))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/tagactivationcodes"));
    }

    @Test
    void search_successWithAccessType() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTION_PARAMETERS + "/search?accessType=BBHB&page=0&size=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes", hasSize(1)))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.tagActionId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.tagId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.tagCode").value("HAXACC"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.description").value("Mobile Access "))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.nature").value("A"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.tagAction").value("PURCHASE"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters", hasSize(1)))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.serviceAction").value(IsNull.nullValue()))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/tagactivationcodes"));
    }

    @Test
    void search_successWithNature() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTION_PARAMETERS + "/search?nature=A&page=0&size=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes", hasSize(1)))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.tagActionId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.tagId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.tagCode").value("HAXACC"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.description").value("Mobile Access "))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.nature").value("A"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.tagAction").value("PURCHASE"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters", hasSize(1)))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.serviceAction").value(IsNull.nullValue()))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/tagactivationcodes"));
    }

    @Test
    void search_successWithActivity() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTION_PARAMETERS + "/search?activity=MOBILE&page=0&size=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes", hasSize(1)))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.tagActionId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.tagId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.tagCode").value("HAXACC"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.description").value("Mobile Access "))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.nature").value("A"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.tagAction").value("PURCHASE"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters", hasSize(1)))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.provActionParameters[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].provisioningActionDTO.serviceAction").value(IsNull.nullValue()))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$._embedded.provisioningActionParameterDetailsDToes[0].parameterValue").value("TEST"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/tagactivationcodes"));
    }

    @Test
    void search_successEmpty() throws Exception {
        mockMvc.perform(get(ROUTE_PROVISIONING_ACTION_PARAMETERS + "/search?activity=INTERNET&page=0&size=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"_links\":{\"self\":{\"href\":\"http://localhost/tagactivationcodes\"}},\"page\":{\"size\":2,\"totalElements\":0,\"totalPages\":0,\"number\":0}}"));
    }

    @Test
    void add_success() throws Exception {
        mockMvc.perform(post(ROUTE_PROVISIONING_ACTION_PARAMETERS)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"tagActionId\": 3, \"parameterId\": 1, \"parameterValue\": \"SOMETHING\"}"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.provisioningActionDTO.tagActionId").value(3))
                .andExpect(jsonPath("$.provisioningActionDTO.provisioningTag.tagId").value(2))
                .andExpect(jsonPath("$.provisioningActionDTO.provisioningTag.tagCode").value("HECARD"))
                .andExpect(jsonPath("$.provisioningActionDTO.provisioningTag.description").value("Sim Card"))
                .andExpect(jsonPath("$.provisioningActionDTO.provisioningTag.activity").value("TELEPHONY"))
                .andExpect(jsonPath("$.provisioningActionDTO.provisioningTag.category").value("COMPONENT"))
                .andExpect(jsonPath("$.provisioningActionDTO.provisioningTag.accessType").value("MOBILE"))
                .andExpect(jsonPath("$.provisioningActionDTO.provisioningTag.nature").value("E"))
                .andExpect(jsonPath("$.provisioningActionDTO.provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$.provisioningActionDTO.provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.provisioningActionDTO.tagAction").value("PURCHASE"))
                .andExpect(jsonPath("$.provisioningActionDTO.provActionParameters", hasSize(1)))
                .andExpect(jsonPath("$.provisioningActionDTO.provActionParameters[0].technicalParameter.parameterId").value(2))
                .andExpect(jsonPath("$.provisioningActionDTO.provActionParameters[0].technicalParameter.parameterCode").value("PARAM2"))
                .andExpect(jsonPath("$.provisioningActionDTO.provActionParameters[0].technicalParameter.description").value("Technical Parameter 2"))
                .andExpect(jsonPath("$.provisioningActionDTO.provActionParameters[0].technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$.provisioningActionDTO.provActionParameters[0].parameterValue").value("TEST2"))
                .andExpect(jsonPath("$.provisioningActionDTO.serviceAction").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.technicalParameter.parameterId").value(1))
                .andExpect(jsonPath("$.technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$.technicalParameter.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$.technicalParameter.parameterType").value("STATIC"))
                .andExpect(jsonPath("$.parameterValue").value("SOMETHING"));
    }

    @Test
    void add_missingProvisioningAction() throws Exception {
        mockMvc.perform(post(ROUTE_PROVISIONING_ACTION_PARAMETERS)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"tagActionId\": 99, \"parameterId\": 1, \"parameterValue\": \"SOMETHING\"}"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Provisioning action with ID '99' was not found\"}"));
    }

    @Test
    void add_missingTechnicalParameter() throws Exception {
        mockMvc.perform(post(ROUTE_PROVISIONING_ACTION_PARAMETERS)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"tagActionId\": 3, \"parameterId\": 99, \"parameterValue\": \"SOMETHING\"}"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Technical parameter with ID '99' was not found\"}"));
    }
}
