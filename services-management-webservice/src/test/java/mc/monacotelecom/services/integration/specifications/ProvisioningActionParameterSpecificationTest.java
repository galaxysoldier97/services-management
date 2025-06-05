package mc.monacotelecom.services.integration.specifications;

import mc.monacotelecom.services.dto.search.SearchProvisioningActionDTO;
import mc.monacotelecom.services.entity.ProvisioningActionParameter;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ProvisioningTagActivity;
import mc.monacotelecom.services.enums.ProvisioningTagNature;
import mc.monacotelecom.services.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@Sql({"/sql/clean.sql", "/sql/provisioning_action_parameter_data.sql"})
class ProvisioningActionParameterSpecificationTest extends BaseIntegrationTest {

    private Page<ProvisioningActionParameter> testSpecification(final SearchProvisioningActionDTO searchDTO){
        Specification<ProvisioningActionParameter> specification = provisioningActionParameterProcess.prepareSpecification(searchDTO);
        return provisioningActionParameterRepository.findAll(specification, PAGEABLE);
    }

    private void assertResult(final Page<ProvisioningActionParameter> result, final  int expectedElements, final String valueExpected){
        Assertions.assertEquals(expectedElements, result.getNumberOfElements());
        Assertions.assertEquals(valueExpected, result.getContent().get(0).getParameterValue());
    }

    @Test
    void search_tagCodes_success(){
        SearchProvisioningActionDTO searchProvisioningActionDTO = new SearchProvisioningActionDTO();
        searchProvisioningActionDTO.setTagCodes(List.of("HAXACC","HECARD"));
        assertResult(testSpecification(searchProvisioningActionDTO), 2, "TEST");
    }

    @Test
    void search_tagCodes_empty(){
        SearchProvisioningActionDTO searchProvisioningActionDTO = new SearchProvisioningActionDTO();
        searchProvisioningActionDTO.setTagCodes(null);
        assertResult(testSpecification(searchProvisioningActionDTO), 2, "TEST");
    }

    @Test
    void search_tagCodes_notPresent(){
        SearchProvisioningActionDTO searchProvisioningActionDTO = new SearchProvisioningActionDTO();
        searchProvisioningActionDTO.setTagCodes(List.of("test"));
        Assertions.assertEquals(0, testSpecification(searchProvisioningActionDTO).getNumberOfElements());
    }

    @Test
    void search_accessType_success(){
        SearchProvisioningActionDTO searchProvisioningActionDTO = new SearchProvisioningActionDTO();
        searchProvisioningActionDTO.setAccessType(Network.BBHB);
        assertResult(testSpecification(searchProvisioningActionDTO), 1, "TEST");
    }

    @Test
    void search_accessType_empty(){
        SearchProvisioningActionDTO searchProvisioningActionDTO = new SearchProvisioningActionDTO();
        searchProvisioningActionDTO.setAccessType(null);
        assertResult(testSpecification(searchProvisioningActionDTO), 2, "TEST");
    }

    @Test
    void search_accessType_notPresent(){
        SearchProvisioningActionDTO searchProvisioningActionDTO = new SearchProvisioningActionDTO();
        searchProvisioningActionDTO.setAccessType(Network.DOCSIS);
        Assertions.assertEquals(0, testSpecification(searchProvisioningActionDTO).getNumberOfElements());
    }

    @Test
    void search_nature_success(){
        SearchProvisioningActionDTO searchProvisioningActionDTO = new SearchProvisioningActionDTO();
        searchProvisioningActionDTO.setNature(ProvisioningTagNature.A);
        assertResult(testSpecification(searchProvisioningActionDTO), 1, "TEST");
    }

    @Test
    void search_nature_empty(){
        SearchProvisioningActionDTO searchProvisioningActionDTO = new SearchProvisioningActionDTO();
        searchProvisioningActionDTO.setNature(null);
        assertResult(testSpecification(searchProvisioningActionDTO), 2, "TEST");
    }

    @Test
    void search_nature_notPresent(){
        SearchProvisioningActionDTO searchProvisioningActionDTO = new SearchProvisioningActionDTO();
        searchProvisioningActionDTO.setNature(ProvisioningTagNature.P);
        Assertions.assertEquals(0, testSpecification(searchProvisioningActionDTO).getNumberOfElements());
    }

    @Test
    void search_activity_success(){
        SearchProvisioningActionDTO searchProvisioningActionDTO = new SearchProvisioningActionDTO();
        searchProvisioningActionDTO.setActivity(ProvisioningTagActivity.MOBILE);
        assertResult(testSpecification(searchProvisioningActionDTO), 1, "TEST");
    }

    @Test
    void search_activity_empty(){
        SearchProvisioningActionDTO searchProvisioningActionDTO = new SearchProvisioningActionDTO();
        searchProvisioningActionDTO.setActivity(null);
        assertResult(testSpecification(searchProvisioningActionDTO), 2, "TEST");
    }

    @Test
    void search_activity_notPresent(){
        SearchProvisioningActionDTO searchProvisioningActionDTO = new SearchProvisioningActionDTO();
        searchProvisioningActionDTO.setActivity(ProvisioningTagActivity.INTERNET);
        Assertions.assertEquals(0, testSpecification(searchProvisioningActionDTO).getNumberOfElements());
    }
}
