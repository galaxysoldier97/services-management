package mc.monacotelecom.services.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mc.monacotelecom.services.dto.ProvisioningActionDTO;
import mc.monacotelecom.services.dto.ProvisioningActionParameterDTO;
import mc.monacotelecom.services.dto.request.CreateProvisioningActionDTO;
import mc.monacotelecom.services.dto.request.UpdateProvisioningActionDTO;
import mc.monacotelecom.services.service.ProvisioningActionParameterService;
import mc.monacotelecom.services.service.ProvisioningActionService;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@Tag(name = "Provisioning Action API")
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("private/auth/provisioningactions")
public class ProvisioningActionController {

    private final ProvisioningActionService provisioningActionService;
    private final ProvisioningActionParameterService provisioningActionParameterService;

    @Operation(summary = "Find a Provisioning Action by Id")
    @ApiResponse(responseCode = "302", description = "Provisioning Action found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @GetMapping("/{id}")
    public ProvisioningActionDTO getById(@PathVariable("id") Long id) {
        return provisioningActionService.getById(id);
    }

    @Operation(summary = "Get all Provisioning Actions")
    @GetMapping
    @PageableAsQueryParam
    public Page<ProvisioningActionDTO> getAll(@Parameter(hidden = true) Pageable pageable) {
        return provisioningActionService.getAll(pageable);
    }

    @Operation(summary = "Add a Provisioning Action")
    @ApiResponse(responseCode = "201", description = "Provisioning Action created")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PostMapping
    public ProvisioningActionDTO add(@Valid @RequestBody CreateProvisioningActionDTO dto) {
        provisioningActionService.add(dto);
        return provisioningActionService.get(dto.getTagId(), dto.getTagAction());
    }

    @Operation(summary = "Update a Provisioning Action")
    @ApiResponse(responseCode = "200", description = "Provisioning Action updated")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PutMapping("/{id}")
    public ProvisioningActionDTO update(@PathVariable("id") Long id,
                                        @Valid @RequestBody UpdateProvisioningActionDTO dto) {
        return provisioningActionService.update(id, dto);
    }

    @Operation(summary = "Delete a Provisioning Action")
    @ApiResponse(responseCode = "204", description = "Provisioning Action deleted")
    @ApiResponse(responseCode = "400", description = "User error, request is inconsistent")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long id,
                           @RequestParam(value = "force", required = false, defaultValue = "false") boolean force) {
        provisioningActionService.delete(id, force);
    }

    @Operation(summary = "Get a Provisioning Action parameter by ID")
    @GetMapping("{provisioningActionId}/parameters/{parameterId}")
    public ProvisioningActionParameterDTO getProvisioningActionParameterById(@PathVariable("provisioningActionId") Long provisioningActionId,
                                                                             @PathVariable("parameterId") Long parameterId) {
        return provisioningActionParameterService.getById(provisioningActionId, parameterId);
    }

    @Operation(summary = "Add a Provisioning Action Parameter")
    @ApiResponse(responseCode = "200", description = "Provisioning Action parameter created")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PostMapping("/{id}/parameters")
    public ProvisioningActionParameterDTO addProvisioningActionParameter(@PathVariable("id") Long id,
                                                                         @Valid @RequestBody ProvisioningActionParameterDTO dto) {
        return provisioningActionParameterService.add(id, dto);
    }

    @Operation(summary = "Update a Provisioning Action parameter")
    @ApiResponse(responseCode = "200", description = "Provisioning Action parameter updated")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PutMapping("/{provisioningActionId}/parameters/{parameterId}")
    public ProvisioningActionParameterDTO update(@PathVariable("provisioningActionId") Long provisioningActionId,
                                                 @PathVariable("parameterId") Long parameterId,
                                                 @Valid @RequestBody ProvisioningActionParameterDTO provisioningActionParameterDTO) {
        return provisioningActionParameterService.update(provisioningActionId, parameterId, provisioningActionParameterDTO);
    }

    @Operation(summary = "Delete a Provisioning Action parameter")
    @ApiResponse(responseCode = "204", description = "Provisioning Action parameter deleted")
    @ApiResponse(responseCode = "400", description = "User error, request is inconsistent")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @DeleteMapping("/{provisioningActionId}/parameters/{parameterId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProvisioningActionParameterById(@PathVariable("provisioningActionId") Long provisioningActionId,
                                                      @PathVariable("parameterId") Long parameterId) {
        provisioningActionParameterService.delete(provisioningActionId, parameterId);
    }

    @Operation(summary = "Get all Provisioning Actions for a Provisioning Tag")
    @GetMapping("/provisioningtag/{provisioningTagId}")
    @PageableAsQueryParam
    public Page<ProvisioningActionDTO> getAllProvisioningActionForProvisioningTag(@PathVariable("provisioningTagId") Long provisioningTagId,
                                                                                  @Parameter(hidden = true) Pageable pageable) {
        return provisioningActionService.getAllProvisioningActionForProvisioningTag(provisioningTagId, pageable);
    }
}
