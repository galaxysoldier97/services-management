package mc.monacotelecom.services.process;

import mc.monacotelecom.inventory.common.nls.EnableCommonNls;
import mc.monacotelecom.services.dto.TagActionDTO;
import mc.monacotelecom.services.dto.request.ChangeTagsDTO;
import mc.monacotelecom.services.entity.*;
import mc.monacotelecom.services.enums.*;
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
class ChangeTagsRemoveTests {

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
    private ArgumentCaptor<Set<ProvisioningTag>> tagsCaptor;

    @Captor
    private ArgumentCaptor<List<ServiceActivation>> savedActivationsCaptor;

    @Captor
    private ArgumentCaptor<Set<ServiceActivation>> deletedActivationsCaptor;

    @Test
    void changeTags_missingTagIfMultiple() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.REMOVE);

        tag2.setTagCode(TAG_CODE_2);
        tag2.setTagAction(TagAction.REMOVE);

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
    void changeTags_activityInconsistency_ACCESS() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.REMOVE);

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
        tag1.setTagAction(TagAction.REMOVE);

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
        tag1.setTagAction(TagAction.REMOVE);

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
        tag1.setTagAction(TagAction.REMOVE);

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
        tag1.setTagAction(TagAction.REMOVE);

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
    void changeTags_removeNotPersistentNotExisting_ACCESS() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        ProvisioningTag tag = getProvisioningTag();
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        when(serviceActivationRepository.findByService(service)).thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(0, response.getServiceTags().size());

        assertEquals(1, response.getServiceActivations().size());
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) 0L, response.getServiceActivations().get(0).getActivValue(), "Active value should be zero");

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_removeNotPersistentExisting_ACCESS() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        ProvisioningTag tag = getProvisioningTag();
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        // Service activation already exists in DB: this activation code has already been set for this service
        var serviceActivation = getServiceActivation(service, getActivationCode());
        serviceActivation.setActivValue(1L);
        when(serviceActivationRepository.findByService(service)).thenReturn(List.of(serviceActivation));

        // Service tag already exists in DB: this tag has already been set for this service
        var serviceTag = getServiceTag(service, tag);
        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(List.of(serviceTag));

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(0, response.getServiceTags().size());

        assertEquals(1, response.getServiceActivations().size());
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) (0L), response.getServiceActivations().get(0).getActivValue(), "Active value should be 0");

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_removeNotPersistentWithQuota_ACCESS() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.REMOVE);

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

        assertEquals(0, response.getServiceTags().size());

        assertEquals(1, response.getServiceActivations().size());
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) (0L), response.getServiceActivations().get(0).getActivValue(), "Active value should be 0");

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_removePersistentNotExisting_ACCESS() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        ProvisioningTag tag = getProvisioningTag();
        tag.setPersistent(true);
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        when(serviceActivationRepository.findByService(service)).thenReturn(Collections.emptyList());

        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(0, response.getServiceTags().size());

        assertEquals(1, response.getServiceActivations().size());
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) (0L), response.getServiceActivations().get(0).getActivValue(), "Active value should be 0");

        verify(serviceTagRepository, times(0)).saveAll(any());
        verify(serviceTagRepository, times(1)).deleteAllByServiceAndTags(eq(service), tagsCaptor.capture());
        final var deletedTags = tagsCaptor.getValue();
        assertFalse(deletedTags.isEmpty());
        assertEquals(1, deletedTags.size());
        final var deletedTag = deletedTags.iterator().next();
        assertEquals(TAG_CODE, deletedTag.getTagCode());

        verify(serviceActivationRepository, times(0)).saveAll(any());
        verify(serviceActivationRepository, times(1)).deleteAll(deletedActivationsCaptor.capture());

        final var deletedActivations = deletedActivationsCaptor.getValue();
        assertFalse(deletedActivations.isEmpty());
        assertEquals(1, deletedActivations.size());
        final var deletedActivation = deletedActivations.iterator().next();
        assertEquals(ACTIV_CODE, deletedActivation.getActivCode());
        assertEquals((Long) 1L, deletedActivation.getService().getServiceId());
        assertEquals((Long) (0L), deletedActivation.getActivValue());
    }

    @Test
    void changeTags_removePersistentExisting_ACCESS() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        ProvisioningTag tag = getProvisioningTag();
        tag.setPersistent(true);
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        // Service activation already exists in DB: this activation code has already been set for this service
        var serviceActivation = getServiceActivation(service, getActivationCode());
        serviceActivation.setActivValue(1L);
        when(serviceActivationRepository.findByService(service)).thenReturn(List.of(serviceActivation));

        // Service tag already exists in DB: this tag has already been set for this service
        var serviceTag = getServiceTag(service, tag);
        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(List.of(serviceTag));

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(0, response.getServiceTags().size());

        assertEquals(1, response.getServiceActivations().size());
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) (-1L), response.getServiceActivations().get(0).getActivValue(), "Active value should be -1 as it's persistent");

        verify(serviceTagRepository, times(0)).saveAll(any());
        verify(serviceTagRepository, times(1)).deleteAllByServiceAndTags(eq(service), tagsCaptor.capture());
        final var deletedTags = tagsCaptor.getValue();
        assertFalse(deletedTags.isEmpty());
        assertEquals(1, deletedTags.size());
        final var deletedTag = deletedTags.iterator().next();
        assertEquals(TAG_CODE, deletedTag.getTagCode());

        verify(serviceActivationRepository, times(0)).saveAll(any());
        verify(serviceActivationRepository, times(1)).deleteAll(deletedActivationsCaptor.capture());

        final var deletedActivations = deletedActivationsCaptor.getValue();
        assertFalse(deletedActivations.isEmpty());
        assertEquals(1, deletedActivations.size());
        final var deletedActivation = deletedActivations.iterator().next();
        assertEquals(ACTIV_CODE, deletedActivation.getActivCode());
        assertEquals((Long) 1L, deletedActivation.getService().getServiceId());
        assertEquals((Long) (-1L), deletedActivation.getActivValue());
    }

    @Test
    void changeTags_removePersistentExistingWithQuota_ACCESS() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.REMOVE);

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
        serviceActivation.setActivValue(30L);
        serviceActivation.getActivationCode().setNature(ActivationNature.BOOSTER);
        when(serviceActivationRepository.findByService(service)).thenReturn(List.of(serviceActivation));

        // Service tag already exists in DB: this tag has already been set for this service
        var serviceTag = getServiceTag(service, tag);
        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(List.of(serviceTag));

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(0, response.getServiceTags().size());

        assertEquals(1, response.getServiceActivations().size());
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) (-20L), response.getServiceActivations().get(0).getActivValue(), "Active value should be -20 (30-50) as it's the new value minus the old one");

        verify(serviceTagRepository, times(0)).saveAll(any());
        verify(serviceTagRepository, times(1)).deleteAllByServiceAndTags(eq(service), tagsCaptor.capture());
        final var deletedTags = tagsCaptor.getValue();
        assertFalse(deletedTags.isEmpty());
        assertEquals(1, deletedTags.size());
        final var deletedTag = deletedTags.iterator().next();
        assertEquals(TAG_CODE, deletedTag.getTagCode());

        verify(serviceActivationRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(1)).deleteAll(deletedActivationsCaptor.capture());

        final var deletedActivations = deletedActivationsCaptor.getValue();
        assertFalse(deletedActivations.isEmpty());
        assertEquals(1, deletedActivations.size());
        final var deletedActivation = deletedActivations.iterator().next();
        assertEquals(ACTIV_CODE, deletedActivation.getActivCode());
        assertEquals((Long) 1L, deletedActivation.getService().getServiceId());
        assertEquals((Long) (-20L), deletedActivation.getActivValue());
    }

    @Test
    void changeTags_removePersistentExistingWithQuotaNoCurrentValue_ACCESS() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        ProvisioningTag tag = getProvisioningTag();
        tag.setPersistent(true);
        // Edit the activation code to make it quota-able (BOOSTER/BUCKET), and add a value on the tag
        tag.getTagActivations().forEach(x -> {
            x.setTagValue(null);
            x.getActivationCode().setNature(ActivationNature.BOOSTER);
        });
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        // Service activation already exists in DB: this activation code has already been set for this service
        var serviceActivation = getServiceActivation(service, getActivationCode());
        serviceActivation.setActivValue(30L);
        serviceActivation.getActivationCode().setNature(ActivationNature.BOOSTER);
        when(serviceActivationRepository.findByService(service)).thenReturn(List.of(serviceActivation));

        // Service tag already exists in DB: this tag has already been set for this service
        var serviceTag = getServiceTag(service, tag);
        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(List.of(serviceTag));

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(0, response.getServiceTags().size());

        assertEquals(0, response.getServiceActivations().size());
        /* This seems like the expected behavior to me, but eh
        assertEquals(1, response.getServiceActivations().size());
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) (30L), response.getServiceActivations().get(0).getActivValue(), "Active value should be 30 as there is no current value and new value is 30");
         */

        verify(serviceTagRepository, times(0)).saveAll(any());
        verify(serviceTagRepository, times(1)).deleteAllByServiceAndTags(eq(service), tagsCaptor.capture());
        final var deletedTags = tagsCaptor.getValue();
        assertFalse(deletedTags.isEmpty());
        assertEquals(1, deletedTags.size());
        final var deletedTag = deletedTags.iterator().next();
        assertEquals(TAG_CODE, deletedTag.getTagCode());

        verify(serviceActivationRepository, times(0)).deleteAll(any());

        // This is kinda unexpected too
        verify(serviceActivationRepository, times(1)).saveAll(savedActivationsCaptor.capture());

        final var savedActivations = savedActivationsCaptor.getValue();
        assertFalse(savedActivations.isEmpty());
        assertEquals(1, savedActivations.size());
        assertEquals(ACTIV_CODE, savedActivations.get(0).getActivCode());
        assertEquals((Long) 1L, savedActivations.get(0).getService().getServiceId());
        assertEquals((Long) 30L, savedActivations.get(0).getActivValue());
    }

    @Test
    void changeTags_removePersistentExistingWithQuotaNoNewValue_ACCESS() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.REMOVE);

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
        serviceActivation.setActivValue(null);
        serviceActivation.getActivationCode().setNature(ActivationNature.BOOSTER);
        when(serviceActivationRepository.findByService(service)).thenReturn(List.of(serviceActivation));

        // Service tag already exists in DB: this tag has already been set for this service
        var serviceTag = getServiceTag(service, tag);
        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(List.of(serviceTag));

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(0, response.getServiceTags().size());

        assertEquals(1, response.getServiceActivations().size());
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) (-50L), response.getServiceActivations().get(0).getActivValue(), "Active value should be -50 as there is no new value and current value is 50");

        verify(serviceTagRepository, times(0)).saveAll(any());
        verify(serviceTagRepository, times(1)).deleteAllByServiceAndTags(eq(service), tagsCaptor.capture());
        final var deletedTags = tagsCaptor.getValue();
        assertFalse(deletedTags.isEmpty());
        assertEquals(1, deletedTags.size());
        final var deletedTag = deletedTags.iterator().next();
        assertEquals(TAG_CODE, deletedTag.getTagCode());

        verify(serviceActivationRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(1)).deleteAll(deletedActivationsCaptor.capture());

        final var deletedActivations = deletedActivationsCaptor.getValue();
        assertFalse(deletedActivations.isEmpty());
        assertEquals(1, deletedActivations.size());
        final var deletedActivation = deletedActivations.iterator().next();
        assertEquals(ACTIV_CODE, deletedActivation.getActivCode());
        assertEquals((Long) 1L, deletedActivation.getService().getServiceId());
        assertEquals((Long) (-50L), deletedActivation.getActivValue());
    }

    /**
     * Provisioning Tag TAG_CODE
     *  -> Activation Code ACTIV_CODE
     *  -> Activation Code ACTIV_CODE_2
     * Provisioning Tag TAG_CODE_2
     *  - Activation Code ACTIV_CODE
     * </p>
     * Current state of the service:
     *     Service Tag = TAG_CODE, TAG_CODE_2
     *     Service Activation = ACTIV_CODE, ACTIV_CODE_2
     * </p>
     * This test will remove TAG_CODE from the service, and assert that ACTIV_CODE stays in place without being returned
     */
    @Test
    void changeTags_sharedCodeShouldNotBeRemoved() {
        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        // This is the specific activation code of the tag being removed
        var activationCode2 = getActivationCode();
        activationCode2.setActivCode(ACTIV_CODE_2);

        ProvisioningTag tag = getProvisioningTag();
        tag.setPersistent(true);
        TagActivation tagActivation = new TagActivation();
        tagActivation.setProvisioningTag(tag);
        tagActivation.setActivationCode(activationCode2);
        tag.getTagActivations().add(tagActivation);
        activationCode2.setTagActivations(tag.getTagActivations());
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(tag));

        // Two service activations already exists in DB
        var serviceActivation = getServiceActivation(service, getActivationCode());
        var serviceActivation2 = getServiceActivation(service, activationCode2);
        when(serviceActivationRepository.findByService(service)).thenReturn(List.of(serviceActivation, serviceActivation2));

        // Two service tags already exists in DB
        var serviceTag = getServiceTag(service, tag);
        ProvisioningTag tag2 = getProvisioningTag();
        tag2.setTagCode(TAG_CODE_2);
        tag2.setPersistent(true);
        var serviceTag2 = getServiceTag(service, tag2);
        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(List.of(serviceTag, serviceTag2));

        service.setServiceTags(List.of(serviceTag, serviceTag2));

        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.REMOVE);
        dto.setChangeTags(List.of(tag1));

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(0, response.getServiceTags().size());

        assertEquals(1, response.getServiceActivations().size(), "Only one activation code is expected, as ACTIV_CODE should not be returned");
        assertEquals(ACTIV_CODE_2, response.getServiceActivations().get(0).getActivCode().getCode(), "ACTIV_CODE_2 should be returned");
        assertEquals((Long) (-1L), response.getServiceActivations().get(0).getActivValue(), "Active value should be -1 as it's persistent");

        verify(serviceTagRepository, times(0)).saveAll(any());
        verify(serviceTagRepository, times(1)).deleteAllByServiceAndTags(eq(service), tagsCaptor.capture());
        final var deletedTags = tagsCaptor.getValue();
        assertFalse(deletedTags.isEmpty());
        assertEquals(1, deletedTags.size());
        final var deletedTag = deletedTags.iterator().next();
        assertEquals(TAG_CODE, deletedTag.getTagCode());

        verify(serviceActivationRepository, times(0)).saveAll(any());
        verify(serviceActivationRepository, times(1)).deleteAll(deletedActivationsCaptor.capture());

        final var deletedActivations = deletedActivationsCaptor.getValue();
        assertFalse(deletedActivations.isEmpty());
        assertEquals(1, deletedActivations.size());
        final var deletedActivation = deletedActivations.iterator().next();
        assertEquals(ACTIV_CODE_2, deletedActivation.getActivCode());
        assertEquals((Long) 1L, deletedActivation.getService().getServiceId());
        assertEquals((Long) (-1L), deletedActivation.getActivValue());
    }
}
