package mc.monacotelecom.services.integration.specifications;

import mc.monacotelecom.services.dto.search.SearchTagActivationCodeDTO;
import mc.monacotelecom.services.entity.TagActivation;
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

@Sql({"/sql/clean.sql", "/sql/tag_activation_code_data.sql"})
class TagActivationSpecificationTest extends BaseIntegrationTest {

    private Page<TagActivation> testSpecification(final SearchTagActivationCodeDTO searchDTO) {
        Specification<TagActivation> specification = tagActivationProcess.prepareSpecification(searchDTO);
        return tagActivationRepository.findAll(specification, PAGEABLE);
    }

    private void assertResult(final Page<TagActivation> result, final int expectedElements, final Long expectedId) {
        Assertions.assertEquals(expectedElements, result.getNumberOfElements());
        Assertions.assertEquals(expectedId, result.getContent().get(0).getTagValue());
    }

    @Test
    void search_tagCodes_success() {
        SearchTagActivationCodeDTO searchTagActivationCodeDTO = new SearchTagActivationCodeDTO();
        searchTagActivationCodeDTO.setTagCodes(List.of("HAXACC", "HPXBRDBAND1"));
        assertResult(testSpecification(searchTagActivationCodeDTO), 2, 367001600L);
    }

    @Test
    void search_tagCodes_empty() {
        SearchTagActivationCodeDTO searchTagActivationCodeDTO = new SearchTagActivationCodeDTO();
        searchTagActivationCodeDTO.setTagCodes(null);
        assertResult(testSpecification(searchTagActivationCodeDTO), 2, 367001600L);
    }

    @Test
    void search_tagCodes_notFound() {
        SearchTagActivationCodeDTO searchTagActivationCodeDTO = new SearchTagActivationCodeDTO();
        searchTagActivationCodeDTO.setTagCodes(List.of("test"));
        Assertions.assertEquals(0, testSpecification(searchTagActivationCodeDTO).getNumberOfElements());
    }

    @Test
    void search_accessType_success() {
        SearchTagActivationCodeDTO searchTagActivationCodeDTO = new SearchTagActivationCodeDTO();
        searchTagActivationCodeDTO.setAccessType(Network.BBHB);
        assertResult(testSpecification(searchTagActivationCodeDTO), 1, 367001600L);
    }

    @Test
    void search_accessType_empty() {
        SearchTagActivationCodeDTO searchTagActivationCodeDTO = new SearchTagActivationCodeDTO();
        searchTagActivationCodeDTO.setAccessType(null);
        assertResult(testSpecification(searchTagActivationCodeDTO), 2, 367001600L);
    }

    @Test
    void search_accessType_notFound() {
        SearchTagActivationCodeDTO searchTagActivationCodeDTO = new SearchTagActivationCodeDTO();
        searchTagActivationCodeDTO.setAccessType(Network.DOCSIS);
        Assertions.assertEquals(0, testSpecification(searchTagActivationCodeDTO).getNumberOfElements());
    }

    @Test
    void search_activity_success() {
        SearchTagActivationCodeDTO searchTagActivationCodeDTO = new SearchTagActivationCodeDTO();
        searchTagActivationCodeDTO.setActivity(ProvisioningTagActivity.INTERNET);
        assertResult(testSpecification(searchTagActivationCodeDTO), 1, 367001601L);
    }

    @Test
    void search_activity_empty() {
        SearchTagActivationCodeDTO searchTagActivationCodeDTO = new SearchTagActivationCodeDTO();
        searchTagActivationCodeDTO.setActivity(null);
        assertResult(testSpecification(searchTagActivationCodeDTO), 2, 367001600L);
    }

    @Test
    void search_activity_notFound() {
        SearchTagActivationCodeDTO searchTagActivationCodeDTO = new SearchTagActivationCodeDTO();
        searchTagActivationCodeDTO.setActivity(ProvisioningTagActivity.TELEPHONY);
        Assertions.assertEquals(0, testSpecification(searchTagActivationCodeDTO).getNumberOfElements());
    }

    @Test
    void search_nature_success() {
        SearchTagActivationCodeDTO searchTagActivationCodeDTO = new SearchTagActivationCodeDTO();
        searchTagActivationCodeDTO.setNature(ProvisioningTagNature.P);
        assertResult(testSpecification(searchTagActivationCodeDTO), 1, 367001601L);
    }

    @Test
    void search_nature_empty() {
        SearchTagActivationCodeDTO searchTagActivationCodeDTO = new SearchTagActivationCodeDTO();
        searchTagActivationCodeDTO.setNature(null);
        assertResult(testSpecification(searchTagActivationCodeDTO), 2, 367001600L);
    }

    @Test
    void search_nature_notFound() {
        SearchTagActivationCodeDTO searchTagActivationCodeDTO = new SearchTagActivationCodeDTO();
        searchTagActivationCodeDTO.setNature(ProvisioningTagNature.O);
        Assertions.assertEquals(0, testSpecification(searchTagActivationCodeDTO).getNumberOfElements());
    }
}
