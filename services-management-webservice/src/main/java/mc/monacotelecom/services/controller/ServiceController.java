package mc.monacotelecom.services.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import mc.monacotelecom.services.service.ServiceService;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.Collection;
import java.util.List;

@Tag(name = "Service API")
@CrossOrigin
@RestController
@Validated
@RequestMapping("private/auth/services")
@RequiredArgsConstructor
@Slf4j
public class ServiceController {

    private final ServiceService serviceService;

    @Operation(summary = "Get service by internal ID")
    @ApiResponse(responseCode = "200", description = "Service found")
    @ApiResponse(responseCode = "404", description = "Service not found")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @GetMapping(value = "/{id}")
    public ServiceDTO getById(@PathVariable("id") Long id) {
        return serviceService.getById(id);
    }

    @Operation(summary = "Get service by ONT ID")
    @ApiResponse(responseCode = "200", description = "Service found")
    @ApiResponse(responseCode = "404", description = "Service not found")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @GetMapping(value = "/ontId/{ontId}")
    public ServiceDTO getByOntId(@PathVariable("ontId")
                                 @Pattern(regexp = ".*:[0-9]+-[0-9]+-[0-9]+-[0-9]+-[0-9]+", message = "ONT ID should follow this kind of format: BKR01:1-1-1-2-4")
                                 String ontId) {
        return serviceService.getByOntId(ontId);
    }

    @Operation(summary = "Get all services with ids inside a given list of crmIds")
    @ApiResponse(responseCode = "200", description = "Services found")
    @ApiResponse(responseCode = "404", description = "Services not found")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PostMapping(value = "/crmServiceIdsIn")
    public Page<ServiceAccessUnmProjection> getServiceAccessCrmIdIn(@RequestBody List<String> ids) {
        return new PageImpl<>(serviceService.getServiceCrmIdIn(ids));
    }

    @Operation(summary = "Create a new service")
    @ApiResponse(responseCode = "201", description = "Service created")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PutMapping
    public ServiceDTO addServiceOnSubscription(@Valid @RequestBody AddServiceRequestDTO addServiceRequest) {
        return serviceService.addServiceOnSubscription(addServiceRequest);
    }

    @Operation(summary = "Create and activate a new service")
    @ApiResponse(responseCode = "201", description = "Service created and activated")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PutMapping("/createandactivate")
    public ServiceDTO addServiceAndActivate(@Valid @RequestBody AddServiceRequestDTO addServiceRequest) {
        return serviceService.addServiceAndActivate(addServiceRequest);
    }

    @Operation(summary = "Update an existing service")
    @ApiResponse(responseCode = "200", description = "Service updated")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PatchMapping("/{id}")
    public ServiceDTO updateService(@PathVariable("id") Long id,
                                    @Valid @RequestBody UpdateServiceDTO dto) {
        return serviceService.update(id, dto);
    }

    @Operation(summary = "Add a request on a service")
    @ApiResponse(responseCode = "200", description = "Service updated")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PatchMapping(value = "/addrequest")
    public ServiceDTO addRequestOnService(@Valid @RequestBody ServiceRequestDTO serviceRequest) {
        return serviceService.addRequest(serviceRequest);
    }

    @Operation(summary = "Activate an existing request on a service")
    @PatchMapping(value = "/activaterequest")
    public ServiceDTO activateRequestOnService(@Valid @RequestBody SearchServiceDTO requestDTO) {
        return serviceService.activateRequest(requestDTO);
    }

    @Operation(summary = "Cancel an activation request on a service")
    @PatchMapping(value = "/cancelrequest")
    public ServiceDTO cancelRequestOnService(@Valid @RequestBody SearchServiceDTO requestDTO) {
        return serviceService.cancelActivationRequest(requestDTO);
    }

