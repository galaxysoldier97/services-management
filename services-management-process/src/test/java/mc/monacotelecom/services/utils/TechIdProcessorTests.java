package mc.monacotelecom.services.utils;

import mc.monacotelecom.inventory.common.nls.EnableCommonNls;
import mc.monacotelecom.services.dto.request.UpdateServiceDTO;
import mc.monacotelecom.services.entity.ServiceAccess;
import mc.monacotelecom.services.entity.ServiceComponent;
import mc.monacotelecom.services.enums.*;
import mc.monacotelecom.services.exceptions.SvcValidationException;
import mc.monacotelecom.services.repository.ServiceAccessRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.stream.Stream;

import static mc.monacotelecom.services.utils.TechIdProcessor.DEFAULT_MIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {
        TechIdProcessor.class
})
@EnableCommonNls
@ActiveProfiles("test")
class TechIdProcessorTests {
    @Autowired
    private TechIdProcessor techIdProcessor;

    @MockBean
    private ServiceAccessRepository serviceAccessRepository;

    private static Stream<Arguments> activityAndAccessType() {
        return Stream.of(
                arguments(ServiceActivity.INTERNET, Network.DOCSIS),
                arguments(ServiceActivity.INTERNET, Network.FTTH),
         
                arguments(ServiceActivity.MOBILE, Network.MOBILE)
        );
    }

    @ParameterizedTest
    @MethodSource("activityAndAccessType")
    void getAtCreationTime_returnsNull(ServiceActivity serviceActivity, Network accessType) {
        var serviceAccess = getServiceAccess(serviceActivity, accessType);

        var techId = techIdProcessor.getAtCreationTime(serviceAccess);

        assertTrue(techId.isEmpty());
    }

    @Test
    void getAtCreationTime_MOBILE_FREEDHOME_returnsComputeMaxDefault() {
        var serviceAccess = getServiceAccess(ServiceActivity.MOBILE, Network.FREEDHOME);
        when(serviceAccessRepository.findFirstByAccessTypeOrderByTechIdDesc(Network.FREEDHOME))
                .thenReturn(Optional.empty());

        var techId = techIdProcessor.getAtCreationTime(serviceAccess);

        assertTrue(techId.isPresent());
        assertEquals(techIdProcessor.plmnCode + DEFAULT_MIN, techId.get());
    }

    @Test
    void getAtCreationTime_MOBILE_FREEDHOME_returnsComputeMaxIncrement() {
        var serviceAccess = getServiceAccess(ServiceActivity.MOBILE, Network.FREEDHOME);

        ServiceAccess existingServiceAccess = getServiceAccess(ServiceActivity.MOBILE, Network.FREEDHOME);
        existingServiceAccess.setTechId("212100000000957");

        when(serviceAccessRepository.findFirstByAccessTypeOrderByTechIdDesc(Network.FREEDHOME))
                .thenReturn(Optional.of(existingServiceAccess));

        var techId = techIdProcessor.getAtCreationTime(serviceAccess);

        assertTrue(techId.isPresent());
        assertEquals("212100000000958", techId.get());
    }

    @Test
    void getAtCreationTime_MOBILE_FREEDHOME_returnsComputeMaxThrowsIfNotNumber() {
        var serviceAccess = getServiceAccess(ServiceActivity.MOBILE, Network.FREEDHOME);

        ServiceAccess existingServiceAccess = getServiceAccess(ServiceActivity.MOBILE, Network.FREEDHOME);
        existingServiceAccess.setTechId("2121000000ACDB");

        when(serviceAccessRepository.findFirstByAccessTypeOrderByTechIdDesc(Network.FREEDHOME))
                .thenReturn(Optional.of(existingServiceAccess));

        var e = assertThrows(SvcValidationException.class, () -> techIdProcessor.getAtCreationTime(serviceAccess));

        assertEquals("Failed to calculate next techId for Network 'FREEDHOME' because of non-numeric value '2121000000ACDB'", e.getMessage());
    }

    @Test
    void getAtCreationTime_MOBILE_FREEDHOME_returnsComputeMaxThrowsIfDoesNotStartWithPlmnCode() {
        var serviceAccess = getServiceAccess(ServiceActivity.MOBILE, Network.FREEDHOME);

        ServiceAccess existingServiceAccess = getServiceAccess(ServiceActivity.MOBILE, Network.FREEDHOME);
        existingServiceAccess.setTechId("992100000000957");

        when(serviceAccessRepository.findFirstByAccessTypeOrderByTechIdDesc(Network.FREEDHOME))
                .thenReturn(Optional.of(existingServiceAccess));

        var e = assertThrows(SvcValidationException.class, () -> techIdProcessor.getAtCreationTime(serviceAccess));

        assertEquals("Failed to calculate next techId for Network 'FREEDHOME' because of value '992100000000957' not starting with PLMN code '21210'", e.getMessage());
    }

