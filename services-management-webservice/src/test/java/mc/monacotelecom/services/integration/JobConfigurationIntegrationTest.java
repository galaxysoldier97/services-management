package mc.monacotelecom.services.integration;

import mc.monacotelecom.services.dto.AddJobConfigurationDTO;
import mc.monacotelecom.services.dto.JobConfigurationDTO;
import mc.monacotelecom.services.enums.JobRecyclingOperation;
import mc.monacotelecom.services.enums.ServiceActivity;
import mc.monacotelecom.services.enums.ServiceCategory;
import mc.monacotelecom.services.enums.Status;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({"/sql/clean.sql", "/sql/job_admin_data.sql"})
class JobConfigurationIntegrationTest extends BaseIntegrationTest {

    final String baseUrl = "/api/v1/private/auth/job/configuration";

    @Test
    void search() throws Exception {
        mockMvc.perform(get(baseUrl + "?enabled=true&operation=&SERVICES_PURGE&status=DEACTIVATED&activity=INTERNET&category=ACCESS"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].days").value(2))
                .andExpect(jsonPath("$.content[0].enabled").value(true))
                .andExpect(jsonPath("$.content[0].operation").value("SERVICES_PURGE"))
                .andExpect(jsonPath("$.content[0].status").value("DEACTIVATED"))
                .andExpect(jsonPath("$.content[0].category").value("ACCESS"))
                .andExpect(jsonPath("$.content[0].activity").value("INTERNET"));
    }

    @Test
    void getById_success() throws Exception {
        mockMvc.perform(get(baseUrl + "/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.days").value(1))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.operation").value("SERVICES_PURGE"))
                .andExpect(jsonPath("$.status").value("CANCELED"))
                .andExpect(jsonPath("$.category").value("COMPONENT"))
                .andExpect(jsonPath("$.activity").value(IsNull.nullValue()));
    }

    @Test
    void getById_missing() throws Exception {
        mockMvc.perform(get(baseUrl + "/99"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Job configuration with id '99' not found"));
    }

    @Test
    void add_success() throws Exception {
        var dto = new AddJobConfigurationDTO();
        dto.setCategory(ServiceCategory.COMPONENT);
        dto.setActivity(ServiceActivity.MOBILE);
        dto.setStatus(Status.ACTIVATED);
        dto.setEnabled(true);
        dto.setDays(78L);
        dto.setOperation(JobRecyclingOperation.SERVICES_PURGE);

        mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.days").value(78))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.operation").value("SERVICES_PURGE"))
                .andExpect(jsonPath("$.status").value("ACTIVATED"))
                .andExpect(jsonPath("$.category").value("COMPONENT"))
                .andExpect(jsonPath("$.activity").value("MOBILE"));
    }

    @Test
    void add_missingOperation() throws Exception {
        var dto = new AddJobConfigurationDTO();
        dto.setEnabled(true);
        dto.setDays(58L);
        dto.setOperation(null);

        mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Rejected value 'null' for field 'operation'"));
    }

    @Test
    void update_success() throws Exception {
        var dto = new JobConfigurationDTO();
        dto.setCategory(ServiceCategory.COMPONENT);
        dto.setActivity(ServiceActivity.MOBILE);
        dto.setStatus(Status.DEACTIVATED);
        dto.setDays(150L);
        dto.setEnabled(false);

        mockMvc.perform(patch(baseUrl + "/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.days").value(150))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.operation").value("SERVICES_PURGE"))
                .andExpect(jsonPath("$.status").value("DEACTIVATED"))
                .andExpect(jsonPath("$.category").value("COMPONENT"))
                .andExpect(jsonPath("$.activity").value("MOBILE"));
    }

    @Test
    void update_missing() throws Exception {
        var dto = new JobConfigurationDTO();

        mockMvc.perform(patch(baseUrl + "/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Job configuration with id '99' not found"));
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(delete(baseUrl + "/1"))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertFalse(jobConfigurationRepository.findById(1L).isPresent());
    }

    @Test
    void delete_missing() throws Exception {
        mockMvc.perform(delete(baseUrl + "/99"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Job configuration with id '99' not found"));
    }
}
