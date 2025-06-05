package mc.monacotelecom.services.integration.specifications;

import mc.monacotelecom.services.dto.search.BaseSearchServiceDTO;
import mc.monacotelecom.services.dto.search.SearchServiceAccessDTO;
import mc.monacotelecom.services.dto.search.SearchServiceComponentDTO;
import mc.monacotelecom.services.dto.search.SearchServiceDTO;
import mc.monacotelecom.services.entity.Service;
import mc.monacotelecom.services.entity.ServiceAccess;
import mc.monacotelecom.services.entity.ServiceComponent;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ServiceActivity;
import mc.monacotelecom.services.enums.ServiceCategory;
import mc.monacotelecom.services.enums.Status;
import mc.monacotelecom.services.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Sql({"/sql/clean.sql", "/sql/service_data.sql"})
class ServiceSpecificationTest extends BaseIntegrationTest {

    private Page<Service> testSpecificationService(final BaseSearchServiceDTO searchDTO) {
        Specification specification = serviceProcess.prepareSpecificationGenericService(searchDTO);
        return serviceRepository.findAll(specification, PAGEABLE);
    }

    private Page<ServiceAccess> testSpecificationServiceAccess(final BaseSearchServiceDTO searchDTO) {
        Specification specification = serviceProcess.prepareSpecificationServiceAccess(searchDTO);
        return serviceAccessRepository.findAll(specification, PAGEABLE);
    }

    private Page<ServiceComponent> testSpecificationServiceComponent(final BaseSearchServiceDTO searchDTO) {
        Specification specification = serviceProcess.prepareSpecificationServiceComponent(searchDTO);
        return serviceComponentRepository.findAll(specification, PAGEABLE);
    }

    private <T extends Service> void assertResult(final Page<T> result, final int expectedElements) {
        Assertions.assertEquals(expectedElements, result.getNumberOfElements());
    }

    private <T extends Service> void assertResult(final Page<T> result, final Long expectedId) {
        Assertions.assertEquals(1, result.getNumberOfElements());
        Assertions.assertEquals(expectedId, result.getContent().get(0).getServiceId());
    }

