package mc.monacotelecom.services.process;

import mc.monacotelecom.inventory.common.nls.EnableCommonNls;
import mc.monacotelecom.services.dto.TagActionDTO;
import mc.monacotelecom.services.dto.request.ChangeTagsDTO;
import mc.monacotelecom.services.entity.*;
import mc.monacotelecom.services.enums.*;
import mc.monacotelecom.services.exceptions.SvcNotFoundException;
import mc.monacotelecom.services.exceptions.SvcValidationException;
import mc.monacotelecom.services.repository.ProvisioningTagRepository;
import mc.monacotelecom.services.repository.ServiceActivationRepository;
import mc.monacotelecom.services.repository.ServiceRepository;
import mc.monacotelecom.services.repository.ServiceTagRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static mc.monacotelecom.services.process.ChangeTagsTestsHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE, classes = {
        ChangeTagsProcess.class
})
@EnableCommonNls
@ActiveProfiles("test")
class ChangeTagsAddTests {

    @Autowired
    private ChangeTagsProcess changeTagsProcess;

    @MockBean
    private ServiceRepository<Service> serviceRepository;

    @MockBean
    private ProvisioningTagRepository provisioningTagRepository;

    @MockBean
    private ServiceActivationRepository serviceActivationRepository;

    @MockBean
    private ServiceTagRepository serviceTagRepository;

    @Captor
    private ArgumentCaptor<Set<ServiceTag>> tagsCaptor;

    @Captor
    private ArgumentCaptor<List<ServiceActivation>> activationsCaptor;