    @Operation(summary = "Change tags on a service")
    @PatchMapping("/{id}/changetags")
    @ApiResponse(responseCode = "200", description = "Operation successful")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "404", description = "Service not found")
    @ApiResponse(responseCode = "500", description = "Internal error")
    public ChangeTagsResponse changeTags(@PathVariable("id") Long id, @RequestBody @Valid ChangeTagsDTO changeTags) {
        return serviceService.changeTags(id, changeTags);
    }

    @Operation(summary = "Get all services")
    @GetMapping
    @PageableAsQueryParam
    public PagedModel<ServiceDTO> getAll(@Parameter(hidden = true) Pageable pageable, @Parameter(hidden = true) PagedResourcesAssembler<Service> assembler) {
        return serviceService.getAll(pageable, assembler);
    }

    @Operation(summary = "Apply an event on a service")
    @PatchMapping(value = "/setactiononservice")
    public ServiceDTO setEventOnService(@Valid @RequestBody ServiceActionRequestDTO serviceActionRequestDTO) {
        return serviceService.setEventOnService(serviceActionRequestDTO);
    }

    @Operation(summary = "Delete a service")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteService(@PathVariable("id") long id,
                              @RequestParam(value = "forced", required = false) boolean isForced) {
        serviceService.delete(id, isForced);
    }

    @Operation(summary = "Delete all tags on a service")
    @DeleteMapping(value = "/{id}/tags")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllTagsOnService(@PathVariable("id") Long id) {
        serviceService.deleteAllTagsOnService(id);
    }

    @Operation(summary = "Find available events on a service")
    @GetMapping(value = "/{id}/events")
    public Collection<Event> findEventsForState(@PathVariable("id") Long id) {
        return serviceService.findEventsForState(id);
    }

    @Operation(summary = "Rollback the addition of a request on a service")
    @PatchMapping(value = "/rollbackaddrequest")
    public ServiceDTO rollbackAddRequestOnService(@RequestBody @Valid SearchServiceDTO requestDTO) {
        return serviceService.rollbackAddRequest(requestDTO);
    }

    @Operation(summary = "Find service revisions by ID")
    @ApiResponse(responseCode = "200", description = "Revisions page")
    @GetMapping(value = "/{id}/revisions")
    @PageableAsQueryParam
    public PagedModel<RevisionDTO<ServiceDTO>> getServiceRevisionsById(@PathVariable("id") Long id,
                                                                       @Parameter(hidden = true) Pageable pageable,
                                                                       @Parameter(hidden = true) PagedResourcesAssembler<Revision<Integer, Service>> assembler) {
        return serviceService.getServiceRevisionsById(id, pageable, assembler);
    }

    @Operation(summary = "Search a service by several criteria")
    @GetMapping("/search")
    @PageableAsQueryParam
    public PagedModel<ServiceDTO> search(@Valid SearchServiceDTO searchServiceDTO, @Parameter(hidden = true) Pageable pageable, @Parameter(hidden = true) PagedResourcesAssembler<Service> assembler) {
        return serviceService.search(searchServiceDTO, pageable, assembler);
    }

    @Operation(summary = "Search a serviceAccess by several criteria")
    @GetMapping("search/accesses")
    @PageableAsQueryParam
    public Page<ServiceAccessDTO> searchServiceAccess(@Valid SearchServiceAccessDTO searchServiceAccessDTO, @Parameter(hidden = true) Pageable pageable) {
        return serviceService.searchServiceAccess(searchServiceAccessDTO, pageable);
    }

    @Operation(summary = "Search a serviceComponent by several criteria")
    @GetMapping("search/components")
    @PageableAsQueryParam
    public Page<ServiceComponentDTO> searchServiceAccess(@Valid SearchServiceComponentDTO searchServiceComponentDTO, @Parameter(hidden = true) Pageable pageable) {
        return serviceService.searchServiceComponent(searchServiceComponentDTO, pageable);
    }

    @Operation(summary = "Retrieve services Internal IDs referring to activation codes / provisioning tags not declared in the provisioning model")
    @ApiResponse(responseCode = "200", description = "Orphan services returned")
    @GetMapping("/orphans")
    public List<Long> getOrphans() {
        return serviceService.getOrphans();
    }
}