    private LocalDateTime prepareDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(date, formatter);
    }

    @Test
    void search_crmService_success() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setCrmServiceId("3");
        assertResult(testSpecificationService(searchServiceDTO), 1L);
    }

    @Test
    void search_crmService_empty() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setCrmServiceId("");
        assertResult(testSpecificationService(searchServiceDTO), 9);
    }

    @Test
    void search_crmService_notFound() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setCrmServiceId("99");
        Assertions.assertEquals(0, testSpecificationService(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void search_serviceId_success() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setServiceId(3L);
        assertResult(testSpecificationService(searchServiceDTO), 3L);
    }

    @Test
    void search_serviceId_empty() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setServiceId(null);
        assertResult(testSpecificationService(searchServiceDTO), 9);
    }

    @Test
    void search_serviceId_notFound() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setServiceId(99L);
        Assertions.assertEquals(0, testSpecificationService(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void search_subscriptionId_success() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setSubscriptionId(3L);
        assertResult(testSpecificationService(searchServiceDTO), 3L);
    }

    @Test
    void search_subscriptionId_empty() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setSubscriptionId(null);
        assertResult(testSpecificationService(searchServiceDTO), 9);
    }

    @Test
    void search_subscriptionId_notFound() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setSubscriptionId(7L);
        Assertions.assertEquals(0, testSpecificationService(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void search_serviceActivity_success() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setServiceActivity(ServiceActivity.INTERNET);
        assertResult(testSpecificationService(searchServiceDTO), 2);
    }

    @Test
    void search_serviceActivity_empty() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setServiceActivity(null);
        assertResult(testSpecificationService(searchServiceDTO), 9);
    }

    @Test
    void search_serviceActivity_notFound() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setServiceActivity(ServiceActivity.TV);
        Assertions.assertEquals(0, testSpecificationService(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void search_number_success() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setNumber("35790000112");
        assertResult(testSpecificationService(searchServiceDTO), 3L);
    }

    @Test
    void search_number_empty() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setNumber("");
        assertResult(testSpecificationService(searchServiceDTO), 9);
    }

    @Test
    void search_number_notFound() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setNumber("12345");
        Assertions.assertEquals(0, testSpecificationService(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void search_rangeId_success() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setMainRangeId(8L);
        assertResult(testSpecificationService(searchServiceDTO), 4L);
    }

    @Test
    void search_rangeId_empty() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setMainRangeId(null);
        assertResult(testSpecificationService(searchServiceDTO), 9);
    }

    @Test
    void search_rangeId_notFound() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setMainRangeId(9L);
        Assertions.assertEquals(0, testSpecificationService(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void search_status_success() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setStatus(Status.BARRED);
        assertResult(testSpecificationService(searchServiceDTO), 6L);
    }

    @Test
    void search_status_empty() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setStatus(null);
        assertResult(testSpecificationService(searchServiceDTO), 9);
    }

    @Test
    void search_status_notFound() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setStatus(Status.SUSPENDED);
        Assertions.assertEquals(0, testSpecificationService(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void search_activationDate_success() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setActivationDate(prepareDate("2020-09-09 00:29:32"));
        assertResult(testSpecificationService(searchServiceDTO), 3);
    }

    @Test
    void search_activationDate_empty() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setActivationDate(null);
        assertResult(testSpecificationService(searchServiceDTO), 9);
    }

    @Test
    void search_activationDate_notFound() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setActivationDate(prepareDate("2021-09-09 00:29:32"));
        Assertions.assertEquals(0, testSpecificationService(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void searchService_serviceCategory_success() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setServiceCategory(ServiceCategory.ACCESS);
        assertResult(testSpecificationService(searchServiceDTO), 9);
    }

    @Test
    void searchService_serviceCategory_empty() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setServiceCategory(null);
        assertResult(testSpecificationService(searchServiceDTO), 9);
    }

    @Test
    void searchAccess_activationDate_notFound() {
        SearchServiceDTO searchServiceDTO = new SearchServiceDTO();
        searchServiceDTO.setActivationDate(prepareDate("2021-09-09 00:29:32"));
        Assertions.assertEquals(0, testSpecificationService(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void searchAccess_parentServiceID_success() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setParentServiceId(1L);
        assertResult(testSpecificationServiceAccess(searchServiceDTO), 5L);
    }

    @Test
    void searchAccess_parentServiceID_empty() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setParentServiceId(null);
        assertResult(testSpecificationServiceAccess(searchServiceDTO), 5);
    }

    @Test
    void searchAccess_parentServiceID_notFound() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setParentServiceId(9L);
        Assertions.assertEquals(0, testSpecificationServiceAccess(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void searchAccess_accessPoint_success() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setAccessPointId("15");
        assertResult(testSpecificationServiceAccess(searchServiceDTO), 3L);
    }

    @Test
    void searchAccess_accessPoint_empty() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setAccessPointId(null);
        assertResult(testSpecificationServiceAccess(searchServiceDTO), 5);
    }

    @Test
    void searchAccess_accessPoint_notFound() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setAccessPointId("9");
        Assertions.assertEquals(0, testSpecificationServiceAccess(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void searchComponent_accessPoint_crmServiceID_success() {
        var searchServiceDTO = new SearchServiceComponentDTO();
        searchServiceDTO.setAccessCrmServiceId("5");
        assertResult(testSpecificationServiceComponent(searchServiceDTO), 4L);
    }

    @Test
    void searchComponent_accessPoint_crmServiceID_empty() {
        var searchServiceDTO = new SearchServiceComponentDTO();
        searchServiceDTO.setAccessCrmServiceId(null);
        assertResult(testSpecificationServiceComponent(searchServiceDTO), 4);
    }

    @Test
    void searchComponent_accessPoint_crmServiceID_notFound() {
        var searchServiceDTO = new SearchServiceComponentDTO();
        searchServiceDTO.setAccessCrmServiceId("9");
        Assertions.assertEquals(0, testSpecificationServiceComponent(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void searchAccess_equipmentId_success() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setAccessPointId("15");
        assertResult(testSpecificationServiceAccess(searchServiceDTO), 3L);
    }

    @Test
    void searchAccess_equipmentId_empty() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setAccessPointId(null);
        assertResult(testSpecificationServiceAccess(searchServiceDTO), 5);
    }

    @Test
    void searchAccess_equipmentId_notFound() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setAccessPointId("9");
        Assertions.assertEquals(0, testSpecificationServiceAccess(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void searchAccess_accessType_success() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setAccessType(Network.BBHB);
        assertResult(testSpecificationServiceAccess(searchServiceDTO), 1L);
    }

    @Test
    void searchAccess_accessType_empty() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        assertResult(testSpecificationServiceAccess(searchServiceDTO), 5);
    }

    @Test
    void searchAccess_accessType_notFound() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setAccessType(Network.DOCSIS);
        Assertions.assertEquals(0, testSpecificationServiceAccess(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void searchAccess_customerNo_success() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setCustomerNo(10L);
        assertResult(testSpecificationServiceAccess(searchServiceDTO), 5L);
    }

    @Test
    void searchAccess_customerNo_empty() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setCustomerNo(null);
        assertResult(testSpecificationServiceAccess(searchServiceDTO), 5);
    }

    @Test
    void searchAccess_customerNo_notFound() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setCustomerNo(11L);
        Assertions.assertEquals(0, testSpecificationServiceAccess(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void searchComponent_accessServiceId_success() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setAccessServiceId(5L);
        assertResult(testSpecificationServiceAccess(searchServiceDTO), 5);
    }

    @Test
    void searchComponent_accessServiceId_empty() {
        var searchServiceDTO = new SearchServiceComponentDTO();
        searchServiceDTO.setAccessServiceId(null);
        assertResult(testSpecificationServiceComponent(searchServiceDTO), 4);
    }

    @Test
    void searchComponent_componentType_success() {
        var searchServiceDTO = new SearchServiceComponentDTO();
        searchServiceDTO.setComponentType("TEAMS");
        assertResult(testSpecificationServiceComponent(searchServiceDTO), 3);
    }

    @Test
    void searchComponent_componentType_notFound() {
        var searchServiceDTO = new SearchServiceComponentDTO();
        searchServiceDTO.setComponentType("CAS");
        assertResult(testSpecificationServiceComponent(searchServiceDTO), 0);
    }

    @Test
    void searchComponent_accessServiceId_notFound() {
        var searchServiceDTO = new SearchServiceComponentDTO();
        searchServiceDTO.setAccessServiceId(11L);
        Assertions.assertEquals(0, testSpecificationServiceComponent(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void searchAccess_multi_success() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setStatus(Status.ACTIVATED);
        searchServiceDTO.setAccessType(Network.BBHB);
        assertResult(testSpecificationServiceAccess(searchServiceDTO), 1L);
    }

    @Test
    void searchAccess_multi_notFound() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setStatus(Status.ACTIVATED);
        searchServiceDTO.setAccessType(Network.FTTH);
        Assertions.assertEquals(0, testSpecificationServiceAccess(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void searchComponent_multi_success() {
        var searchServiceDTO = new SearchServiceComponentDTO();
        searchServiceDTO.setStatus(Status.CANCELED);
        searchServiceDTO.setAccessServiceId(5L);
        assertResult(testSpecificationServiceComponent(searchServiceDTO), 4L);
    }

    @Test
    void searchComponent_multi_notFound() {
        var searchServiceDTO = new SearchServiceComponentDTO();
        searchServiceDTO.setStatus(Status.PENDING);
        searchServiceDTO.setAccessServiceId(5L);
        Assertions.assertEquals(0, testSpecificationServiceComponent(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void searchAccess_ontId_found() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setOntId("BKR01:1-1-1-2-4");
        assertResult(testSpecificationServiceAccess(searchServiceDTO), 3L);
    }

    @Test
    void searchAccess_ontId_empty() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setOntId("");
        assertResult(testSpecificationServiceAccess(searchServiceDTO), 5);
    }

    @Test
    void searchAccess_ontId_notFound() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setOntId("BIU78:1-1-1-2-99");
        Assertions.assertEquals(0, testSpecificationServiceAccess(searchServiceDTO).getNumberOfElements());
    }

    @Test
    void searchAccess_techId_found() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setTechId("212100000000002");
        assertResult(testSpecificationServiceAccess(searchServiceDTO), 7L);
    }

    @Test
    void searchAccess_techId_empty() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setTechId("");
        assertResult(testSpecificationServiceAccess(searchServiceDTO), 5);
    }

    @Test
    void searchAccess_techId_notFound() {
        var searchServiceDTO = new SearchServiceAccessDTO();
        searchServiceDTO.setTechId("212100000000003");
        Assertions.assertEquals(0, testSpecificationServiceAccess(searchServiceDTO).getNumberOfElements());
    }
}
