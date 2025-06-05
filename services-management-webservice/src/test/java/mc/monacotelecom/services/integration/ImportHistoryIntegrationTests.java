package mc.monacotelecom.services.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql({"/sql/clean.sql", "/sql/import_history_data.sql"})
class ImportHistoryIntegrationTests extends BaseIntegrationTest {

    final String baseUrl = "/private/auth/import";

    @Test
    void getById_success() throws Exception {
        final long id = 1L;

        mockMvc.perform(get(baseUrl + "/history/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.importStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.fileName").value("TRP-EQT-Import Plmn.xlsx"))
                .andExpect(jsonPath("$.start").value("2021-01-11T17:22:12"))
                .andExpect(jsonPath("$.end").value("2021-01-11T17:22:21"))
                .andExpect(jsonPath("$.errors", hasSize(0)));
    }

    @Test
    void getById_notFound() throws Exception {
        final long id = 10L;

        mockMvc.perform(get(baseUrl + "/history/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{ \"error\": \"Import History with ID '10' could not be found\" }"));
    }

    @Test
    void deleteLast_success() throws Exception {
        mockMvc.perform(delete(baseUrl + "/history/last")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void search_success() throws Exception {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("fileName","TRP-EQT-Import Provider.xlsx");

        mockMvc.perform(get(baseUrl + "/history/search")
                .contentType(MediaType.APPLICATION_JSON)
                .params(map))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].importStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.content[0].fileName").value("TRP-EQT-Import Provider.xlsx"))
                .andExpect(jsonPath("$.content[0].start").value("2021-01-11T17:23:37"))
                .andExpect(jsonPath("$.content[0].end").value("2021-01-11T17:23:37"))
                .andExpect(jsonPath("$.content[0].errors", hasSize(0)));
    }
}
