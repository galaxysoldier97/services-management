package mc.monacotelecom.services.service;

import lombok.RequiredArgsConstructor;
import mc.monacotelecom.services.dto.*;
import mc.monacotelecom.services.dto.request.AddServiceRequestDTO;
import mc.monacotelecom.services.dto.request.ChangeTagsDTO;
import mc.monacotelecom.services.dto.request.ServiceActionRequestDTO;
import mc.monacotelecom.services.dto.request.UpdateServiceDTO;
import mc.monacotelecom.services.dto.search.SearchServiceAccessDTO;
import mc.monacotelecom.services.dto.search.SearchServiceComponentDTO;
import mc.monacotelecom.services.dto.search.SearchServiceDTO;
import mc.monacotelecom.services.entity.Service;
import mc.monacotelecom.services.entity.projection.ServiceAccessUnmProjection;
import mc.monacotelecom.services.enums.Event;
import mc.monacotelecom.services.process.ChangeTagsProcess;
import mc.monacotelecom.services.process.ServiceProcess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceProcess serviceProcess;
    private final ChangeTagsProcess changeTagsProcess;

    @Transactional
    public ServiceDTO addRequest(ServiceRequestDTO serviceRequest) {
        return serviceProcess.addRequest(serviceRequest);
    }

    @Transactional
    public ServiceDTO activateRequest(SearchServiceDTO requestDTO) {
        return serviceProcess.activateRequest(requestDTO);
    }

    @Transactional
    public void deleteAllTagsOnService(Long serviceId) {
        serviceProcess.deleteAllTagsOnService(serviceId);
    }

    @Transactional
    public ServiceDTO cancelActivationRequest(SearchServiceDTO requestDTO) {
        return serviceProcess.cancelActivationRequest(requestDTO);
    }

    @Transactional
    public ChangeTagsResponse changeTags(Long serviceId, ChangeTagsDTO changeTags) {
        return changeTagsProcess.changeTags(serviceId, changeTags);
    }

    @Transactional
    public ServiceDTO rollbackAddRequest(SearchServiceDTO requestDTO) {
        return serviceProcess.rollBackAddRequest(requestDTO);
    }

    @Transactional
    public ServiceDTO addServiceOnSubscription(AddServiceRequestDTO addServiceRequest) {
        return serviceProcess.addServiceOnSubscription(addServiceRequest);
    }

    @Transactional(readOnly = true)
    public ServiceDTO getById(Long serviceId) {
        return serviceProcess.getById(serviceId);
    }

    @Transactional(readOnly = true)
    public List<ServiceAccessUnmProjection> getServiceCrmIdIn(List<String> crmServiceIds) {
        return serviceProcess.getServicesWithCrmIdIn(crmServiceIds);
    }

    @Transactional(readOnly = true)
    public ServiceDTO getByOntId(final String ontId) {
        return serviceProcess.getByOntId(ontId);
    }

    @Transactional
    public ServiceDTO update(Long serviceId, UpdateServiceDTO updateServiceRequest) {
        return serviceProcess.update(serviceId, updateServiceRequest);
    }

    @Transactional(readOnly = true)
    public PagedModel<ServiceDTO> search(SearchServiceDTO searchServiceDTO, Pageable pageable, PagedResourcesAssembler<Service> assembler) {
        return serviceProcess.search(searchServiceDTO, pageable, assembler);
    }

    @Transactional(readOnly = true)
    public Page<ServiceAccessDTO> searchServiceAccess(SearchServiceAccessDTO searchServiceDTO, Pageable pageable) {
        return serviceProcess.searchServiceAccess(searchServiceDTO, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ServiceComponentDTO> searchServiceComponent(SearchServiceComponentDTO searchServiceDTO, Pageable pageable) {
        return serviceProcess.searchServiceComponent(searchServiceDTO, pageable);
    }

    @Transactional(readOnly = true)
    public PagedModel<ServiceDTO> getAll(Pageable pageable, PagedResourcesAssembler<Service> assembler) {
        return serviceProcess.getAll(pageable, assembler);
    }

    @Transactional(readOnly = true)
    public PagedModel<RevisionDTO<ServiceDTO>> getServiceRevisionsById(Long serviceId, Pageable pageable, PagedResourcesAssembler<Revision<Integer, Service>> assembler) {
        return serviceProcess.getServiceRevisionsById(serviceId, pageable, assembler);
    }

    @Transactional
    public ServiceDTO setEventOnService(ServiceActionRequestDTO serviceActionRequestDTO) {
        return serviceProcess.setEventOnService(serviceActionRequestDTO);
    }

    @Transactional
    public void delete(final long serviceId, final boolean isForced) {
        serviceProcess.delete(serviceId, isForced);
    }

    @Transactional(readOnly = true)
    public Collection<Event> findEventsForState(Long serviceId) {
        return serviceProcess.findEventsForState(serviceId);
    }

    @Transactional(readOnly = true)
    public List<Long> getOrphans() {
        return serviceProcess.getOrphans();
    }

}