    @Test
    void changeTags_missingService() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1));

        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.empty());

        var e = assertThrows(SvcNotFoundException.class, () -> changeTagsProcess.changeTags(SERVICE_ID, dto));
        assertEquals("Service with ID '1' was not found", e.getMessage());

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_missingTagIfOne() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1));

        var service = new Service();
        service.setServiceId(SERVICE_ID);
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        when(provisioningTagRepository.findById(TAG_CODE)).thenReturn(Optional.empty());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertTrue(response.getServiceActivations().isEmpty());
        assertTrue(response.getServiceTags().isEmpty());

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_missingTagIfMultiple() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE_2);
        tag2.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1, tag2));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        ProvisioningTag tag = getProvisioningTag();
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE, TAG_CODE_2))).thenReturn(List.of(tag));

        when(serviceActivationRepository.findByService(service)).thenReturn(Collections.emptyList());

        var e = assertThrows(SvcValidationException.class, () -> changeTagsProcess.changeTags(SERVICE_ID, dto));
        assertEquals("Requested provisioning tags [TAG_CODE_2] do not exist", e.getMessage());

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_serviceHavingMissingTags() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1));

        var service = new Service();
        service.setServiceId(SERVICE_ID);

        // Put orphan service tag on service
        when(serviceRepository.findServicesWithNonExistentProvisioningTagByService(SERVICE_ID)).thenReturn(Collections.singletonList("MISSINGTAG"));

        // Put orphan service activation on service
        when(serviceRepository.findServicesWithNonExistentActivationCodeByService(SERVICE_ID)).thenReturn(Collections.singletonList("MISSINGCODE"));

        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var tag = getProvisioningTag();
        tag.setActivity(ProvisioningTagActivity.MOBILE);
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        var e = assertThrows(SvcValidationException.class, () -> changeTagsProcess.changeTags(SERVICE_ID, dto));
        assertEquals("Service with ID '1' refers to missing provisioning tags [MISSINGTAG] or activation codes [MISSINGCODE]", e.getMessage());

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_activityInconsistency_ACCESS() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1));

        var service = new Service();
        service.setServiceId(SERVICE_ID);
        service.setServiceCategory(ServiceCategory.ACCESS);
        service.setServiceActivity(ServiceActivity.INTERNET);
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var tag = getProvisioningTag();
        tag.setActivity(ProvisioningTagActivity.MOBILE);
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        var e = assertThrows(SvcValidationException.class, () -> changeTagsProcess.changeTags(SERVICE_ID, dto));
        assertEquals("Provisioning tag 'TAG_CODE' has activity 'MOBILE' that does not match service activity 'INTERNET'", e.getMessage());

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_activityInconsistency_COMPONENT() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1));

        ServiceComponent service = getServiceComponent();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var tag = getProvisioningTag();
        tag.setCategory(ServiceCategory.COMPONENT);
        tag.setActivity(ProvisioningTagActivity.MOBILE);
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        var e = assertThrows(SvcValidationException.class, () -> changeTagsProcess.changeTags(SERVICE_ID, dto));
        assertEquals("Provisioning tag 'TAG_CODE' has activity 'MOBILE' that does not match service activity 'INTERNET'", e.getMessage());

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_categoryInconsistency_ACCESS() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var tag = getProvisioningTag();
        tag.setCategory(ServiceCategory.COMPONENT);
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        var e = assertThrows(SvcValidationException.class, () -> changeTagsProcess.changeTags(SERVICE_ID, dto));
        assertEquals("Provisioning tag 'TAG_CODE' has category 'COMPONENT' that does not match service category 'ACCESS'", e.getMessage());

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_categoryInconsistency_COMPONENT() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1));

        ServiceComponent service = getServiceComponent();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var tag = getProvisioningTag();
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        var e = assertThrows(SvcValidationException.class, () -> changeTagsProcess.changeTags(SERVICE_ID, dto));
        assertEquals("Provisioning tag 'TAG_CODE' has category 'ACCESS' that does not match service category 'COMPONENT'", e.getMessage());

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_accessTypeInconsistency_ACCESS() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var tag = getProvisioningTag();
        tag.setAccessType(Network.FTTH);
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        var e = assertThrows(SvcValidationException.class, () -> changeTagsProcess.changeTags(SERVICE_ID, dto));
        assertEquals("Provisioning tag 'TAG_CODE' has access type 'FTTH' that does not match service access type 'DOCSIS'", e.getMessage());

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_onlyOneAdd_ACCESS() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        ProvisioningTag tag = getProvisioningTag();
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        when(serviceActivationRepository.findByService(service)).thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(1, response.getServiceActivations().size());
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) 1L, response.getServiceActivations().get(0).getActivValue());

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_onlyOneAdd_COMPONENT() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1));

        ServiceComponent service = getServiceComponent();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        ProvisioningTag tag = getProvisioningTag();
        tag.setCategory(ServiceCategory.COMPONENT);
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        when(serviceActivationRepository.findByService(service)).thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(1, response.getServiceActivations().size());
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) 1L, response.getServiceActivations().get(0).getActivValue());

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_onlyOneAdd_boosterWithTagValue() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        ProvisioningTag tag = getProvisioningTag();
        // Edit the activation code to make it quota-able (BOOSTER/BUCKET), and add a value on the tag
        tag.getTagActivations().forEach(x -> {
            x.setTagValue(50L);
            x.getActivationCode().setNature(ActivationNature.BOOSTER);
        });
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        when(serviceActivationRepository.findByService(service)).thenReturn(Collections.emptyList());

        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(1, response.getServiceActivations().size(), "Service activations to be performed includes the activation code to be persisted");
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) 50L, response.getServiceActivations().get(0).getActivValue(), "Active value should be the provided value '50'");

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_onlyOnePersistentAdd_notPresentInDb() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        ProvisioningTag tag = getProvisioningTag();
        tag.setPersistent(true);
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        // Service activation was not already existing in DB: it's the first time this activation code is used for this service
        when(serviceActivationRepository.findByService(service)).thenReturn(Collections.emptyList());

        // Service tag was not already existing in DB: it's the first time this tag is used for this service
        when(serviceTagRepository.findAllByServiceAndTags(service, List.of(tag))).thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(1, response.getServiceActivations().size(), "Service activations to be performed includes the activation code to be persisted");
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) 1L, response.getServiceActivations().get(0).getActivValue());

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(1)).saveAll(tagsCaptor.capture());

        final var savedTags = tagsCaptor.getValue();
        assertFalse(savedTags.isEmpty());
        assertEquals(1, savedTags.size());
        final var serviceTag = savedTags.iterator().next();
        assertEquals((Long) 1L, serviceTag.getService().getServiceId());
        assertEquals(TAG_CODE, serviceTag.getTagCode());
        assertNull(serviceTag.getTagValue());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(1)).saveAll(activationsCaptor.capture());

        final var savedActivations = activationsCaptor.getValue();
        assertFalse(savedActivations.isEmpty());
        assertEquals(1, savedActivations.size());
        assertEquals(ACTIV_CODE, savedActivations.get(0).getActivCode());
        assertEquals((Long) 1L, savedActivations.get(0).getService().getServiceId());
        assertEquals((Long) 1L, savedActivations.get(0).getActivValue());
    }

    @Test
    void changeTags_onlyOnePersistentAdd_presentInDb() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        ProvisioningTag tag = getProvisioningTag();
        tag.setPersistent(true);
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        // Service activation already exists in DB: this activation code has already been set for this service
        var serviceActivation = getServiceActivation(service, getActivationCode());
        when(serviceActivationRepository.findByService(service)).thenReturn(List.of(serviceActivation));

        // Service tag already exists in DB: this tag has already been set for this service
        var existingServiceTag = getServiceTag(service, tag);
        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(List.of(existingServiceTag));

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(0, response.getServiceActivations().size(), "Service activations to be performed should not include the already-persisted activation code");

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(1)).saveAll(tagsCaptor.capture());

        final var savedTags = tagsCaptor.getValue();
        assertFalse(savedTags.isEmpty());
        assertEquals(1, savedTags.size());
        final var serviceTag = savedTags.iterator().next();
        assertEquals((Long) 1L, serviceTag.getService().getServiceId());
        assertEquals(TAG_CODE, serviceTag.getTagCode());
        assertNull(serviceTag.getTagValue());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_onlyOnePersistentAdd_boosterWithExistingValue() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        ProvisioningTag tag = getProvisioningTag();
        tag.setPersistent(true);
        // Edit the activation code to make it quota-able (BOOSTER/BUCKET), and add a value on the tag
        tag.getTagActivations().forEach(x -> {
            x.getActivationCode().setNature(ActivationNature.BOOSTER);
        });
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        // Service activation already exists in DB: this activation code has already been set for this service
        var serviceActivation = getServiceActivation(service, getActivationCode());
        serviceActivation.setActivValue(1L);
        serviceActivation.getActivationCode().setNature(ActivationNature.BOOSTER);
        when(serviceActivationRepository.findByService(service)).thenReturn(List.of(serviceActivation));

        // Service tag does not exist in db: this tag has not already been used on this service
        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(1, response.getServiceActivations().size(), "Service activations to be performed includes the activation code to be persisted");
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) 2L, response.getServiceActivations().get(0).getActivValue(), "Active value should be incremented from the initial '1' value");

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(1)).saveAll(tagsCaptor.capture());

        final var savedTags = tagsCaptor.getValue();
        assertFalse(savedTags.isEmpty());
        assertEquals(1, savedTags.size());
        final var serviceTag = savedTags.iterator().next();
        assertEquals((Long) 1L, serviceTag.getService().getServiceId());
        assertEquals(TAG_CODE, serviceTag.getTagCode());
        assertNull(serviceTag.getTagValue());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(1)).saveAll(activationsCaptor.capture());

        final var savedActivations = activationsCaptor.getValue();
        assertFalse(savedActivations.isEmpty());
        assertEquals(1, savedActivations.size());
        assertEquals(ACTIV_CODE, savedActivations.get(0).getActivCode());
        assertEquals((Long) 1L, savedActivations.get(0).getService().getServiceId());
        assertEquals((Long) 2L, savedActivations.get(0).getActivValue());
    }

    @Test
    void changeTags_onlyOnePersistentAdd_boosterWithTagValue() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        ProvisioningTag tag = getProvisioningTag();
        tag.setPersistent(true);
        // Edit the activation code to make it quota-able (BOOSTER/BUCKET), and add a value on the tag
        tag.getTagActivations().forEach(x -> {
            x.setTagValue(50L);
            x.getActivationCode().setNature(ActivationNature.BOOSTER);
        });
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        when(serviceActivationRepository.findByService(service)).thenReturn(Collections.emptyList());

        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(1, response.getServiceActivations().size(), "Service activations to be performed includes the activation code to be persisted");
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) 50L, response.getServiceActivations().get(0).getActivValue(), "Active value should be the provided value '50'");

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(1)).saveAll(tagsCaptor.capture());

        final var savedTags = tagsCaptor.getValue();
        assertFalse(savedTags.isEmpty());
        assertEquals(1, savedTags.size());
        final var serviceTag = savedTags.iterator().next();
        assertEquals((Long) 1L, serviceTag.getService().getServiceId());
        assertEquals(TAG_CODE, serviceTag.getTagCode());
        assertNull(serviceTag.getTagValue());

        // Should be the expected behavior to me
        // assertEquals((Long) 50L, serviceTag.getTagValue());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(1)).saveAll(activationsCaptor.capture());

        final var savedActivations = activationsCaptor.getValue();
        assertFalse(savedActivations.isEmpty());
        assertEquals(1, savedActivations.size());
        assertEquals(ACTIV_CODE, savedActivations.get(0).getActivCode());
        assertEquals((Long) 1L, savedActivations.get(0).getService().getServiceId());
        assertEquals((Long) 50L, savedActivations.get(0).getActivValue());
    }

    @Test
    void changeTags_onlyOnePersistentAdd_boosterWithTagValueAndExistingValue() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        ProvisioningTag tag = getProvisioningTag();
        tag.setPersistent(true);
        // Edit the activation code to make it quota-able (BOOSTER/BUCKET), and add a value on the tag
        tag.getTagActivations().forEach(x -> {
            x.setTagValue(50L);
            x.getActivationCode().setNature(ActivationNature.BOOSTER);
        });
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        // Service activation already exists in DB: this activation code has already been set for this service
        var serviceActivation = getServiceActivation(service, getActivationCode());
        serviceActivation.setActivValue(20L);
        serviceActivation.getActivationCode().setNature(ActivationNature.BOOSTER);
        when(serviceActivationRepository.findByService(service)).thenReturn(List.of(serviceActivation));

        // Service tag does not exist in db: this tag has not already been used on this service
        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(1, response.getServiceActivations().size(), "Service activations to be performed includes the activation code to be persisted");
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) 70L, response.getServiceActivations().get(0).getActivValue(), "Active value should be the addition between initial value '20' and the provided '50' value");

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(1)).saveAll(tagsCaptor.capture());

        final var savedTags = tagsCaptor.getValue();
        assertFalse(savedTags.isEmpty());
        assertEquals(1, savedTags.size());
        final var serviceTag = savedTags.iterator().next();
        assertEquals((Long) 1L, serviceTag.getService().getServiceId());
        assertEquals(TAG_CODE, serviceTag.getTagCode());
        assertNull(serviceTag.getTagValue());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(1)).saveAll(activationsCaptor.capture());

        final var savedActivations = activationsCaptor.getValue();
        assertFalse(savedActivations.isEmpty());
        assertEquals(1, savedActivations.size());
        assertEquals(ACTIV_CODE, savedActivations.get(0).getActivCode());
        assertEquals((Long) 1L, savedActivations.get(0).getService().getServiceId());
        assertEquals((Long) 70L, savedActivations.get(0).getActivValue());
    }

    @Test
    void changeTags_twoPersistentAdd_boosterWithTagValueAndExistingValue() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE_2);
        tag2.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1, tag2));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        ProvisioningTag dbtag1 = getProvisioningTag();
        dbtag1.setPersistent(true);
        // Edit the activation code to make it quota-able (BOOSTER/BUCKET), and add a value on the tag
        dbtag1.getTagActivations().forEach(x -> {
            x.setTagValue(40L);
            x.getActivationCode().setNature(ActivationNature.BOOSTER);
        });

        ProvisioningTag dbtag2 = getProvisioningTag();
        dbtag2.setPersistent(true);
        dbtag2.setTagCode(TAG_CODE_2);
        // Edit the activation code to make it quota-able (BOOSTER/BUCKET), and add a value on the tag
        dbtag2.getTagActivations().forEach(x -> {
            x.setTagValue(30L);
            x.getActivationCode().setNature(ActivationNature.BOOSTER);
        });

        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE, TAG_CODE_2))).thenReturn(List.of(dbtag1, dbtag2));

        // Service activation already exists in DB: this activation code has already been set for this service
        var serviceActivation = getServiceActivation(service, getActivationCode());
        serviceActivation.setActivValue(20L);
        serviceActivation.getActivationCode().setNature(ActivationNature.BOOSTER);
        when(serviceActivationRepository.findByService(service)).thenReturn(List.of(serviceActivation));

        // Service tag does not exist in db: this tag has not already been used on this service
        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(2, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());
        assertEquals(TAG_CODE_2, response.getServiceTags().get(1).getTagCode());

        assertEquals(1, response.getServiceActivations().size(), "Service activations to be performed includes the activation code to be persisted");
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) 90L, response.getServiceActivations().get(0).getActivValue(), "Active value should be the addition between initial value '20' and the provided '40' and '30' value");

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(1)).saveAll(tagsCaptor.capture());

        final var savedTags = tagsCaptor.getValue();
        assertFalse(savedTags.isEmpty());
        assertEquals(2, savedTags.size());
        assertTrue(savedTags.stream().anyMatch(x -> TAG_CODE.equals(x.getTagCode())
                && x.getService().getServiceId() == 1L
                && x.getTagValue() == null));
        assertTrue(savedTags.stream().anyMatch(x -> TAG_CODE_2.equals(x.getTagCode())
                && x.getService().getServiceId() == 1L
                && x.getTagValue() == null));

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(1)).saveAll(activationsCaptor.capture());

        final var savedActivations = activationsCaptor.getValue();
        assertFalse(savedActivations.isEmpty());
        // Doing twice the same update, nothing to see here
        assertEquals(2, savedActivations.size());
        assertEquals(ACTIV_CODE, savedActivations.get(0).getActivCode());
        assertEquals((Long) 1L, savedActivations.get(0).getService().getServiceId());
        assertEquals((Long) 90L, savedActivations.get(0).getActivValue());
        assertEquals(ACTIV_CODE, savedActivations.get(1).getActivCode());
        assertEquals((Long) 1L, savedActivations.get(1).getService().getServiceId());
        assertEquals((Long) 90L, savedActivations.get(1).getActivValue());
    }

    @Test
    void changeTags_twoAdd_boosterWithTagValue() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE_2);
        tag2.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1, tag2));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        ProvisioningTag dbtag1 = getProvisioningTag();
        // Edit the activation code to make it quota-able (BOOSTER/BUCKET), and add a value on the tag
        dbtag1.getTagActivations().forEach(x -> {
            x.setTagValue(40L);
            x.getActivationCode().setNature(ActivationNature.BOOSTER);
        });

        ProvisioningTag dbtag2 = getProvisioningTag();
        dbtag2.setTagCode(TAG_CODE_2);
        // Edit the activation code to make it quota-able (BOOSTER/BUCKET), and add a value on the tag
        dbtag2.getTagActivations().forEach(x -> {
            x.setTagValue(30L);
            x.getActivationCode().setNature(ActivationNature.BOOSTER);
        });

        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE, TAG_CODE_2))).thenReturn(List.of(dbtag1, dbtag2));

        when(serviceActivationRepository.findByService(service)).thenReturn(Collections.emptyList());

        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(2, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());
        assertEquals(TAG_CODE_2, response.getServiceTags().get(1).getTagCode());

        assertEquals(1, response.getServiceActivations().size(), "Service activations to be performed includes the activation code to be persisted");
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) 70L, response.getServiceActivations().get(0).getActivValue(), "Active value should be the addition between provided '40' and '30' values");

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_persistent_sharedActivationCodesBetweenTwoTags_add() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        // This is a new activation code
        var newActivationCode = getActivationCode();
        newActivationCode.setActivCode(ACTIV_CODE_2);

        // This new tag brings two activation codes: one already present through another tag, and a new one
        ProvisioningTag tag = getProvisioningTag();
        tag.setPersistent(true);
        TagActivation tagActivation = new TagActivation();
        tagActivation.setProvisioningTag(tag);
        tagActivation.setActivationCode(newActivationCode);
        tag.getTagActivations().add(tagActivation);
        newActivationCode.setTagActivations(tag.getTagActivations());
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        // Another provisioning tag exists, that had been added before on the service and brought one common activation code
        ProvisioningTag tag2 = getProvisioningTag();
        tag2.setPersistent(true);
        tag2.setTagCode(TAG_CODE_2);

        // Service activation already exists in DB: this activation code has already been set for this service
        var serviceActivation = getServiceActivation(service, getActivationCode());
        when(serviceActivationRepository.findByService(service)).thenReturn(List.of(serviceActivation));

        // Only service tag in DB is the second tag, which is not the subject of this request
        var existingServiceTag = getServiceTag(service, tag2);
        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(Collections.singletonList(existingServiceTag));

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(1, response.getServiceActivations().size(), "Service activations to be performed should only include the new activation code");
        assertEquals(ACTIV_CODE_2, response.getServiceActivations().get(0).getActivCode().getCode(), "Service activations to be performed should include the new activation code");

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(1)).saveAll(tagsCaptor.capture());

        final var savedTags = tagsCaptor.getValue();
        assertFalse(savedTags.isEmpty());
        assertEquals(1, savedTags.size());
        final var serviceTag = savedTags.iterator().next();
        assertEquals((Long) 1L, serviceTag.getService().getServiceId());
        assertEquals(TAG_CODE, serviceTag.getTagCode());
        assertNull(serviceTag.getTagValue());

        verify(serviceActivationRepository, times(0)).deleteAll(any());

        verify(serviceActivationRepository, times(1)).saveAll(activationsCaptor.capture());

        final var savedActivations = activationsCaptor.getValue();
        assertFalse(savedActivations.isEmpty());
        assertEquals(1, savedActivations.size());
        assertEquals(ACTIV_CODE_2, savedActivations.get(0).getActivCode());
        assertEquals((Long) 1L, savedActivations.get(0).getService().getServiceId());
        assertEquals((Long) 1L, savedActivations.get(0).getActivValue());
    }
}
