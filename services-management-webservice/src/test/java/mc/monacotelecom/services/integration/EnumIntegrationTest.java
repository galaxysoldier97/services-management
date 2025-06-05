package mc.monacotelecom.services.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EnumIntegrationTest extends BaseIntegrationTest {

    final String baseUrl = "/public/enums";

    @Test
    void list_accessType_success() throws Exception {
        mockMvc.perform(get(baseUrl + "/network")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[\"DOCSIS\",\"FTTH\",\"DISE\",\"FREEDHOME\",\"ZATTOO\",\"TRUNKSIP\",\"BBHB\",\"MOBILE\"]"));
    }

    @Test
    void list_activity_success() throws Exception {
        mockMvc.perform(get(baseUrl + "/activity")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[\"MOBILE\", \"INTERNET\", \"TELEPHONY\", \"TV\", \"NDD\", \"OX\", \"MEVO\"]"));
    }

    @Test
    void list_componentType_success() throws Exception {
        mockMvc.perform(get(baseUrl + "/componentType")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[\"TEAMS\",\"CAS\"]"));
    }

}
