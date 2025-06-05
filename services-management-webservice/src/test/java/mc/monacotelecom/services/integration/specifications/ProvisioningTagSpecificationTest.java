package mc.monacotelecom.services.integration.specifications;

import mc.monacotelecom.services.dto.search.SearchProvisioningTagDTO;
import mc.monacotelecom.services.entity.ProvisioningTag;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ProvisioningTagActivity;
import mc.monacotelecom.services.enums.ProvisioningTagNature;
import mc.monacotelecom.services.enums.ServiceCategory;
import mc.monacotelecom.services.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;

@Sql({"/sql/clean.sql", "/sql/provisioning_tag_data.sql"})
class ProvisioningTagSpecificationTest extends BaseIntegrationTest {

    private Page<ProvisioningTag> testSpecification(final SearchProvisioningTagDTO searchDTO){
        Specification<ProvisioningTag> specification = provisioningTagProcess.prepareSpecification(searchDTO);
        return provisioningTagRepository.findAll(specification, PAGEABLE);
    }

    private void assertQuantity(final Page<ProvisioningTag> result, final  int expectedElements){
        Assertions.assertEquals(expectedElements, result.getNumberOfElements());
    }

    private void assertResultId(final Page<ProvisioningTag> result, final Long expectedId){
        Assertions.assertEquals(1, result.getNumberOfElements());
        Assertions.assertEquals(expectedId, result.getContent().get(0).getInternalId());
    }

    @Test
    void search_tagCode_success() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setTagCode("HAXACC");
        assertResultId(testSpecification(searchProvisioningTagDTO), 1L);
    }

    @Test
    void search_tagCode_empty() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setTagCode("");
        assertQuantity(testSpecification(searchProvisioningTagDTO), 6);
    }

    @Test
    void search_tagCode_notFound() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setTagCode("test");
        Assertions.assertEquals(0, testSpecification(searchProvisioningTagDTO).getNumberOfElements());
    }

    @Test
    void search_accessType_success() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setAccessType(Network.BBHB);
        assertQuantity(testSpecification(searchProvisioningTagDTO), 4);
    }

    @Test
    void search_accessType_empty() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setAccessType(null);
        assertQuantity(testSpecification(searchProvisioningTagDTO), 6);
    }

    @Test
    void search_accessType_notFound() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setAccessType(Network.DOCSIS);
        Assertions.assertEquals(0, testSpecification(searchProvisioningTagDTO).getNumberOfElements());
    }

    @Test
    void search_activity_success() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setActivity(ProvisioningTagActivity.MOBILE);
        assertQuantity(testSpecification(searchProvisioningTagDTO), 5);
    }

    @Test
    void search_activity_empty() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setActivity(null);
        assertQuantity(testSpecification(searchProvisioningTagDTO), 6);
    }

    @Test
    void search_activity_notFound() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setActivity(ProvisioningTagActivity.TELEPHONY);
        Assertions.assertEquals(0, testSpecification(searchProvisioningTagDTO).getNumberOfElements());
    }

    @Test
    void search_nature_success() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setNature(ProvisioningTagNature.A);
        assertResultId(testSpecification(searchProvisioningTagDTO), 1L);
    }

    @Test
    void search_nature_empty() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setNature(null);
        assertQuantity(testSpecification(searchProvisioningTagDTO), 6);
    }

    @Test
    void search_nature_notFound() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setNature(ProvisioningTagNature.C);
        Assertions.assertEquals(0, testSpecification(searchProvisioningTagDTO).getNumberOfElements());
    }

    @Test
    void search_componentType_success() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setComponentType("CAS");
        assertResultId(testSpecification(searchProvisioningTagDTO), 5L);
    }

    @Test
    void search_componentType_empty() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setComponentType(null);
        assertQuantity(testSpecification(searchProvisioningTagDTO), 6);
    }

    @Test
    void search_componentType_notFound() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setComponentType("NOT_FOUND");
        Assertions.assertEquals(0, testSpecification(searchProvisioningTagDTO).getNumberOfElements());
    }

    @Test
    void search_category_component() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setCategory(ServiceCategory.COMPONENT);
        assertResultId(testSpecification(searchProvisioningTagDTO), 4L);
    }

    @Test
    void search_category_access() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setCategory(ServiceCategory.ACCESS);
        assertQuantity(testSpecification(searchProvisioningTagDTO), 5);
    }

    @Test
    void search_category_empty() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setCategory(null);
        assertQuantity(testSpecification(searchProvisioningTagDTO), 6);
    }

    @Test
    void search_persistent_true() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setPersistent(Boolean.TRUE);
        assertQuantity(testSpecification(searchProvisioningTagDTO), 4);
    }

    @Test
    void search_persistent_false() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setPersistent(Boolean.FALSE);
        assertQuantity(testSpecification(searchProvisioningTagDTO), 2);
    }

    @Test
    void search_persistent_empty() {
        SearchProvisioningTagDTO searchProvisioningTagDTO = new SearchProvisioningTagDTO();
        searchProvisioningTagDTO.setPersistent(null);
        assertQuantity(testSpecification(searchProvisioningTagDTO), 6);
    }
}
