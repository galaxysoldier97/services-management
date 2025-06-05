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
import org.junit.jupiter.api.Disabled;
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
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE, classes = {
        ChangeTagsProcess.class,
})
@EnableCommonNls
@ActiveProfiles("test")
class ChangeTagsAddAndRemoveTests {

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
    private ArgumentCaptor<Set<ServiceTag>> serviceTagsCaptor;

    @Captor
    private ArgumentCaptor<Set<ProvisioningTag>> provisioningTagsCaptor;

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
        tag1.setTagAction(TagAction.ADD);

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
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE_2);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE);
        tag2.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1, tag2));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var dbtag1 = getProvisioningTag();
        var dbtag2 = getProvisioningTag();
        dbtag2.setTagCode(TAG_CODE_2);
        dbtag2.setActivity(ProvisioningTagActivity.MOBILE);
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE, TAG_CODE_2))).thenReturn(List.of(dbtag1, dbtag2));

        var e = assertThrows(SvcValidationException.class, () -> changeTagsProcess.changeTags(SERVICE_ID, dto));
        assertEquals("Provisioning tag 'TAG_CODE_2' has activity 'MOBILE' that does not match service activity 'INTERNET'", e.getMessage());

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_activityInconsistency_COMPONENT() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE_2);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE);
        tag2.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1, tag2));

        var service = getServiceComponent();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var dbtag1 = getProvisioningTag();
        var dbtag2 = getProvisioningTag();
        dbtag2.setTagCode(TAG_CODE_2);
        dbtag2.setActivity(ProvisioningTagActivity.MOBILE);
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE, TAG_CODE_2))).thenReturn(List.of(dbtag1, dbtag2));

        var e = assertThrows(SvcValidationException.class, () -> changeTagsProcess.changeTags(SERVICE_ID, dto));
        assertEquals("Provisioning tag 'TAG_CODE_2' has activity 'MOBILE' that does not match service activity 'INTERNET'", e.getMessage());

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_categoryInconsistency_ACCESS() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE_2);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE);
        tag2.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1, tag2));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var dbtag1 = getProvisioningTag();
        var dbtag2 = getProvisioningTag();
        dbtag2.setTagCode(TAG_CODE_2);
        dbtag2.setCategory(ServiceCategory.COMPONENT);
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE, TAG_CODE_2))).thenReturn(List.of(dbtag1, dbtag2));

        var e = assertThrows(SvcValidationException.class, () -> changeTagsProcess.changeTags(SERVICE_ID, dto));
        assertEquals("Provisioning tag 'TAG_CODE_2' has category 'COMPONENT' that does not match service category 'ACCESS'", e.getMessage());

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_categoryInconsistency_COMPONENT() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE_2);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE);
        tag2.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1, tag2));

        var service = getServiceComponent();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var dbtag1 = getProvisioningTag();
        var dbtag2 = getProvisioningTag();
        dbtag2.setTagCode(TAG_CODE_2);
        dbtag2.setCategory(ServiceCategory.ACCESS);
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE, TAG_CODE_2))).thenReturn(List.of(dbtag1, dbtag2));

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
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE_2);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE);
        tag2.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1, tag2));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var dbtag1 = getProvisioningTag();
        var dbtag2 = getProvisioningTag();
        dbtag2.setTagCode(TAG_CODE_2);
        dbtag2.setAccessType(Network.FTTH);
        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE, TAG_CODE_2))).thenReturn(List.of(dbtag1, dbtag2));

        var e = assertThrows(SvcValidationException.class, () -> changeTagsProcess.changeTags(SERVICE_ID, dto));
        assertEquals("Provisioning tag 'TAG_CODE_2' has access type 'FTTH' that does not match service access type 'DOCSIS'", e.getMessage());

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    // Test same tag with both add and remove
    @Test
    void changeTags_addAndRemove_sameTag() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE);
        tag2.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1, tag2));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var dbtag1 = getProvisioningTag();

        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(dbtag1));

        when(serviceActivationRepository.findByService(service)).thenReturn(Collections.emptyList());

        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(0, response.getServiceTags().size());
        assertEquals(0, response.getServiceActivations().size());;

        verify(serviceTagRepository, times(0)).saveAll(any());
        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(eq(service), any());

        verify(serviceActivationRepository, times(0)).saveAll(any());
        verify(serviceActivationRepository, times(0)).deleteAll(any());
    }

    // Test same tag with both add and remove
    @Test
    void changeTags_addAndRemove_sameTag_reverted() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.REMOVE);

        tag2.setTagCode(TAG_CODE);
        tag2.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1, tag2));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var dbtag1 = getProvisioningTag();

        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(dbtag1));

        when(serviceActivationRepository.findByService(service)).thenReturn(Collections.emptyList());

        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(0, response.getServiceTags().size());
        assertEquals(0, response.getServiceActivations().size());;

        verify(serviceTagRepository, times(0)).saveAll(any());
        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(eq(service), any());

        verify(serviceActivationRepository, times(0)).saveAll(any());
        verify(serviceActivationRepository, times(0)).deleteAll(any());
    }

    // Test one add and one different remove with different activation codes
    @Test
    void changeTags_addAndRemove_differentActivationCodes() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE_2);
        tag2.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1, tag2));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var dbtag1 = getProvisioningTag();

        var activationCode2 = getActivationCode();
        activationCode2.setActivCode(ACTIV_CODE_2);
        var dbtag2 = getProvisioningTag(activationCode2);
        dbtag2.setTagCode(TAG_CODE_2);

        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE, TAG_CODE_2))).thenReturn(List.of(dbtag1, dbtag2));

        when(serviceActivationRepository.findByService(service)).thenReturn(Collections.emptyList());

        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(2, response.getServiceActivations().size());
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) 1L, response.getServiceActivations().get(0).getActivValue(), "Active value should be 1");
        assertEquals(ACTIV_CODE_2, response.getServiceActivations().get(1).getActivCode().getCode());
        assertEquals((Long) 0L, response.getServiceActivations().get(1).getActivValue(), "Active value should be 0");

        verify(serviceTagRepository, times(0)).saveAll(any());
        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(eq(service), any());

        verify(serviceActivationRepository, times(0)).saveAll(any());
        verify(serviceActivationRepository, times(0)).deleteAll(any());
    }

    // Test one add and one different remove with different activation codes
    @Test
    void changeTags_addAndRemove_differentActivationCodes_reverted() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE_2);
        tag1.setTagAction(TagAction.REMOVE);

        tag2.setTagCode(TAG_CODE);
        tag2.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1, tag2));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var dbtag1 = getProvisioningTag();

        var activationCode2 = getActivationCode();
        activationCode2.setActivCode(ACTIV_CODE_2);
        var dbtag2 = getProvisioningTag(activationCode2);
        dbtag2.setTagCode(TAG_CODE_2);

        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE, TAG_CODE_2))).thenReturn(List.of(dbtag1, dbtag2));

        when(serviceActivationRepository.findByService(service)).thenReturn(Collections.emptyList());

        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(2, response.getServiceActivations().size());
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) 1L, response.getServiceActivations().get(0).getActivValue(), "Active value should be 1");
        assertEquals(ACTIV_CODE_2, response.getServiceActivations().get(1).getActivCode().getCode());
        assertEquals((Long) 0L, response.getServiceActivations().get(1).getActivValue(), "Active value should be 0");

        verify(serviceTagRepository, times(0)).saveAll(any());
        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(eq(service), any());

        verify(serviceActivationRepository, times(0)).saveAll(any());
        verify(serviceActivationRepository, times(0)).deleteAll(any());
    }

    // Test one add and one different remove with common activation codes
    @Disabled("Randomness")
    @Test
    void changeTags_addAndRemove_sameActivationCodes() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE_2);
        tag2.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1, tag2));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var dbtag1 = getProvisioningTag();
        var dbtag2 = getProvisioningTag();
        dbtag2.setTagCode(TAG_CODE_2);

        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE, TAG_CODE_2))).thenReturn(List.of(dbtag1, dbtag2));

        when(serviceActivationRepository.findByService(service)).thenReturn(Collections.emptyList());

        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(1, response.getServiceActivations().size());
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        // This is the part where randomness enters the game, it is 1 or 0 randomly
        assertEquals((Long) 1L, response.getServiceActivations().get(0).getActivValue(), "Active value should be 1");

        verify(serviceTagRepository, times(0)).saveAll(any());
        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(eq(service), any());

        verify(serviceActivationRepository, times(0)).saveAll(any());
        verify(serviceActivationRepository, times(0)).deleteAll(any());
    }

    // Test one add and one different remove with common activation codes
    @Disabled("Randomness")
    @Test
    void changeTags_addAndRemove_sameActivationCodes_reverted() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE_2);
        tag1.setTagAction(TagAction.REMOVE);

        tag2.setTagCode(TAG_CODE);
        tag2.setTagAction(TagAction.ADD);

        dto.setChangeTags(List.of(tag1, tag2));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var dbtag1 = getProvisioningTag();
        var dbtag2 = getProvisioningTag();
        dbtag2.setTagCode(TAG_CODE_2);

        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE, TAG_CODE_2))).thenReturn(List.of(dbtag1, dbtag2));

        when(serviceActivationRepository.findByService(service)).thenReturn(Collections.emptyList());

        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(1, response.getServiceActivations().size());
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        // This is the part where randomness enters the game, it is 1 or 0 randomly
        assertEquals((Long) 1L, response.getServiceActivations().get(0).getActivValue(), "Active value should be 1");

        verify(serviceTagRepository, times(0)).saveAll(any());
        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(eq(service), any());

        verify(serviceActivationRepository, times(0)).saveAll(any());
        verify(serviceActivationRepository, times(0)).deleteAll(any());
    }

    // Test same persistent tag with both add and remove, already in db
    @Test
    void changeTags_addAndRemove_persistent_sameTag_notExisting() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE);
        tag2.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1, tag2));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var dbtag1 = getProvisioningTag();
        dbtag1.setPersistent(true);

        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(dbtag1));

        when(serviceActivationRepository.findByService(service)).thenReturn(Collections.emptyList());

        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(0, response.getServiceTags().size());
        assertEquals(0, response.getServiceActivations().size());;

        verify(serviceTagRepository, times(0)).saveAll(any());
        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(eq(service), any());

        verify(serviceActivationRepository, times(0)).saveAll(any());
        verify(serviceActivationRepository, times(0)).deleteAll(any());
    }

    // Test same persistent tag with both add and remove, not existing in db
    @Test
    void changeTags_addAndRemove_persistent_sameTag_existing() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE);
        tag2.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1, tag2));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var dbtag1 = getProvisioningTag();
        dbtag1.setPersistent(true);

        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE))).thenReturn(List.of(dbtag1));

        // Service activation already exists in DB: this activation code has already been set for this service
        var serviceActivation = getServiceActivation(service, getActivationCode());
        serviceActivation.setActivValue(1L);
        when(serviceActivationRepository.findByService(service)).thenReturn(List.of(serviceActivation));

        // Service tag already exists in DB: this tag has already been set for this service
        var serviceTag = getServiceTag(service, dbtag1);
        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(List.of(serviceTag));

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(0, response.getServiceTags().size());
        assertEquals(0, response.getServiceActivations().size());;

        verify(serviceTagRepository, times(0)).saveAll(any());
        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(eq(service), any());

        verify(serviceActivationRepository, times(0)).saveAll(any());
        verify(serviceActivationRepository, times(0)).deleteAll(any());
    }

    // Test one add and one different remove with different activation codes (PERSISTENT)
    @Test
    void changeTags_addAndRemove_persistent_differentActivationCodes_existing() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE_2);
        tag2.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1, tag2));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var dbtag1 = getProvisioningTag();
        dbtag1.setPersistent(true);

        var activationCode2 = getActivationCode();
        activationCode2.setActivCode(ACTIV_CODE_2);
        var dbtag2 = getProvisioningTag(activationCode2);
        dbtag2.setTagCode(TAG_CODE_2);
        dbtag2.setPersistent(true);

        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE, TAG_CODE_2))).thenReturn(List.of(dbtag1, dbtag2));

        // Service activation already exists in DB: this activation code has already been set for this service
        var serviceActivation = getServiceActivation(service, getActivationCode());
        serviceActivation.setActivValue(1L);
        serviceActivation.setActivCode(ACTIV_CODE_2);
        serviceActivation.getActivationCode().setActivCode(ACTIV_CODE_2);
        when(serviceActivationRepository.findByService(service)).thenReturn(List.of(serviceActivation));

        // Service tag already exists in DB: this tag has already been set for this service
        var existingServiceTag = getServiceTag(service, dbtag2);
        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(List.of(existingServiceTag));

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(2, response.getServiceActivations().size());
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) 1L, response.getServiceActivations().get(0).getActivValue(), "Active value should be 1");
        assertEquals(ACTIV_CODE_2, response.getServiceActivations().get(1).getActivCode().getCode());
        assertEquals((Long) (-1L), response.getServiceActivations().get(1).getActivValue(), "Active value should be -1");

        verify(serviceTagRepository, times(1)).saveAll(serviceTagsCaptor.capture());

        final var savedTags = serviceTagsCaptor.getValue();
        assertFalse(savedTags.isEmpty());
        assertEquals(1, savedTags.size());
        final var serviceTag = savedTags.iterator().next();
        assertEquals((Long) 1L, serviceTag.getService().getServiceId());
        assertEquals(TAG_CODE, serviceTag.getProvisioningTag().getTagCode());
        assertNull(serviceTag.getTagValue());

        verify(serviceTagRepository, times(1)).deleteAllByServiceAndTags(eq(service), provisioningTagsCaptor.capture());
        final var deletedTags = provisioningTagsCaptor.getValue();
        assertFalse(deletedTags.isEmpty());
        assertEquals(1, deletedTags.size());
        final var deletedTag = deletedTags.iterator().next();
        assertEquals(TAG_CODE_2, deletedTag.getTagCode());

        verify(serviceActivationRepository, times(1)).saveAll(savedActivationsCaptor.capture());

        final var savedActivations = savedActivationsCaptor.getValue();
        assertFalse(savedActivations.isEmpty());
        assertEquals(1, savedActivations.size());
        assertEquals(ACTIV_CODE, savedActivations.get(0).getActivCode());
        assertEquals((Long) 1L, savedActivations.get(0).getService().getServiceId());
        assertEquals((Long) 1L, savedActivations.get(0).getActivValue());

        verify(serviceActivationRepository, times(1)).deleteAll(deletedActivationsCaptor.capture());

        final var deletedActivations = deletedActivationsCaptor.getValue();
        assertFalse(deletedActivations.isEmpty());
        assertEquals(1, deletedActivations.size());
        final var deletedActivation = deletedActivations.iterator().next();
        assertEquals(ACTIV_CODE_2, deletedActivation.getActivCode());
        assertEquals((Long) 1L, deletedActivation.getService().getServiceId());
        assertEquals((Long) (-1L), deletedActivation.getActivValue());
    }

    // Test one add and one different remove with different activation codes (PERSISTENT)
    @Test
    void changeTags_addAndRemove_persistent_differentActivationCodes_notExisting() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE_2);
        tag2.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1, tag2));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var dbtag1 = getProvisioningTag();
        dbtag1.setPersistent(true);

        var activationCode2 = getActivationCode();
        activationCode2.setActivCode(ACTIV_CODE_2);
        var dbtag2 = getProvisioningTag(activationCode2);
        dbtag2.setTagCode(TAG_CODE_2);
        dbtag2.setPersistent(true);

        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE, TAG_CODE_2))).thenReturn(List.of(dbtag1, dbtag2));

        when(serviceActivationRepository.findByService(service)).thenReturn(Collections.emptyList());

        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(2, response.getServiceActivations().size());
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) 1L, response.getServiceActivations().get(0).getActivValue(), "Active value should be 1");
        assertEquals(ACTIV_CODE_2, response.getServiceActivations().get(1).getActivCode().getCode());
        assertEquals((Long) (0L), response.getServiceActivations().get(1).getActivValue(), "Active value should be -1");

        verify(serviceTagRepository, times(1)).saveAll(serviceTagsCaptor.capture());

        final var savedTags = serviceTagsCaptor.getValue();
        assertFalse(savedTags.isEmpty());
        assertEquals(1, savedTags.size());
        final var serviceTag = savedTags.iterator().next();
        assertEquals((Long) 1L, serviceTag.getService().getServiceId());
        assertEquals(TAG_CODE, serviceTag.getTagCode());
        assertNull(serviceTag.getTagValue());

        verify(serviceTagRepository, times(1)).deleteAllByServiceAndTags(eq(service), provisioningTagsCaptor.capture());
        final var deletedTags = provisioningTagsCaptor.getValue();
        assertFalse(deletedTags.isEmpty());
        assertEquals(1, deletedTags.size());
        final var deletedTag = deletedTags.iterator().next();
        assertEquals(TAG_CODE_2, deletedTag.getTagCode());

        verify(serviceActivationRepository, times(1)).saveAll(savedActivationsCaptor.capture());

        final var savedActivations = savedActivationsCaptor.getValue();
        assertFalse(savedActivations.isEmpty());
        assertEquals(1, savedActivations.size());
        assertEquals(ACTIV_CODE, savedActivations.get(0).getActivCode());
        assertEquals((Long) 1L, savedActivations.get(0).getService().getServiceId());
        assertEquals((Long) 1L, savedActivations.get(0).getActivValue());

        verify(serviceActivationRepository, times(1)).deleteAll(deletedActivationsCaptor.capture());

        final var deletedActivations = deletedActivationsCaptor.getValue();
        assertFalse(deletedActivations.isEmpty());
        assertEquals(1, deletedActivations.size());
        final var deletedActivation = deletedActivations.iterator().next();
        assertEquals(ACTIV_CODE_2, deletedActivation.getActivCode());
        assertEquals((Long) 1L, deletedActivation.getService().getServiceId());
        assertEquals((Long) (0L), deletedActivation.getActivValue());
    }

    // Test one add and one different remove with common activation codes (PERSISTENT)
    // Ends up creating common activation codes
    @Test
    void changeTags_addAndRemove_persistent_sameActivationCodes_notExisting() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE_2);
        tag2.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1, tag2));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var dbtag1 = getProvisioningTag();
        dbtag1.setPersistent(true);

        var dbtag2 = getProvisioningTag();
        dbtag2.setTagCode(TAG_CODE_2);
        dbtag2.setPersistent(true);

        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE, TAG_CODE_2))).thenReturn(List.of(dbtag1, dbtag2));

        when(serviceActivationRepository.findByService(service)).thenReturn(Collections.emptyList());

        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(Collections.emptyList());

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(1, response.getServiceActivations().size());
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) 1L, response.getServiceActivations().get(0).getActivValue(), "Active value should be 1");

        verify(serviceTagRepository, times(1)).saveAll(serviceTagsCaptor.capture());

        final var savedTags = serviceTagsCaptor.getValue();
        assertFalse(savedTags.isEmpty());
        assertEquals(1, savedTags.size());
        final var serviceTag = savedTags.iterator().next();
        assertEquals((Long) 1L, serviceTag.getService().getServiceId());
        assertEquals(TAG_CODE, serviceTag.getTagCode());
        assertNull(serviceTag.getTagValue());

        verify(serviceTagRepository, times(1)).deleteAllByServiceAndTags(eq(service), provisioningTagsCaptor.capture());
        final var deletedTags = provisioningTagsCaptor.getValue();
        assertFalse(deletedTags.isEmpty());
        assertEquals(1, deletedTags.size());
        final var deletedTag = deletedTags.iterator().next();
        assertEquals(TAG_CODE_2, deletedTag.getTagCode());

        verify(serviceActivationRepository, times(1)).saveAll(savedActivationsCaptor.capture());

        final var savedActivations = savedActivationsCaptor.getValue();
        assertFalse(savedActivations.isEmpty());
        // Two times the same service activation ?
        assertEquals(2, savedActivations.size());
        assertEquals(ACTIV_CODE, savedActivations.get(0).getActivCode());
        assertEquals((Long) 1L, savedActivations.get(0).getService().getServiceId());
        assertEquals((Long) 1L, savedActivations.get(0).getActivValue());

        verify(serviceActivationRepository, times(0)).deleteAll(deletedActivationsCaptor.capture());
    }

    // Test one add and one different remove with common activation codes (PERSISTENT)
    // Ends up deleting common activation codes
    @Test
    void changeTags_addAndRemove_persistent_sameActivationCodes_existing() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE_2);
        tag2.setTagAction(TagAction.REMOVE);

        dto.setChangeTags(List.of(tag1, tag2));

        ServiceAccess service = getServiceAccess();
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        var dbtag1 = getProvisioningTag();
        dbtag1.setPersistent(true);

        var dbtag2 = getProvisioningTag();
        dbtag2.setTagCode(TAG_CODE_2);
        dbtag2.setPersistent(true);

        when(provisioningTagRepository.findByTagCodeIn(Set.of(TAG_CODE, TAG_CODE_2))).thenReturn(List.of(dbtag1, dbtag2));

        // Service activation already exists in DB: this activation code has already been set for this service
        var serviceActivation = getServiceActivation(service, getActivationCode());
        serviceActivation.setActivValue(1L);
        serviceActivation.setActivCode(ACTIV_CODE_2);
        serviceActivation.getActivationCode().setActivCode(ACTIV_CODE_2);
        when(serviceActivationRepository.findByService(service)).thenReturn(List.of(serviceActivation));

        // Service tag already exists in DB: this tag has already been set for this service
        var existingServiceTag = getServiceTag(service, dbtag2);
        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(List.of(existingServiceTag));

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(1, response.getServiceActivations().size());
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) (-1L), response.getServiceActivations().get(0).getActivValue(), "Active value should be 1");

        verify(serviceTagRepository, times(1)).saveAll(serviceTagsCaptor.capture());

        final var savedTags = serviceTagsCaptor.getValue();
        assertFalse(savedTags.isEmpty());
        assertEquals(1, savedTags.size());
        final var serviceTag = savedTags.iterator().next();
        assertEquals((Long) 1L, serviceTag.getService().getServiceId());
        assertEquals(TAG_CODE, serviceTag.getTagCode());
        assertNull(serviceTag.getTagValue());

        verify(serviceTagRepository, times(1)).deleteAllByServiceAndTags(eq(service), provisioningTagsCaptor.capture());
        final var deletedTags = provisioningTagsCaptor.getValue();
        assertFalse(deletedTags.isEmpty());
        assertEquals(1, deletedTags.size());
        final var deletedTag = deletedTags.iterator().next();
        assertEquals(TAG_CODE_2, deletedTag.getTagCode());

        verify(serviceActivationRepository, times(0)).saveAll(savedActivationsCaptor.capture());

        verify(serviceActivationRepository, times(1)).deleteAll(deletedActivationsCaptor.capture());
        final var deletedActivations = deletedActivationsCaptor.getValue();
        assertFalse(deletedActivations.isEmpty());
        assertEquals(1, deletedActivations.size());
        final var deletedActivation = deletedActivations.iterator().next();
        assertEquals(ACTIV_CODE, deletedActivation.getActivCode());
        assertEquals((Long) 1L, deletedActivation.getService().getServiceId());
        assertEquals((Long) (-1L), deletedActivation.getActivValue());
    }

    @Disabled("Randomness")
    @Test
    void changeTags_addAndRemove_boosterWithTagValue() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE_2);
        tag2.setTagAction(TagAction.REMOVE);

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

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(1, response.getServiceActivations().size(), "Service activations to be performed includes the activation code to be persisted");
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) 10L, response.getServiceActivations().get(0).getActivValue(), "Active value should be the addition between provided '40' and '-30' values");

        verify(serviceTagRepository, times(0)).deleteAllByServiceAndTags(any(), any());
        verify(serviceTagRepository, times(0)).saveAll(any());

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(0)).saveAll(any());
    }

    @Test
    void changeTags_addAndRemove_persistent_boosterWithTagValueAndExistingValue() {
        var dto = new ChangeTagsDTO();
        var tag1 = new TagActionDTO();
        var tag2 = new TagActionDTO();

        tag1.setTagCode(TAG_CODE);
        tag1.setTagAction(TagAction.ADD);

        tag2.setTagCode(TAG_CODE_2);
        tag2.setTagAction(TagAction.REMOVE);

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
        serviceActivation.setActivValue(30L);
        serviceActivation.getActivationCode().setNature(ActivationNature.BOOSTER);
        when(serviceActivationRepository.findByService(service)).thenReturn(List.of(serviceActivation));

        // Service tag already exists in DB: this tag has already been set for this service
        var serviceTag = getServiceTag(service, dbtag2);
        when(serviceTagRepository.findAllByServiceAndTags(eq(service), any(Collection.class)))
                .thenReturn(List.of(serviceTag));

        var response = changeTagsProcess.changeTags(SERVICE_ID, dto);

        assertEquals(1, response.getServiceTags().size());
        assertEquals(TAG_CODE, response.getServiceTags().get(0).getTagCode());

        assertEquals(1, response.getServiceActivations().size(), "Service activations to be performed includes the activation code to be persisted");
        assertEquals(ACTIV_CODE, response.getServiceActivations().get(0).getActivCode().getCode());
        assertEquals((Long) 40L, response.getServiceActivations().get(0).getActivValue(), "Active value should be the new activation code value, the old value has been removed");

        verify(serviceTagRepository, times(1)).saveAll(serviceTagsCaptor.capture());
        verify(serviceTagRepository, times(1)).deleteAllByServiceAndTags(eq(service), provisioningTagsCaptor.capture());
        final var deletedTags = provisioningTagsCaptor.getValue();
        assertFalse(deletedTags.isEmpty());
        assertEquals(1, deletedTags.size());
        final var deletedTag = deletedTags.iterator().next();
        assertEquals(TAG_CODE_2, deletedTag.getTagCode());

        final var savedTags = serviceTagsCaptor.getValue();
        assertFalse(savedTags.isEmpty());
        assertEquals(1, savedTags.size());
        assertTrue(savedTags.stream().anyMatch(x -> TAG_CODE.equals(x.getTagCode())
                && x.getService().getServiceId() == 1L
                && x.getTagValue() == null));

        verify(serviceActivationRepository, times(0)).deleteAll(any());
        verify(serviceActivationRepository, times(1)).saveAll(savedActivationsCaptor.capture());

        final var savedActivations = savedActivationsCaptor.getValue();
        assertFalse(savedActivations.isEmpty());
        // Doing twice the same update, nothing to see here
        assertEquals(2, savedActivations.size());
        assertEquals(ACTIV_CODE, savedActivations.get(0).getActivCode());
        assertEquals((Long) 1L, savedActivations.get(0).getService().getServiceId());
        assertEquals((Long) 40L, savedActivations.get(0).getActivValue());
    }
}
