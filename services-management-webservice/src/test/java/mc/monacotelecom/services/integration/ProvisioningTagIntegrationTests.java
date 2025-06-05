package mc.monacotelecom.services.integration;

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

@Sql({"/sql/clean.sql", "/sql/provisioning_tag_data.sql"})
class ProvisioningTagIntegrationTests extends BaseIntegrationTest {

    private static final String ROUTE_PROVISIONING_TAGS = "/private/auth/provisioningtags";

    @Nested
    @DisplayName("GetAll")
    class GetAll {
        @Test
        void getAll_success() throws Exception {
            mockMvc.perform(get(ROUTE_PROVISIONING_TAGS + "?page=1&size=2"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.provisioningtags", hasSize(2)))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].tagId").value(3))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].tagCode").value("HECARD"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].description").value("Sim Card"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].activity").value("MOBILE"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].componentType").value("TEAMS"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].category").value("ACCESS"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].accessType").value("BBHB"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].nature").value("E"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].persistent").value(true))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].tagParameters").value(IsNull.nullValue()))
                    .andExpect(jsonPath("$._embedded.provisioningtags[1].tagId").value(4))
                    .andExpect(jsonPath("$._embedded.provisioningtags[1].tagCode").value("HODATA1"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[1].description").value("Data Option 1"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[1].activity").value("INTERNET"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[1].componentType").value("TEAMS"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[1].category").value("COMPONENT"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[1].accessType").value("BBHB"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[1].nature").value("O"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[1].persistent").value(true))
                    .andExpect(jsonPath("$._embedded.provisioningtags[1].tagParameters").value(IsNull.nullValue()))
                    .andExpect(jsonPath("$._links.prev.href").value("http://localhost/provisioningtags?page=0&size=2"))
                    .andExpect(jsonPath("$._links.self.href").value("http://localhost/provisioningtags"))
                    .andExpect(jsonPath("$._links.next.href").value("http://localhost/provisioningtags?page=2&size=2"));
        }
    }

    @Nested
    @DisplayName("GetById")
    class GetById {
        @Test
        void getById_success() throws Exception {
            mockMvc.perform(get(ROUTE_PROVISIONING_TAGS + "/1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tagId").value(1))
                    .andExpect(jsonPath("$.tagCode").value("HAXACC"))
                    .andExpect(jsonPath("$.description").value("Mobile Access "))
                    .andExpect(jsonPath("$.activity").value("MOBILE"))
                    .andExpect(jsonPath("$.componentType").value("TEAMS"))
                    .andExpect(jsonPath("$.category").value("ACCESS"))
                    .andExpect(jsonPath("$.accessType").value("BBHB"))
                    .andExpect(jsonPath("$.nature").value("A"))
                    .andExpect(jsonPath("$.persistent").value(true))
                    .andExpect(jsonPath("$.tagParameters").value(IsNull.nullValue()));
        }

        @Test
        void getById_missing() throws Exception {
            mockMvc.perform(get(ROUTE_PROVISIONING_TAGS + "/99"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().json("{\"status\":404,\"error\":\"Provisioning tag with ID '99' was not found\"}"));
        }
    }

    @Nested
    @DisplayName("Add")
    class Add {
        @Test
        void add_success() throws Exception {
            mockMvc.perform(post(ROUTE_PROVISIONING_TAGS)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{\"tagCode\": \"HUPORTIN\", \"description\":\"Portability In\", \"activity\": \"MOBILE\", \"category\":\"ACCESS\", \"accessType\": \"MOBILE\", \"nature\": \"U\", \"componentType\" : \"TEAMS\", \"persistent\": true, \"tagParameters\": []}"))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.tagId").value(7))
                    .andExpect(jsonPath("$.tagCode").value("HUPORTIN"))
                    .andExpect(jsonPath("$.description").value("Portability In"))
                    .andExpect(jsonPath("$.activity").value("MOBILE"))
                    .andExpect(jsonPath("$.category").value("ACCESS"))
                    .andExpect(jsonPath("$.accessType").value("MOBILE"))
                    .andExpect(jsonPath("$.nature").value("U"))
                    .andExpect(jsonPath("$.componentType").value("TEAMS"))
                    .andExpect(jsonPath("$.persistent").value(true))
                    .andExpect(jsonPath("$.tagParameters").value(IsNull.nullValue()));
        }

        @Test
        void add_failureForExistingCode() throws Exception {
            mockMvc.perform(post(ROUTE_PROVISIONING_TAGS)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{\"tagCode\": \"HAXACC\", \"description\":\"Portability In\", \"activity\": \"MOBILE\", \"category\":\"ACCESS\", \"accessType\": \"MOBILE\", \"nature\": \"U\", \"persistent\": true, \"tagParameters\": []}"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json("{\"error\":\"Provisioning tag with code 'HAXACC' already exists\"}"));
        }

        @Test
        void add_failureForBadActivity() throws Exception {
            mockMvc.perform(post(ROUTE_PROVISIONING_TAGS)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{\"tagCode\": \"HUPORTIN\", \"description\":\"Portability In\", \"activity\": \"UNKNOWN\", \"category\":\"ACCESS\", \"accessType\": \"MOBILE\", \"nature\": \"U\", \"persistent\": true, \"tagParameters\": []}"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }

        @Test
        void add_failureForBadCategory() throws Exception {
            mockMvc.perform(post(ROUTE_PROVISIONING_TAGS)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{\"tagCode\": \"HUPORTIN\", \"description\":\"Portability In\", \"activity\": \"MOBILE\", \"category\":\"UNKNOWN\", \"accessType\": \"MOBILE\", \"nature\": \"U\", \"persistent\": true, \"tagParameters\": []}"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }

        @Test
        void add_failureForBadAccessType() throws Exception {
            mockMvc.perform(post(ROUTE_PROVISIONING_TAGS)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{\"tagCode\": \"HUPORTIN\", \"description\":\"Portability In\", \"activity\": \"MOBILE\", \"category\":\"ACCESS\", \"accessType\": \"UNKNOWN\", \"nature\": \"Z\", \"persistent\": true, \"tagParameters\": []}"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }

        @Test
        void add_failureForBadNature() throws Exception {
            mockMvc.perform(post(ROUTE_PROVISIONING_TAGS)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{\"tagCode\": \"HUPORTIN\", \"description\":\"Portability In\", \"activity\": \"MOBILE\", \"category\":\"ACCESS\", \"accessType\": \"MOBILE\", \"nature\": \"Z\", \"persistent\": true, \"tagParameters\": []}"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("Update")
    class Update {
        @Test
        void update_success() throws Exception {
            mockMvc.perform(put(ROUTE_PROVISIONING_TAGS + "/1")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{\"description\":\"Mobile Access 2\",\"activity\":\"INTERNET\", \"componentType\" : \"CAS\",\"category\":\"COMPONENT\",\"accessType\":\"MOBILE\",\"nature\":\"U\",\"persistent\":false}"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\"tagId\": 1, \"tagCode\": \"HAXACC\", \"description\":\"Mobile Access 2\", \"activity\": \"INTERNET\",\"componentType\" : \"CAS\",\"category\":\"COMPONENT\", \"accessType\": \"MOBILE\", \"nature\": \"U\", \"persistent\": false, \"tagParameters\": null}"));
        }

        @Test
        void update_failureForBadActivity() throws Exception {
            mockMvc.perform(put(ROUTE_PROVISIONING_TAGS + "/1")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{\"description\":\"Mobile Access 2\",\"activity\":\"UNKNOWN\",\"category\":\"COMPONENT\",\"accessType\":\"MOBILE\",\"nature\":\"U\",\"persistent\":false}"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }

        @Test
        void update_failureForBadCategory() throws Exception {
            mockMvc.perform(put(ROUTE_PROVISIONING_TAGS + "/1")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{\"description\":\"Mobile Access 2\",\"activity\":\"INTERNET\",\"category\":\"UNKNOWN\",\"accessType\":\"MOBILE\",\"nature\":\"U\",\"persistent\":false}"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }

        @Test
        void update_failureForBadAccessType() throws Exception {
            mockMvc.perform(put(ROUTE_PROVISIONING_TAGS + "/1")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{\"description\":\"Mobile Access 2\",\"activity\":\"INTERNET\",\"category\":\"COMPONENT\",\"accessType\":\"UNKNOWN\",\"nature\":\"U\",\"persistent\":false}"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }

        @Test
        void update_failureForBadNature() throws Exception {
            mockMvc.perform(put(ROUTE_PROVISIONING_TAGS + "/1")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{\"description\":\"Mobile Access 2\",\"activity\":\"INTERNET\",\"category\":\"COMPONENT\",\"accessType\":\"MOBILE\",\"nature\":\"Z\",\"persistent\":false}"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("Delete")
    class Delete {
        @Test
        void delete_success() throws Exception {
            mockMvc.perform(delete(ROUTE_PROVISIONING_TAGS + "/1"))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        void delete_notFound() throws Exception {
            mockMvc.perform(delete(ROUTE_PROVISIONING_TAGS + "/99"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().json("{\"status\":404,\"error\":\"Provisioning tag with ID '99' was not found\"}"));
        }

        @Test
        void delete_failureIfHasChild() throws Exception {
            mockMvc.perform(delete(ROUTE_PROVISIONING_TAGS + "/4"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json("{\"status\":400,\"error\":\"Provisioning tag with code 'HODATA1' cannot be deleted because it still has tag 'HUCCHGE' as child\"}"));
        }

        @Test
        void delete_successIfChild() throws Exception {
            mockMvc.perform(delete(ROUTE_PROVISIONING_TAGS + "/6"))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            assertTrue(provisioningTagRepository.findById("HPXBRDBAND1").isPresent(), "Parent Prov Tag should not has been removed");
            assertTrue(provisioningTagRepository.findById("HUCCHGE2").isEmpty(), "Child Prov Tag has not been removed");
        }

        @Test
        void delete_forceWhenHasChild() throws Exception {
            mockMvc.perform(delete(ROUTE_PROVISIONING_TAGS + "/4?force=true"))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            assertTrue(provisioningTagRepository.findById("HODATA1").isEmpty(), "Parent Prov Tag has not been removed");
            assertTrue(provisioningTagRepository.findById("HUCCHGE").isEmpty(), "Child Prov Tag has not been removed");
        }

        @Test
        void delete_failureIfHasActivationCodes() throws Exception {
            mockMvc.perform(delete(ROUTE_PROVISIONING_TAGS + "/3"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json("{\"status\":400,\"error\":\"Provisioning tag with code 'HECARD' cannot be deleted because it is still linked to activation codes\"}"));
        }

        @Test
        void delete_forceWhenHasActivationCodes() throws Exception {
            mockMvc.perform(delete(ROUTE_PROVISIONING_TAGS + "/3?force=true"))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            assertTrue(provisioningTagRepository.findById("HECARD").isEmpty(), "Prov Tag has not been removed");
            var activationCode = activationCodeRepository.findById("HPX_126")
                    .orElseThrow(() -> new RuntimeException("Activation code HPX_126 should still exist"));
            assertTrue(activationCode.getTagActivations().isEmpty(), "Tag activations haven't been removed");
        }

        @Test
        void delete_failureIfHasService() throws Exception {
            mockMvc.perform(delete(ROUTE_PROVISIONING_TAGS + "/5"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json("{\"status\":400,\"error\":\"Provisioning tag with code 'HUCCHGE' cannot be deleted because it is still linked to services\"}"));
        }

        @Test
        void delete_forceWhenHasService() throws Exception {
            mockMvc.perform(delete(ROUTE_PROVISIONING_TAGS + "/5?force=true"))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            assertTrue(provisioningTagRepository.findById("HUCCHGE").isEmpty(), "Prov Tag has not been removed");
            var serviceAccess = serviceAccessRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Service Access 1 should still exist"));
            assertTrue(serviceTagRepository.findAllByService(serviceAccess).stream().noneMatch(x -> "HUCCHGE".equals(x.getTagCode())), "Related Service Tag should have been removed");
        }
    }

    @Nested
    @DisplayName("Search")
    class Search {
        @Test
        void search_byActivity() throws Exception {
            mockMvc.perform(get(ROUTE_PROVISIONING_TAGS + "/search?activity=INTERNET&size=1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.provisioningtags", hasSize(1)))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].tagId").value(4))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].tagCode").value("HODATA1"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].description").value("Data Option 1"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].activity").value("INTERNET"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].componentType").value("TEAMS"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].category").value("COMPONENT"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].accessType").value("BBHB"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].nature").value("O"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].persistent").value(true))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].tagParameters").value(IsNull.nullValue()))
                    .andExpect(jsonPath("$._links.self.href").value("http://localhost/provisioningtags"));
        }

        @Test
        void search_byTagCode() throws Exception {
            mockMvc.perform(get(ROUTE_PROVISIONING_TAGS + "/search?tagCode=HODATA1&size=1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.provisioningtags", hasSize(1)))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].tagId").value(4))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].tagCode").value("HODATA1"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].description").value("Data Option 1"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].activity").value("INTERNET"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].componentType").value("TEAMS"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].category").value("COMPONENT"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].accessType").value("BBHB"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].nature").value("O"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].persistent").value(true))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].tagParameters").value(IsNull.nullValue()))
                    .andExpect(jsonPath("$._links.self.href").value("http://localhost/provisioningtags"));
        }

        @Test
        void search_byAccessType() throws Exception {
            mockMvc.perform(get(ROUTE_PROVISIONING_TAGS + "/search?accessType=MOBILE&size=1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.provisioningtags", hasSize(1)))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].tagId").value(5))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].tagCode").value("HUCCHGE"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].description").value("Change SIM Card"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].activity").value("MOBILE"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].componentType").value("CAS"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].category").value("ACCESS"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].accessType").value("MOBILE"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].nature").value("U"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].persistent").value(false))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].tagParameters").value(IsNull.nullValue()))
                    .andExpect(jsonPath("$._links.self.href").value("http://localhost/provisioningtags"));
        }

        @Test
        void search_byNature() throws Exception {
            mockMvc.perform(get(ROUTE_PROVISIONING_TAGS + "/search?nature=O&size=1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.provisioningtags", hasSize(1)))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].tagId").value(4))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].tagCode").value("HODATA1"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].description").value("Data Option 1"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].activity").value("INTERNET"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].componentType").value("TEAMS"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].category").value("COMPONENT"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].accessType").value("BBHB"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].nature").value("O"))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].persistent").value(true))
                    .andExpect(jsonPath("$._embedded.provisioningtags[0].tagParameters").value(IsNull.nullValue()))
                    .andExpect(jsonPath("$._links.self.href").value("http://localhost/provisioningtags"));
        }
    }

    @Nested
    @DisplayName("GetAllParametersOnAction")
    class GetAllParametersOnAction {
        @Test
        void getAllParametersOnAction_success() throws Exception {
            mockMvc.perform(post(ROUTE_PROVISIONING_TAGS + "/parametersOnActions")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{ \"parametersOnActionDTO\" : [{\"tagCode\": \"HAXACC\", \"tagAction\": \"PURCHASE\"}]}"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.provisioningActionParameterDTO", hasSize(1)))
                    .andExpect(jsonPath("$.provisioningActionParameterDTO[0].technicalParameter.parameterId").value(1))
                    .andExpect(jsonPath("$.provisioningActionParameterDTO[0].technicalParameter.parameterCode").value("PARAM1"))
                    .andExpect(jsonPath("$.provisioningActionParameterDTO[0].technicalParameter.description").value("Technical Parameter 1"))
                    .andExpect(jsonPath("$.provisioningActionParameterDTO[0].technicalParameter.parameterType").value("STATIC"))
                    .andExpect(jsonPath("$.provisioningActionParameterDTO[0].technicalParameter.links", hasSize(0)))
                    .andExpect(jsonPath("$.provisioningActionParameterDTO[0].parameterValue").value("TEST"))
                    .andExpect(jsonPath("$.provisioningActionParameterDTO[0].links", hasSize(0)));
        }

        @Test
        void getAllParametersOnAction_missingProvisioningAction() throws Exception {
            mockMvc.perform(post(ROUTE_PROVISIONING_TAGS + "/parametersOnActions")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{ \"parametersOnActionDTO\" : [{\"tagCode\": \"HPXBRDBAND1\", \"tagAction\": \"PURCHASE\"}]}"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json("{\"status\":400,\"error\":\"Expected a Provisioning Action at this point\"}"));
        }

        @Test
        void getAllParametersOnAction_noProvisioningActionParameters() throws Exception {
            mockMvc.perform(post(ROUTE_PROVISIONING_TAGS + "/parametersOnActions")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{ \"parametersOnActionDTO\" : [{\"tagCode\": \"HECARD\", \"tagAction\": \"PURCHASE\"}]}"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\"provisioningActionParameterDTO\":[]}"));
        }

        @Test
        void getAllParametersOnAction_empty() throws Exception {
            mockMvc.perform(post(ROUTE_PROVISIONING_TAGS + "/parametersOnActions")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{ \"parametersOnActionDTO\" : []}"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json("{\"status\":400,\"error\":\"The list of TagAction/TagCode cannot be empty\"}"));
        }
    }

    @Nested
    @DisplayName("Decompose")
    class Decompose {
        @Test
        void decomposeProductsRequest_success() throws Exception {
            mockMvc.perform(patch(ROUTE_PROVISIONING_TAGS + "/decomposeproductsrequest")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{ \"provisioningProductRequests\" : [{\"productCode\":\"PAY_AS_YOU_GO_1\", \"productAction\": \"ACTIVATION\"}]}"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].provisioningProductId").value(1))
                    .andExpect(jsonPath("$[0].productCode").value("PAY_AS_YOU_GO_1"))
                    .andExpect(jsonPath("$[0].productAction").value("ACTIVATION"))
                    .andExpect(jsonPath("$[0].provAction.tagActionId").value(1))
                    .andExpect(jsonPath("$[0].provAction.provisioningTag.tagId").value(1))
                    .andExpect(jsonPath("$[0].provAction.provisioningTag.tagCode").value("HAXACC"))
                    .andExpect(jsonPath("$[0].provAction.provisioningTag.description").value("Mobile Access "))
                    .andExpect(jsonPath("$[0].provAction.provisioningTag.activity").value("MOBILE"))
                    .andExpect(jsonPath("$[0].provAction.provisioningTag.componentType").value("TEAMS"))
                    .andExpect(jsonPath("$[0].provAction.provisioningTag.category").value("ACCESS"))
                    .andExpect(jsonPath("$[0].provAction.provisioningTag.accessType").value("BBHB"))
                    .andExpect(jsonPath("$[0].provAction.provisioningTag.nature").value("A"))
                    .andExpect(jsonPath("$[0].provAction.provisioningTag.persistent").value(true))
                    .andExpect(jsonPath("$[0].provAction.provisioningTag.tagParameters").value(IsNull.nullValue()))
                    .andExpect(jsonPath("$[0].provAction.provisioningTag.links", hasSize(0)))
                    .andExpect(jsonPath("$[0].provAction.tagAction").value("PURCHASE"))
                    .andExpect(jsonPath("$[0].provAction.provActionParameters", hasSize(1)))
                    .andExpect(jsonPath("$[0].provAction.provActionParameters[0].technicalParameter.parameterId").value(1))
                    .andExpect(jsonPath("$[0].provAction.provActionParameters[0].technicalParameter.parameterCode").value("PARAM1"))
                    .andExpect(jsonPath("$[0].provAction.provActionParameters[0].technicalParameter.description").value("Technical Parameter 1"))
                    .andExpect(jsonPath("$[0].provAction.provActionParameters[0].technicalParameter.parameterType").value("STATIC"))
                    .andExpect(jsonPath("$[0].provAction.provActionParameters[0].technicalParameter.links", hasSize(0)))
                    .andExpect(jsonPath("$[0].provAction.provActionParameters[0].parameterValue").value("TEST"))
                    .andExpect(jsonPath("$[0].provAction.provActionParameters[0].links", hasSize(0)))
                    .andExpect(jsonPath("$[0].provAction.serviceAction").value(IsNull.nullValue()))
                    .andExpect(jsonPath("$[0].provAction.links", hasSize(0)))
                    .andExpect(jsonPath("$[0].links", hasSize(0)));
        }

        @Test
        void decomposeProductsRequest_noRequest() throws Exception {
            mockMvc.perform(patch(ROUTE_PROVISIONING_TAGS + "/decomposeproductsrequest")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{ \"provisioningProductRequests\" : []}"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().json("{\"status\":404,\"error\":\"No Provisioning product were found for the following provisioningCode - provisioningAction: ''\",\"extraData\":null}"));
        }

        @Test
        void decomposeProductsRequest_missingProduct() throws Exception {
            mockMvc.perform(patch(ROUTE_PROVISIONING_TAGS + "/decomposeproductsrequest")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{ \"provisioningProductRequests\" : [{\"productCode\":\"PAY_AS_YOU_GO_99\", \"productAction\": \"ACTIVATION\"}]}"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().json("{\"status\":404,\"error\":\"No Provisioning product were found for the following provisioningCode - provisioningAction: 'PAY_AS_YOU_GO_99 - ACTIVATION'\",\"extraData\":null}"));
        }

        @Test
        void decomposeProductsRequest_missingAction() throws Exception {
            mockMvc.perform(patch(ROUTE_PROVISIONING_TAGS + "/decomposeproductsrequest")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{ \"provisioningProductRequests\" : [{\"productCode\":\"PAY_AS_YOU_GO_1\", \"productAction\": \"UNKNOWN\"}]}"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }

        @Test
        void decomposeProductsRequest_successSimultaneousActivateDeactivate() throws Exception {
            mockMvc.perform(patch(ROUTE_PROVISIONING_TAGS + "/decomposeproductsrequest")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content("{ \"provisioningProductRequests\" : [{\"productCode\":\"PAY_AS_YOU_GO_1\", \"productAction\": \"ACTIVATION\"}, {\"productCode\":\"PAY_AS_YOU_GO_1\", \"productAction\": \"DEACTIVATION\"}]}"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json("[]"));
        }
    }

    @Nested
    @DisplayName("GetOrphans")
    class GetOrphans {
        @Test
        void getOrphans() throws Exception {
            mockMvc.perform(get(ROUTE_PROVISIONING_TAGS + "/orphans"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json("[\"HUCCHGEMISSING\"]"));
        }
    }
}
