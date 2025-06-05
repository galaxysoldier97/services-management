package mc.monacotelecom.services.integration;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Sql({"/sql/clean.sql", "/sql/tag_activation_code_data.sql"})
class TagActivationCodeIntegrationTests extends BaseIntegrationTest {

    private static final String ROUTE_TAG_ACTIVATION_CODES = "/private/auth/tagactivationcodes";

    @Test
    void getAll_success() throws Exception {
        mockMvc.perform(get(ROUTE_TAG_ACTIVATION_CODES + "?page=0&size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes", hasSize(1)))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.activationCodeId").value(1))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.code").value("HPX_126"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.description").value("BroadBand Mobile "))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.nature").value("PROFILE"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.networkComponent").value("SPG"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.tagId").value(1))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.tagCode").value("HAXACC"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.description").value("Mobile Access "))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.componentType").value(IsNull.nullValue()))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.nature").value("A"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].tagValue").value(367001600));
    }

    @Test
    void search_successWithTagCodes() throws Exception {
        mockMvc.perform(get(ROUTE_TAG_ACTIVATION_CODES + "/search?tagCodes=HPXBRDBAND1&page=0&size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes", hasSize(1)))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.activationCodeId").value(2))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.code").value("HOD_72"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.description").value("Service Data GPRS"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.nature").value("OPTION"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.networkComponent").value("SPG"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.tagId").value(2))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.tagCode").value("HPXBRDBAND1"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.description").value("Broadband In a Box - Profile 1"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.activity").value("INTERNET"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.accessType").value("MOBILE"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.nature").value("P"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].tagValue").value(367001601))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/tagactivationcodes"));
    }

    @Test
    void search_successWithAccessType() throws Exception {
        mockMvc.perform(get(ROUTE_TAG_ACTIVATION_CODES + "/search?accessType=BBHB&page=0&size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes", hasSize(1)))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.activationCodeId").value(1))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.code").value("HPX_126"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.description").value("BroadBand Mobile "))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.nature").value("PROFILE"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.networkComponent").value("SPG"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.tagId").value(1))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.tagCode").value("HAXACC"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.description").value("Mobile Access "))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.nature").value("A"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].tagValue").value(367001600))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/tagactivationcodes"));
    }

    @Test
    void search_successWithNature() throws Exception {
        mockMvc.perform(get(ROUTE_TAG_ACTIVATION_CODES + "/search?nature=P&page=0&size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes", hasSize(1)))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.activationCodeId").value(2))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.code").value("HOD_72"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.description").value("Service Data GPRS"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.nature").value("OPTION"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.networkComponent").value("SPG"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.tagId").value(2))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.tagCode").value("HPXBRDBAND1"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.description").value("Broadband In a Box - Profile 1"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.activity").value("INTERNET"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.accessType").value("MOBILE"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.nature").value("P"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].tagValue").value(367001601))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/tagactivationcodes"));
    }

    @Test
    void search_successWithActivity() throws Exception {
        mockMvc.perform(get(ROUTE_TAG_ACTIVATION_CODES + "/search?activity=MOBILE&page=0&size=1"))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes", hasSize(1)))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.activationCodeId").value(1))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.code").value("HPX_126"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.description").value("BroadBand Mobile "))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.nature").value("PROFILE"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].activationCode.networkComponent").value("SPG"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.tagId").value(1))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.tagCode").value("HAXACC"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.description").value("Mobile Access "))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.activity").value("MOBILE"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.accessType").value("BBHB"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.nature").value("A"))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$._embedded.tagActivationCodeDetailsDToes[0].tagValue").value(367001600))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/tagactivationcodes"));
    }

    @Test
    void search_successEmpty() throws Exception {
        mockMvc.perform(get(ROUTE_TAG_ACTIVATION_CODES + "/search?tagCodes=NOTHINGpage=0&size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"_links\":{\"self\":{\"href\":\"http://localhost/tagactivationcodes\"}},\"page\":{\"size\":1,\"totalElements\":0,\"totalPages\":0,\"number\":0}}"));
    }

    @Test
    void add_success() throws Exception {
        mockMvc.perform(post(ROUTE_TAG_ACTIVATION_CODES)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"activId\": 1, \"tagId\": 2, \"tagValue\": \"123456\"}"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.activationCode.activationCodeId").value(1))
                .andExpect(jsonPath("$.activationCode.code").value("HPX_126"))
                .andExpect(jsonPath("$.activationCode.description").value("BroadBand Mobile "))
                .andExpect(jsonPath("$.activationCode.nature").value("PROFILE"))
                .andExpect(jsonPath("$.activationCode.networkComponent").value("SPG"))
                .andExpect(jsonPath("$.provisioningTag.tagId").value(2))
                .andExpect(jsonPath("$.provisioningTag.tagCode").value("HPXBRDBAND1"))
                .andExpect(jsonPath("$.provisioningTag.description").value("Broadband In a Box - Profile 1"))
                .andExpect(jsonPath("$.provisioningTag.activity").value("INTERNET"))
                .andExpect(jsonPath("$.provisioningTag.category").value("ACCESS"))
                .andExpect(jsonPath("$.provisioningTag.componentType").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.provisioningTag.accessType").value("MOBILE"))
                .andExpect(jsonPath("$.provisioningTag.nature").value("P"))
                .andExpect(jsonPath("$.provisioningTag.persistent").value(true))
                .andExpect(jsonPath("$.provisioningTag.tagParameters").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.tagValue").value(123456));
    }

    @Test
    void add_failureForMissingActivationCode() throws Exception {
        mockMvc.perform(post(ROUTE_TAG_ACTIVATION_CODES)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"activId\": 99, \"tagId\": 2, \"tagValue\": \"123456\"}"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Activation code with ID '99' was not found\"}"));
    }

    @Test
    void add_failureForMissingProvisioningTag() throws Exception {
        mockMvc.perform(post(ROUTE_TAG_ACTIVATION_CODES)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"activId\": 1, \"tagId\": 99, \"tagValue\": \"123456\"}"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Provisioning tag with ID '99' was not found\"}"));
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(delete(ROUTE_TAG_ACTIVATION_CODES + "/1/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_missingActivationCode() throws Exception {
        mockMvc.perform(delete(ROUTE_TAG_ACTIVATION_CODES + "/1/99"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Activation code with ID '99' was not found\"}"));
    }

    @Test
    void delete_missingProvisioningTag() throws Exception {
        mockMvc.perform(delete(ROUTE_TAG_ACTIVATION_CODES + "/99/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Provisioning tag with ID '99' was not found\"}"));
    }
}
