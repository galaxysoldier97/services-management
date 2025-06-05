package mc.monacotelecom.services.integration;

import mc.monacotelecom.services.dto.request.AddOrUpdateServiceParameterDTO;
import mc.monacotelecom.services.dto.request.UpdateServiceParameterDTO;
import mc.monacotelecom.services.enums.TechnicalParameterType;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Sql({"/sql/clean.sql", "/sql/service_data.sql"})
class ServiceParametersIntegrationTest extends BaseIntegrationTest {

    final String baseUrl = "/private/auth/services/{serviceId}/parameters";

    @Test
    void addServiceParameter_success() throws Exception {
        AddOrUpdateServiceParameterDTO dto1 = new AddOrUpdateServiceParameterDTO();
        dto1.setValue("2");
        dto1.setTechnicalParameterCode("PARAM1");

        AddOrUpdateServiceParameterDTO dto2 = new AddOrUpdateServiceParameterDTO();
        dto2.setValue("3");
        dto2.setTechnicalParameterCode("PARAM3");

        final String requestJson = objectMapper.writeValueAsString(List.of(dto1, dto2));

        final long serviceId = 1L;

        mockMvc.perform(post(baseUrl, serviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json("[{\"value\":\"2\",\"technicalParameter\":{\"parameterId\":20,\"parameterCode\":\"PARAM1\",\"description\":null,\"parameterType\":\"CONTEXT\",\"links\":[]}},{\"value\":\"3\",\"technicalParameter\":{\"parameterId\":22,\"parameterCode\":\"PARAM3\",\"description\":null,\"parameterType\":\"CONTEXT\",\"links\":[]}}]"));

        var serviceParam = serviceParameterRepository.findByTechnicalParameterInternalIdAndServiceId(20L, serviceId)
                .orElseThrow(() -> new RuntimeException("Service Param should exist"));

        assertAll(
                () -> assertThat(serviceParam.getTechnicalParameter().getParameterCode()).isEqualTo("PARAM1"),
                () -> assertThat(serviceParam.getTechnicalParameter().getParameterType()).isEqualTo(TechnicalParameterType.CONTEXT),
                () -> assertThat(serviceParam.getValue()).isEqualTo("2")
        );
    }


    @Test
    void deleteAllServiceParameter_success() throws Exception {
        final long serviceId = 5L;

        mockMvc.perform(delete(baseUrl , serviceId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
       assertTrue(serviceParameterRepository.findByServiceId(serviceId).isEmpty());

    }

    @Test
    void addServiceParameter_notContext() throws Exception {
        AddOrUpdateServiceParameterDTO dto = new AddOrUpdateServiceParameterDTO();
        dto.setValue("2");
        dto.setTechnicalParameterCode("PARAM1");

        final String requestJson = objectMapper.writeValueAsString(List.of(dto));

        final long serviceId = 100L;

        mockMvc.perform(post(baseUrl, serviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Service with ID '100' was not found\"}"));
    }

    @Test
    void addServiceParameter_notFound() throws Exception {
        AddOrUpdateServiceParameterDTO dto = new AddOrUpdateServiceParameterDTO();
        dto.setValue("2");
        dto.setTechnicalParameterCode("PARAM5");

        final String requestJson = objectMapper.writeValueAsString(List.of(dto));

        final long serviceId = 1L;

        mockMvc.perform(post(baseUrl, serviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Technical parameter with technical code 'PARAM5' and technical type 'CONTEXT' not found\"}"));
    }

    @Test
    void addServiceParameter_alreadyExist() throws Exception {
        AddOrUpdateServiceParameterDTO dto = new AddOrUpdateServiceParameterDTO();
        dto.setTechnicalParameterCode("PARAM1");
        dto.setValue("5");
        final String requestJson = objectMapper.writeValueAsString(List.of(dto));

        final long serviceId = 5L;

        mockMvc.perform(post(baseUrl, serviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().json("{\"error\":\"Service parameter already exists for service ID '5' and technical parameter code 'PARAM1'\"}"));
    }

    @Test
    void deleteServiceParameter_success() throws Exception {
        final long technicalParameterId = 20L;
        final long serviceId = 5L;

        mockMvc.perform(delete(baseUrl + "/{technicalParameterId}", serviceId, technicalParameterId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteServiceParameter_notFound() throws Exception {
        final long technicalParameterId = 100L;
        final long serviceId = 5L;

        mockMvc.perform(delete(baseUrl + "/{technicalParameterId}", serviceId, technicalParameterId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Service Parameter with service ID '5' and technical parameter code '100' not found\"}"));
    }

    @Test
    void patchServiceParameter_success() throws Exception {
        UpdateServiceParameterDTO dto = new UpdateServiceParameterDTO();
        dto.setValue("5");

        final long technicalParameterId = 20L;
        final long serviceId = 5L;

        final String requestJson = objectMapper.writeValueAsString(dto);
        mockMvc.perform(patch(baseUrl + "/{technicalParameterId}", serviceId, technicalParameterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value("5"))
                .andExpect(jsonPath("$.technicalParameter.parameterId").value(20))
                .andExpect(jsonPath("$.technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$.technicalParameter.description").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.technicalParameter.parameterType").value("CONTEXT"));
    }

    @Test
    void patchServiceParameter_notFound() throws Exception {
        final long technicalParameterId = 100L;
        final long serviceId = 5L;

        UpdateServiceParameterDTO dto = new UpdateServiceParameterDTO();
        dto.setValue("5");

        final String requestJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch(baseUrl + "/{technicalParameterId}", serviceId, technicalParameterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Service Parameter with service ID '5' and technical parameter code '100' not found\"}"));
    }

    @Test
    void patchServiceParameterWithCode_success() throws Exception {
        AddOrUpdateServiceParameterDTO dto1 = new AddOrUpdateServiceParameterDTO();
        dto1.setTechnicalParameterCode("PARAM1");
        dto1.setValue("5");

        AddOrUpdateServiceParameterDTO dto2 = new AddOrUpdateServiceParameterDTO();
        dto2.setTechnicalParameterCode("PARAM3");
        dto2.setValue("6");

        final long serviceId = 5L;

        final String requestJson = objectMapper.writeValueAsString(List.of(dto1, dto2));
        mockMvc.perform(patch(baseUrl, serviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].value").value("5"))
                .andExpect(jsonPath("$[0].technicalParameter.parameterId").value(20))
                .andExpect(jsonPath("$[0].technicalParameter.parameterCode").value("PARAM1"))
                .andExpect(jsonPath("$[0].technicalParameter.description").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[0].technicalParameter.parameterType").value("CONTEXT"))
                .andExpect(jsonPath("$[1].value").value("6"))
                .andExpect(jsonPath("$[1].technicalParameter.parameterId").value(22))
                .andExpect(jsonPath("$[1].technicalParameter.parameterCode").value("PARAM3"))
                .andExpect(jsonPath("$[1].technicalParameter.description").value(IsNull.nullValue()))
                .andExpect(jsonPath("$[1].technicalParameter.parameterType").value("CONTEXT"));
    }

    @Test
    void patchServiceParameterWithCode_technicalParam_notFound() throws Exception {
        AddOrUpdateServiceParameterDTO dto = new AddOrUpdateServiceParameterDTO();
        dto.setValue("2");
        dto.setTechnicalParameterCode("PARAM5");

        final String requestJson = objectMapper.writeValueAsString(List.of(dto));
        final long serviceId = 1L;

        mockMvc.perform(patch(baseUrl, serviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Technical parameter with technical code 'PARAM5' and technical type 'CONTEXT' not found\"}"));
    }

    @Test
    void patchServiceParameterWithCode_service_notFound() throws Exception {
        AddOrUpdateServiceParameterDTO dto = new AddOrUpdateServiceParameterDTO();
        dto.setValue("2");
        dto.setTechnicalParameterCode("PARAM1");

        final String requestJson = objectMapper.writeValueAsString(List.of(dto));
        final long serviceId = 2L;

        mockMvc.perform(patch(baseUrl, serviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Service Parameter with service ID '2' and technical parameter code 'PARAM1' not found\"}"));
    }
}
