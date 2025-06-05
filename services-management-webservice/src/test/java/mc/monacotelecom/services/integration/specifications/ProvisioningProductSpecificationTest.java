package mc.monacotelecom.services.integration.specifications;

import mc.monacotelecom.services.dto.search.SearchProvisioningProductDTO;
import mc.monacotelecom.services.entity.ProvisioningProduct;
import mc.monacotelecom.services.enums.ProvisioningTagAction;
import mc.monacotelecom.services.enums.ServiceAction;
import mc.monacotelecom.services.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;

import static mc.monacotelecom.services.enums.ProvisioningProductActionRequest.CHGE_ADDR;
import static mc.monacotelecom.services.enums.ProvisioningProductActionRequest.DEACTIVATION;

@Sql({"/sql/clean.sql", "/sql/provisioning_product_data.sql"})
class ProvisioningProductSpecificationTest extends BaseIntegrationTest {

    private Page<ProvisioningProduct> testSpecification(final SearchProvisioningProductDTO dto) {
        Specification<ProvisioningProduct> specification = provisioningProductProcess.prepareSpecification(dto);
        return provisioningProductRepository.findAll(specification, PAGEABLE);
    }

    private void assertResult(final Page<ProvisioningProduct> result, final int expectedElements) {
        Assertions.assertEquals(expectedElements, result.getNumberOfElements());
    }

    private void assertResult(final Page<ProvisioningProduct> result, final Long expectedId) {
        assertResult(result, 1);
        Assertions.assertEquals(expectedId, result.getContent().get(0).getInternalId());
    }

    @Test
    void search_code_success() {
        SearchProvisioningProductDTO dto = new SearchProvisioningProductDTO();
        dto.setCode("PAY_AS_YOU_GO_2");
        assertResult(testSpecification(dto), 3L);
    }

    @Test
    void search_code_notPresent() {
        SearchProvisioningProductDTO dto = new SearchProvisioningProductDTO();
        dto.setCode("PAY_AS_YOU_GO_99");
        Assertions.assertEquals(0, testSpecification(dto).getNumberOfElements());
    }

    @Test
    void search_code_empty() {
        SearchProvisioningProductDTO dto = new SearchProvisioningProductDTO();
        dto.setCode(null);
        assertResult(testSpecification(dto), 3);
    }

    @Test
    void search_action_success() {
        SearchProvisioningProductDTO dto = new SearchProvisioningProductDTO();
        dto.setAction(DEACTIVATION);
        assertResult(testSpecification(dto), 2L);
    }

    @Test
    void search_action_notPresent() {
        SearchProvisioningProductDTO dto = new SearchProvisioningProductDTO();
        dto.setAction(CHGE_ADDR);
        Assertions.assertEquals(0, testSpecification(dto).getNumberOfElements());
    }

    @Test
    void search_action_empty() {
        SearchProvisioningProductDTO dto = new SearchProvisioningProductDTO();
        dto.setAction(null);
        assertResult(testSpecification(dto), 3);
    }

    @Test
    void search_tagCode_success() {
        SearchProvisioningProductDTO dto = new SearchProvisioningProductDTO();
        dto.setTagCode("HECARD");
        assertResult(testSpecification(dto),  2L);
    }

    @Test
    void search_tagCode_notPresent() {
        SearchProvisioningProductDTO dto = new SearchProvisioningProductDTO();
        dto.setTagCode("HUCCHGE");
        Assertions.assertEquals(0, testSpecification(dto).getNumberOfElements());
    }

    @Test
    void search_tagCode_empty() {
        SearchProvisioningProductDTO dto = new SearchProvisioningProductDTO();
        dto.setTagCode(null);
        assertResult(testSpecification(dto), 3);
    }

    @Test
    void search_tagAction_success() {
        SearchProvisioningProductDTO dto = new SearchProvisioningProductDTO();
        dto.setTagAction(ProvisioningTagAction.CANCEL);
        assertResult(testSpecification(dto), 2L);
    }

    @Test
    void search_tagAction_notPresent() {
        SearchProvisioningProductDTO dto = new SearchProvisioningProductDTO();
        dto.setTagAction(ProvisioningTagAction.TRANSFER);
        Assertions.assertEquals(0, testSpecification(dto).getNumberOfElements());
    }

    @Test
    void search_tagAction_empty() {
        SearchProvisioningProductDTO dto = new SearchProvisioningProductDTO();
        dto.setTagAction(null);
        assertResult(testSpecification(dto), 3);
    }

    @Test
    void search_serviceAction_success() {
        SearchProvisioningProductDTO dto = new SearchProvisioningProductDTO();
        dto.setServiceAction(ServiceAction.DEACTIVATION);
        assertResult(testSpecification(dto),  2L);
    }

    @Test
    void search_serviceAction_notPresent() {
        SearchProvisioningProductDTO dto = new SearchProvisioningProductDTO();
        dto.setServiceAction(ServiceAction.BARRING);
        Assertions.assertEquals(0, testSpecification(dto).getNumberOfElements());
    }

    @Test
    void search_serviceAction_empty() {
        SearchProvisioningProductDTO dto = new SearchProvisioningProductDTO();
        dto.setServiceAction(null);
        assertResult(testSpecification(dto), 3);
    }
}
