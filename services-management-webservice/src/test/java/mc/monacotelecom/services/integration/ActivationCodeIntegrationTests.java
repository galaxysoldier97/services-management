package mc.monacotelecom.services.integration;

import mc.monacotelecom.services.enums.Network;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql({"/sql/clean.sql", "/sql/activation_code_data.sql"})
class ActivationCodeIntegrationTests extends BaseIntegrationTest {

    private static final String ROUTE_ACTIVATION_CODE = "/private/auth/activationcodes";

    @Nested
    @DisplayName("GetAll")
    class GetAll {
        @Test
        void getAll_success() throws Exception {
            mockMvc.perform(get(ROUTE_ACTIVATION_CODE + "?page=1&size=2"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.activationcodes", hasSize(2)))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].activationCodeId").value(4))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].code").value("HOG_HBOX20"))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].description").value("Data Bucket 4 - 750 GB - 20 / 5Mps"))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].nature").value("BUCKET"))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].networkComponent").value("PCRF"))
                    .andExpect(jsonPath("$._embedded.activationcodes[1].activationCodeId").value(5))
                    .andExpect(jsonPath("$._embedded.activationcodes[1].code").value("HOTU_HBOX60"))
                    .andExpect(jsonPath("$._embedded.activationcodes[1].description").value("Top Up 1 - 50 GB - 60 / 5Mps"))
                    .andExpect(jsonPath("$._embedded.activationcodes[1].nature").value("BOOSTER"))
                    .andExpect(jsonPath("$._embedded.activationcodes[1].networkComponent").value("PCRF"))
                    .andExpect(jsonPath("$._links.prev.href").value("http://localhost?page=0&size=2"))
                    .andExpect(jsonPath("$._links.self.href").value("http://localhost"))
                    .andExpect(jsonPath("$._links.next.href").value("http://localhost?page=2&size=2"));
        }
    }

    @Nested
    @DisplayName("GetById")
    class GetById {
        @Test
        void getById_success() throws Exception {
            mockMvc.perform(get(ROUTE_ACTIVATION_CODE + "/1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.activationCodeId").value(1))
                    .andExpect(jsonPath("$.code").value("HPX_126"))
                    .andExpect(jsonPath("$.description").value("BroadBand Mobile "))
                    .andExpect(jsonPath("$.nature").value("PROFILE"))
                    .andExpect(jsonPath("$.networkComponent").value("SPG"));
        }

        @Test
        void getById_missing() throws Exception {
            mockMvc.perform(get(ROUTE_ACTIVATION_CODE + "/99"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().json("{\"status\":404,\"error\":\"Activation code with ID '99' was not found\"}"));
        }
    }

    @Nested
    @DisplayName("Add")
    class Add {
        @Test
        void add_success() throws Exception {
            mockMvc.perform(post(ROUTE_ACTIVATION_CODE)
                            .content("{\"code\": \"XXX\", \"description\": \"description\", \"nature\": \"PROFILE\", \"networkComponent\": \"CS\"}")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.activationCodeId").value(9))
                    .andExpect(jsonPath("$.code").value("XXX"))
                    .andExpect(jsonPath("$.description").value("description"))
                    .andExpect(jsonPath("$.nature").value("PROFILE"))
                    .andExpect(jsonPath("$.networkComponent").value("CS"));
        }

        @Test
        void add_failureBadNature() throws Exception {
            mockMvc.perform(post(ROUTE_ACTIVATION_CODE)
                            .content("{\"code\": \"XXX\", \"description\": \"description\", \"nature\": \"UNKNOWN\", \"networkComponent\": \"CS\"}")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }

        @Test
        void add_failureBadNetworkComponent() throws Exception {
            mockMvc.perform(post(ROUTE_ACTIVATION_CODE)
                            .content("{\"code\": \"XXX\", \"description\": \"description\", \"nature\": \"PROFILE\", \"networkComponent\": \"UNKNOWN\"}")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }

        @Test
        void add_failureNotUnique() throws Exception {
            mockMvc.perform(post(ROUTE_ACTIVATION_CODE)
                            .content("{\"code\": \"HPX_126\", \"description\": \"description\", \"nature\": \"PROFILE\", \"networkComponent\": \"CS\"}")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json("{\"error\":\"Activation code 'HPX_126' already exists\"}"));
        }
    }

    @Nested
    @DisplayName("Update")
    class Update {
        @Test
        void update_success() throws Exception {
            mockMvc.perform(put(ROUTE_ACTIVATION_CODE + "/1")
                            .content("{\"description\": \"BroadBand Mobile 2\", \"nature\": \"OPTION\", \"networkComponent\": \"CS\"}")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.activationCodeId").value(1))
                    .andExpect(jsonPath("$.code").value("HPX_126"))
                    .andExpect(jsonPath("$.description").value("BroadBand Mobile 2"))
                    .andExpect(jsonPath("$.nature").value("OPTION"))
                    .andExpect(jsonPath("$.networkComponent").value("CS"));
        }

        @Test
        void update_missing() throws Exception {
            mockMvc.perform(put(ROUTE_ACTIVATION_CODE + "/99")
                            .content("{\"description\": \"BroadBand Mobile 2\", \"nature\": \"OPTION\", \"networkComponent\": \"CS\"}")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().json("{\"error\":\"Activation code with ID '99' was not found\"}"));
        }

        @Test
        void update_failureForInvalidNature() throws Exception {
            mockMvc.perform(put(ROUTE_ACTIVATION_CODE + "/1")
                            .content("{\"code\": \"HPX_127\", \"description\": \"BroadBand Mobile 2\", \"nature\": \"UNKNOWN\", \"networkComponent\": \"CS\"}")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }

        @Test
        void update_failureForInvalidNetworkComponent() throws Exception {
            mockMvc.perform(put(ROUTE_ACTIVATION_CODE + "/1")
                            .content("{\"code\": \"HPX_127\", \"description\": \"BroadBand Mobile 2\", \"nature\": \"OPTION\", \"networkComponent\": \"UNKNOWN\"}")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("Delete")
    class Delete {
        @Test
        void delete_success() throws Exception {
            mockMvc.perform(delete(ROUTE_ACTIVATION_CODE + "/2"))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        void delete_failureIfUsedByService() throws Exception {
            mockMvc.perform(delete(ROUTE_ACTIVATION_CODE + "/1"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json("{\"status\":400,\"error\":\"Activation code with ID 'HPX_126' cannot be deleted because it is still referenced by services\"}"));
        }

        @Test
        void delete_forceWhenUsedByService() throws Exception {
            mockMvc.perform(delete(ROUTE_ACTIVATION_CODE + "/1?force=true"))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            assertTrue(activationCodeRepository.findByInternalId(1L).isEmpty(), "Activation code should have been removed");
            var service = serviceAccessRepository.findById(1L).orElseThrow(() -> new RuntimeException("Service should not have been removed"));
            assertTrue(serviceActivationRepository.findAllByService(service).stream().noneMatch(x -> "HPX_126".equals(x.getActivCode())), "Related Service Tag should have been removed");
        }

        @Test
        void delete_successIfUsedByTag() throws Exception {
            mockMvc.perform(delete(ROUTE_ACTIVATION_CODE + "/7"))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            assertTrue(activationCodeRepository.findByInternalId(7L).isEmpty(), "Activation code should have been removed");
            var tag = provisioningTagRepository.findByInternalId(146L).orElseThrow(() -> new RuntimeException("Provisioning Tag should not have been removed"));
            assertTrue(tagActivationRepository.findByProvisioningTag(tag).isEmpty(), "Tag Activations should have been removed");
        }

        @Test
        void delete_missing() throws Exception {
            mockMvc.perform(delete(ROUTE_ACTIVATION_CODE + "/99"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().json("{\"status\":404,\"error\":\"Activation code with ID '99' was not found\"}"));
        }
    }

    @Nested
    @DisplayName("GetAllByTechId")
    class GetAllByTechId {
        @Test
        void getAllByTechId_success() throws Exception {
            mockMvc.perform(get(ROUTE_ACTIVATION_CODE + "/techid/212100000000001/" + Network.BBHB.name()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.serviceActivations", hasSize(2)))
                    .andExpect(jsonPath("$._embedded.serviceActivations[0].activCode.activationCodeId").value(1))
                    .andExpect(jsonPath("$._embedded.serviceActivations[0].activCode.code").value("HPX_126"))
                    .andExpect(jsonPath("$._embedded.serviceActivations[0].activCode.description").value("BroadBand Mobile "))
                    .andExpect(jsonPath("$._embedded.serviceActivations[0].activCode.nature").value("PROFILE"))
                    .andExpect(jsonPath("$._embedded.serviceActivations[0].activCode.networkComponent").value("SPG"))
                    .andExpect(jsonPath("$._embedded.serviceActivations[0].activValue").value(IsNull.nullValue()))
                    .andExpect(jsonPath("$._embedded.serviceActivations[1].activCode").value(IsNull.nullValue()))
                    .andExpect(jsonPath("$._embedded.serviceActivations[1].activValue").value(IsNull.nullValue()))
                    .andExpect(jsonPath("$._links.self.href").value("http://localhost"));
        }

        @Test
        void getAllByTechId_missingAccessType() throws Exception {
            mockMvc.perform(get(ROUTE_ACTIVATION_CODE + "/techid/212100000000001/" + Network.FREEDHOME.name()))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().json("{\"status\":404,\"error\":\"Service Access not found for tech ID '212100000000001', access type 'FREEDHOME' and status different than [DEACTIVATED, CANCELED]\"}"));
        }

        @Test
        void getAllByTechId_missingTechId() throws Exception {
            mockMvc.perform(get(ROUTE_ACTIVATION_CODE + "/techid/212100000000002/" + Network.BBHB.name()))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().json("{\"status\":404,\"error\":\"Service Access not found for tech ID '212100000000002', access type 'BBHB' and status different than [DEACTIVATED, CANCELED]\"}"));
        }
    }

    @Nested
    @DisplayName("GetAllByAccessType")
    class GetAllByAccessType {
        @Test
        void getAllByAccessType_success() throws Exception {
            mockMvc.perform(get(ROUTE_ACTIVATION_CODE + "/accessType/" + Network.BBHB.name()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.serviceActivations", hasSize(2)))
                    .andExpect(jsonPath("$._embedded.serviceActivations[0].activCode.activationCodeId").value(1))
                    .andExpect(jsonPath("$._embedded.serviceActivations[0].activCode.code").value("HPX_126"))
                    .andExpect(jsonPath("$._embedded.serviceActivations[0].activCode.description").value("BroadBand Mobile "))
                    .andExpect(jsonPath("$._embedded.serviceActivations[0].activCode.nature").value("PROFILE"))
                    .andExpect(jsonPath("$._embedded.serviceActivations[0].activCode.networkComponent").value("SPG"))
                    .andExpect(jsonPath("$._embedded.serviceActivations[0].activValue").value(IsNull.nullValue()))
                    .andExpect(jsonPath("$._embedded.serviceActivations[1].activCode").value(IsNull.nullValue()))
                    .andExpect(jsonPath("$._embedded.serviceActivations[1].activValue").value(IsNull.nullValue()))
                    .andExpect(jsonPath("$._links.self.href").value("http://localhost"));
        }

        @Test
        void getAllByAccessType_notFound() throws Exception {
            mockMvc.perform(get(ROUTE_ACTIVATION_CODE + "/accessType/" + Network.FREEDHOME.name()))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().json("{\"status\":404,\"error\":\"Service Activation not found\"}"));
        }
    }

    @Nested
    @DisplayName("Search")
    class Search {
        @Test
        void search_byActivCode() throws Exception {
            mockMvc.perform(get(ROUTE_ACTIVATION_CODE + "/search?activCode=GPRSLOCK&size=1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.activationcodes", hasSize(1)))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].activationCodeId").value(6))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].code").value("GPRSLOCK"))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].description").value("Lock GPRS service"))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].nature").value("LOCK"))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].networkComponent").value("SPG"))
                    .andExpect(jsonPath("$._links.self.href").value("http://localhost"));
        }

        @Test
        void search_byNature() throws Exception {
            mockMvc.perform(get(ROUTE_ACTIVATION_CODE + "/search?nature=LOCK&size=1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.activationcodes", hasSize(1)))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].activationCodeId").value(6))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].code").value("GPRSLOCK"))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].description").value("Lock GPRS service"))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].nature").value("LOCK"))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].networkComponent").value("SPG"))
                    .andExpect(jsonPath("$._links.self.href").value("http://localhost"));
        }

        @Test
        void search_byNetworkComponent() throws Exception {
            mockMvc.perform(get(ROUTE_ACTIVATION_CODE + "/search?networkComponent=SPG&size=1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.activationcodes", hasSize(1)))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].activationCodeId").value(1))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].code").value("HPX_126"))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].description").value("BroadBand Mobile "))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].nature").value("PROFILE"))
                    .andExpect(jsonPath("$._embedded.activationcodes[0].networkComponent").value("SPG"))
                    .andExpect(jsonPath("$._links.self.href").value("http://localhost"))
                    .andExpect(jsonPath("$._links.next.href").value("http://localhost?page=1&size=1"));
        }
    }

    @Nested
    @DisplayName("GetOrphans")
    class GetOrphans {
        @Test
        void getOrphans() throws Exception {
            mockMvc.perform(get(ROUTE_ACTIVATION_CODE + "/orphans"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json("[\"MISSING\"]"));
        }
    }
}