    @Test
    void getAtUpdateTime_ACCESS_TV_ZATTOO_returnsCustomerNo() {
        var serviceAccess = getServiceAccess(ServiceActivity.TV, Network.ZATTOO);

        var updateServiceRequestDTO = new UpdateServiceDTO();
        updateServiceRequestDTO.setAction(ServiceUpdateAction.addCrmRef);
        updateServiceRequestDTO.setCustomerNo(456L);

        var techId = techIdProcessor.getAtUpdateTime(serviceAccess, updateServiceRequestDTO);

        assertTrue(techId.isPresent());
        assertEquals("456", techId.get());
    }

    @Test
    void getAtUpdateTime_ACCESS_TV_ZATTOO_returnsCustomerNotEvenWithTechId() {
        var serviceAccess = getServiceAccess(ServiceActivity.TV, Network.ZATTOO);

        var updateServiceRequestDTO = new UpdateServiceDTO();
        updateServiceRequestDTO.setAction(ServiceUpdateAction.addCrmRef);
        updateServiceRequestDTO.setCustomerNo(456L);
        updateServiceRequestDTO.setTechId("SHOULD NOT BE USED");

        var techId = techIdProcessor.getAtUpdateTime(serviceAccess, updateServiceRequestDTO);

        assertTrue(techId.isPresent());
        assertEquals("456", techId.get());
    }

    @Test
    void getAtUpdateTime_ACCESS_INTERNET_FTTH_returnsRequestTechId() {
        var serviceAccess = getServiceAccess(ServiceActivity.INTERNET, Network.FTTH);

        var updateServiceRequestDTO = new UpdateServiceDTO();
        updateServiceRequestDTO.setAction(ServiceUpdateAction.addCrmRef);
        updateServiceRequestDTO.setCustomerNo(456L);
        updateServiceRequestDTO.setTechId("789");

        var techId = techIdProcessor.getAtUpdateTime(serviceAccess, updateServiceRequestDTO);

        assertTrue(techId.isPresent());
        assertEquals("789", techId.get());
    }

    @Test
    void getAtUpdateTime_ACCESS_INTERNET_DOCSIS_returnsRequestTechId() {
        var serviceAccess = getServiceAccess(ServiceActivity.INTERNET, Network.DOCSIS);

        var updateServiceRequestDTO = new UpdateServiceDTO();
        updateServiceRequestDTO.setAction(ServiceUpdateAction.addCrmRef);
        updateServiceRequestDTO.setCustomerNo(456L);
        updateServiceRequestDTO.setTechId("789");

        var techId = techIdProcessor.getAtUpdateTime(serviceAccess, updateServiceRequestDTO);

        assertTrue(techId.isPresent());
        assertEquals("789", techId.get());
    }

    @Test
    void getAtUpdateTime_ACCESS_nullAccessType_returnsRequestTechId() {
        var serviceAccess = getServiceAccess(ServiceActivity.INTERNET, Network.DOCSIS);
        serviceAccess.setAccessType(null);

        var updateServiceRequestDTO = new UpdateServiceDTO();
        updateServiceRequestDTO.setAction(ServiceUpdateAction.addCrmRef);
        updateServiceRequestDTO.setCustomerNo(456L);
        updateServiceRequestDTO.setTechId("789");

        var techId = techIdProcessor.getAtUpdateTime(serviceAccess, updateServiceRequestDTO);

        assertTrue(techId.isPresent());
        assertEquals("789", techId.get());
    }

    @Test
    void getAtUpdateTime_COMPONENT_CLOUDCALLING_returnsRequestTechId() {
        var serviceComponent = getServiceComponent("TEAMS");

        var updateServiceRequestDTO = new UpdateServiceDTO();
        updateServiceRequestDTO.setAction(ServiceUpdateAction.addCrmRef);
        updateServiceRequestDTO.setTechId("789");

        var techId = techIdProcessor.getAtUpdateTime(serviceComponent, updateServiceRequestDTO);

        assertTrue(techId.isPresent());
        assertEquals("789", techId.get());
    }

    @Test
    void getAtUpdateTime_returnsNullIfNoTechId() {
        var serviceAccess = getServiceAccess(ServiceActivity.INTERNET, Network.FTTH);

        var updateServiceRequestDTO = new UpdateServiceDTO();
        updateServiceRequestDTO.setAction(ServiceUpdateAction.addCrmRef);

        var techId = techIdProcessor.getAtUpdateTime(serviceAccess, updateServiceRequestDTO);

        assertTrue(techId.isEmpty());
    }

    private ServiceAccess getServiceAccess(ServiceActivity serviceActivity, Network accessType) {
        var serviceAccess = new ServiceAccess();
        serviceAccess.setServiceCategory(ServiceCategory.ACCESS);
        serviceAccess.setServiceId(123L);
        serviceAccess.setStatus(Status.ACTIVATED);
        serviceAccess.setServiceActivity(serviceActivity);
        serviceAccess.setAccessType(accessType);
        return serviceAccess;
    }

    private ServiceComponent getServiceComponent(String componentType) {
        var serviceComponent = new ServiceComponent();
        serviceComponent.setServiceCategory(ServiceCategory.COMPONENT);
        serviceComponent.setServiceId(123L);
        serviceComponent.setStatus(Status.ACTIVATED);
        serviceComponent.setComponentType(componentType);
        return serviceComponent;
    }
}
