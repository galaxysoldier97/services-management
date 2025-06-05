package mc.monacotelecom.services.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Sql({"/sql/clean.sql", "/sql/technical_parameter_data.sql"})
class TechnicalParameterIntegrationTests extends BaseIntegrationTest {

    private static final String ROUTE_TECHNICAL_PARAMETERS = "/private/auth/technicalparameters";

    @Test
    void getAll_success() throws Exception {
        mockMvc.perform(get(ROUTE_TECHNICAL_PARAMETERS + "?page=0&size=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.technicalParameterDToes", hasSize(2)))
                .andExpect(jsonPath("$._embedded.technicalParameterDToes[0].parameterId").value(1))
                .andExpect(jsonPath("$._embedded.technicalParameterDToes[0].parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$._embedded.technicalParameterDToes[0].description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$._embedded.technicalParameterDToes[0].parameterType").value("STATIC"))
                .andExpect(jsonPath("$._embedded.technicalParameterDToes[1].parameterId").value(2))
                .andExpect(jsonPath("$._embedded.technicalParameterDToes[1].parameterCode").value("PARAM2"))
                .andExpect(jsonPath("$._embedded.technicalParameterDToes[1].description").value("Technical Parameter 2"))
                .andExpect(jsonPath("$._embedded.technicalParameterDToes[1].parameterType").value("CONTEXT"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost"))
                .andExpect(jsonPath("$._links.next.href").value("http://localhost?page=1&size=2"));
    }

    @Test
    void getById_success() throws Exception {
        mockMvc.perform(get(ROUTE_TECHNICAL_PARAMETERS + "/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parameterId").value(1))
                .andExpect(jsonPath("$.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$.description").value("Technical Parameter 1"))
                .andExpect(jsonPath("$.parameterType").value("STATIC"));
    }

    @Test
    void getById_missing() throws Exception {
        mockMvc.perform(get(ROUTE_TECHNICAL_PARAMETERS + "/99"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Technical parameter with ID '99' was not found\"}"));
    }

    @Test
    void add_success() throws Exception {
        mockMvc.perform(post(ROUTE_TECHNICAL_PARAMETERS)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"parameterCode\": \"PARAM100\", \"description\": \"Technical Parameter 100\", \"parameterType\": \"STATIC\"}"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.parameterId").value(6))
                .andExpect(jsonPath("$.parameterCode").value("PARAM100"))
                .andExpect(jsonPath("$.description").value("Technical Parameter 100"))
                .andExpect(jsonPath("$.parameterType").value("STATIC"));
    }

    @Test
    void add_success_existingCodeDifferentType() throws Exception {
        mockMvc.perform(post(ROUTE_TECHNICAL_PARAMETERS)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"parameterCode\": \"PARAM1\", \"description\": \"Technical Parameter 100\", \"parameterType\": \"CONTEXT\"}"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.parameterId").value(6))
                .andExpect(jsonPath("$.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$.description").value("Technical Parameter 100"))
                .andExpect(jsonPath("$.parameterType").value("CONTEXT"));
    }

    @Test
    void add_failureForExistingCodeAndParam() throws Exception {
        mockMvc.perform(post(ROUTE_TECHNICAL_PARAMETERS)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"parameterCode\": \"PARAM1\", \"description\": \"Technical Parameter 100\", \"parameterType\": \"STATIC\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"Technical parameter with technical code 'PARAM1' and technical type 'STATIC' already exists\"}"));
    }

    @Test
    void add_failureForBadType() throws Exception {
        mockMvc.perform(post(ROUTE_TECHNICAL_PARAMETERS)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"parameterCode\": \"PARAM100\", \"description\": \"Technical Parameter 100\", \"parameterType\": \"UNKNOWN\"}"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void update_success() throws Exception {
        mockMvc.perform(put(ROUTE_TECHNICAL_PARAMETERS + "/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"description\": \"Technical Parameter 100\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parameterId").value(1))
                .andExpect(jsonPath("$.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$.description").value("Technical Parameter 100"))
                .andExpect(jsonPath("$.parameterType").value("STATIC"));
    }

    @Test
    void update_notFound() throws Exception {
        mockMvc.perform(put(ROUTE_TECHNICAL_PARAMETERS + "/6")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"parameterCode\": \"PARAM1\", \"description\": \"Technical Parameter 100\", \"parameterType\": \"CONTEXT\"}"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Technical parameter with ID '6' was not found\"}"));
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(delete(ROUTE_TECHNICAL_PARAMETERS + "/3"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_failureIfUsedByAction() throws Exception {
        mockMvc.perform(delete(ROUTE_TECHNICAL_PARAMETERS + "/1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":400,\"error\":\"Technical parameter with ID '1' cannot be deleted because it is referenced by provisioning actions '[HAXACC - PURCHASE]'\"}"));
    }

    @Test
    void delete_successIfUsedByService() throws Exception {
        mockMvc.perform(delete(ROUTE_TECHNICAL_PARAMETERS + "/4"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_missing() throws Exception {
        mockMvc.perform(delete(ROUTE_TECHNICAL_PARAMETERS + "/99"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Technical parameter with ID '99' was not found\"}"));
    }

    @Test
    void search_success() throws Exception {
        mockMvc.perform(get(ROUTE_TECHNICAL_PARAMETERS + "/search?size=1&code=PARAM2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.technicalParameterDToes", hasSize(1)))
                .andExpect(jsonPath("$._embedded.technicalParameterDToes[0].parameterId").value(2))
                .andExpect(jsonPath("$._embedded.technicalParameterDToes[0].parameterCode").value("PARAM2"))
                .andExpect(jsonPath("$._embedded.technicalParameterDToes[0].description").value("Technical Parameter 2"))
                .andExpect(jsonPath("$._embedded.technicalParameterDToes[0].parameterType").value("CONTEXT"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/technicalparameters"));
    }
}
