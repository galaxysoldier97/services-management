package mc.monacotelecom.services.integration.specifications;

import mc.monacotelecom.services.dto.search.SearchActivationCodeDTO;
import mc.monacotelecom.services.entity.ActivationCode;
import mc.monacotelecom.services.enums.ActivationNature;
import mc.monacotelecom.services.enums.NetworkComponent;
import mc.monacotelecom.services.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;

@Sql({"/sql/clean.sql", "/sql/activation_code_data.sql"})
class ActivationCodeSpecificationTest extends BaseIntegrationTest {

    private Page<ActivationCode> testSpecification(final SearchActivationCodeDTO searchDTO) {
        Specification<ActivationCode> specification = activationCodeProcess.prepareSpecification(searchDTO);
        return activationCodeRepository.findAll(specification, PAGEABLE);
    }

    private void assertResult(final Page<ActivationCode> result, final int expectedElements, final Long expectedId) {
        Assertions.assertEquals(expectedElements, result.getNumberOfElements());
        Assertions.assertEquals(expectedId, result.getContent().get(0).getInternalId());
    }

    @Test
    void search_activCode_success() {
        SearchActivationCodeDTO searchActivationCodeDTO = new SearchActivationCodeDTO();
        searchActivationCodeDTO.setActivCode("HOG_HBOX20");
        assertResult(testSpecification(searchActivationCodeDTO), 1, 4L);
    }

    @Test
    void search_activCode_empty() {
        SearchActivationCodeDTO searchActivationCodeDTO = new SearchActivationCodeDTO();
        searchActivationCodeDTO.setActivCode("");
        assertResult(testSpecification(searchActivationCodeDTO), 7, 1L);
    }

    @Test
    void search_activCode_notPresent() {
        SearchActivationCodeDTO searchActivationCodeDTO = new SearchActivationCodeDTO();
        searchActivationCodeDTO.setActivCode("test");
        Assertions.assertEquals(0, testSpecification(searchActivationCodeDTO).getNumberOfElements());
    }

    @Test
    void search_nature_success() {
        SearchActivationCodeDTO searchActivationCodeDTO = new SearchActivationCodeDTO();
        searchActivationCodeDTO.setNature(ActivationNature.PROFILE);
        assertResult(testSpecification(searchActivationCodeDTO), 1, 1L);
    }

    @Test
    void search_nature_empty() {
        SearchActivationCodeDTO searchActivationCodeDTO = new SearchActivationCodeDTO();
        searchActivationCodeDTO.setNature(null);
        assertResult(testSpecification(searchActivationCodeDTO), 7, 1L);
    }

    @Test
    void search_nature_notPresent() {
        SearchActivationCodeDTO searchActivationCodeDTO = new SearchActivationCodeDTO();
        searchActivationCodeDTO.setNature(ActivationNature.FORWARDING);
        Assertions.assertEquals(0, testSpecification(searchActivationCodeDTO).getNumberOfElements());
    }

    @Test
    void search_networkComponent_success() {
        SearchActivationCodeDTO searchActivationCodeDTO = new SearchActivationCodeDTO();
        searchActivationCodeDTO.setNetworkComponent(NetworkComponent.SPG);
        assertResult(testSpecification(searchActivationCodeDTO), 4, 1L);
    }

    @Test
    void search_networkComponent_empty() {
        SearchActivationCodeDTO searchActivationCodeDTO = new SearchActivationCodeDTO();
        searchActivationCodeDTO.setNetworkComponent(null);
        assertResult(testSpecification(searchActivationCodeDTO), 7, 1L);
    }

    @Test
    void search_networkComponent_notPresent() {
        SearchActivationCodeDTO searchActivationCodeDTO = new SearchActivationCodeDTO();
        searchActivationCodeDTO.setNetworkComponent(NetworkComponent.DISE);
        Assertions.assertEquals(0, testSpecification(searchActivationCodeDTO).getNumberOfElements());
    }

    @Test
    void search_tagCode_success() {
        SearchActivationCodeDTO searchActivationCodeDTO = new SearchActivationCodeDTO();
        searchActivationCodeDTO.setTagCode("IEFBOX2");
        assertResult(testSpecification(searchActivationCodeDTO), 1, 7L);
    }

    @Test
    void search_tagCode_successWithLike() {
        SearchActivationCodeDTO searchActivationCodeDTO = new SearchActivationCodeDTO();
        searchActivationCodeDTO.setTagCode("IEFBOX");
        Assertions.assertEquals(2, testSpecification(searchActivationCodeDTO).getNumberOfElements());
    }

    @Test
    void search_tagCode_empty() {
        SearchActivationCodeDTO searchActivationCodeDTO = new SearchActivationCodeDTO();
        searchActivationCodeDTO.setTagCode("");
        assertResult(testSpecification(searchActivationCodeDTO), 7, 1L);
    }

    @Test
    void search_tagCode_notPresent() {
        SearchActivationCodeDTO searchActivationCodeDTO = new SearchActivationCodeDTO();
        searchActivationCodeDTO.setTagCode("MISSING");
        Assertions.assertEquals(0, testSpecification(searchActivationCodeDTO).getNumberOfElements());
    }

    @Test
    void search_productCode_success() {
        SearchActivationCodeDTO searchActivationCodeDTO = new SearchActivationCodeDTO();
        searchActivationCodeDTO.setProductCode("PAY_AS_YOU_GO_1");
        assertResult(testSpecification(searchActivationCodeDTO), 1, 6L);
    }

    @SuppressWarnings("java:S5976")
    @Test
    void search_productCode_successLike() {
        SearchActivationCodeDTO searchActivationCodeDTO = new SearchActivationCodeDTO();
        searchActivationCodeDTO.setProductCode("PAY_AS_YOU_GO");
        Assertions.assertEquals(2, testSpecification(searchActivationCodeDTO).getNumberOfElements());
    }

    @Test
    void search_productCode_empty() {
        SearchActivationCodeDTO searchActivationCodeDTO = new SearchActivationCodeDTO();
        searchActivationCodeDTO.setProductCode("");
        Assertions.assertEquals(7, testSpecification(searchActivationCodeDTO).getNumberOfElements());
    }

    @Test
    void search_productCode_notPresent() {
        SearchActivationCodeDTO searchActivationCodeDTO = new SearchActivationCodeDTO();
        searchActivationCodeDTO.setProductCode("MISSING");
        Assertions.assertEquals(0, testSpecification(searchActivationCodeDTO).getNumberOfElements());
    }
}
