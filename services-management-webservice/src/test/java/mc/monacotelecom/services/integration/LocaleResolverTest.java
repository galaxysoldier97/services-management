package mc.monacotelecom.services.integration;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static org.apache.http.HttpHeaders.ACCEPT_LANGUAGE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({"/sql/clean.sql"})
class LocaleResolverTest extends BaseIntegrationTest {

    @Test
    void englishErrorByDefault() throws Exception {
        mockMvc.perform(get("/private/auth/services/13"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"error\": \"Service with ID '13' was not found\"}"));
    }

    @Test
    void frenchErrorIfFrHeader() throws Exception {
        mockMvc.perform(get("/private/auth/services/13")
                .header(ACCEPT_LANGUAGE, "fr-FR"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"error\": \"Le service d'ID '13' n'existe pas\"}"));
    }

    @Test
    void englishErrorIfEnHeader() throws Exception {
        mockMvc.perform(get("/private/auth/services/13")
                .header(ACCEPT_LANGUAGE, "en-US"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"error\": \"Service with ID '13' was not found\"}"));
    }

    @Test
    void englishErrorIfUnknownHeader() throws Exception {
        mockMvc.perform(get("/private/auth/services/13")
                .header(ACCEPT_LANGUAGE, "ko"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"error\": \"Service with ID '13' was not found\"}"));
    }
}
