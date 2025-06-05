package mc.monacotelecom.services.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mc.monacotelecom.services.dto.ParametersOnActionResponseDTO;
import mc.monacotelecom.services.dto.ProvisioningProductDTO;
import mc.monacotelecom.services.dto.ProvisioningTagDTO;
import mc.monacotelecom.services.dto.request.CreateProvisioningTagDTO;
import mc.monacotelecom.services.dto.request.DecompositionRequestDTO;
import mc.monacotelecom.services.dto.request.GetParametersOnActionDTO;
import mc.monacotelecom.services.dto.request.UpdateProvisioningTagDTO;
import mc.monacotelecom.services.dto.search.SearchProvisioningTagDTO;
import mc.monacotelecom.services.entity.ProvisioningTag;
import mc.monacotelecom.services.service.ProvisioningTagService;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Tag(name = "Provisioning Tag API")
@CrossOrigin
@RestController
@Validated
@RequestMapping("private/auth/provisioningtags")
public class ProvisioningTagController {

    private final ProvisioningTagService provisioningTagService;

    @Operation(summary = "Find a Provisioning Tag by ID")
    @ApiResponse(responseCode = "200", description = "Provisioning Tag found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @GetMapping("/{id}")
    public ProvisioningTagDTO getById(@PathVariable("id") Long id) {
        return provisioningTagService.getById(id);
    }

    @Operation(summary = "Get all Provisioning Tags")
    @GetMapping
    @PageableAsQueryParam
    public PagedModel<ProvisioningTagDTO> getAll(@Parameter(hidden = true) Pageable pageable, @Parameter(hidden = true) PagedResourcesAssembler<ProvisioningTag> assembler) {
        return provisioningTagService.getAll(pageable, assembler);
    }

    @Operation(summary = "Get all Provisioning Action parameters for a Provisioning Tag")
    @ApiResponse(responseCode = "200", description = "Provisioning Action parameters returned")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PostMapping("/parametersOnActions")
    public ParametersOnActionResponseDTO getAllParametersOnAction(@RequestBody GetParametersOnActionDTO dto) {
        return provisioningTagService.getAllParametersOnAction(dto);
    }

    @Operation(summary = "Search Provisioning Tags by several criteria,...")
    @GetMapping("/search")
    @PageableAsQueryParam
    public PagedModel<ProvisioningTagDTO> search(final SearchProvisioningTagDTO searchProvisioningTagDTO, @Parameter(hidden = true) Pageable pageable, @Parameter(hidden = true) PagedResourcesAssembler<ProvisioningTag> assembler) {
        return provisioningTagService.search(searchProvisioningTagDTO, pageable, assembler);
    }

    @Operation(summary = "Add a Provisioning Tag")
    @ApiResponse(responseCode = "201", description = "Provisioning Tag created")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProvisioningTagDTO add(@Valid @RequestBody CreateProvisioningTagDTO provisioningTagDTO) {
        provisioningTagService.add(provisioningTagDTO);
        return provisioningTagService.get(provisioningTagDTO.getTagCode());
    }

    @Operation(summary = "Update a Provisioning Tag")
    @ApiResponse(responseCode = "200", description = "Provisioning Tag updated")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PutMapping("/{id}")
    public ProvisioningTagDTO update(@PathVariable("id") Long id,
                                     @Valid @RequestBody UpdateProvisioningTagDTO dto) {
        return provisioningTagService.update(id, dto);
    }

    @Operation(summary = "Delete a Provisioning Tag")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id,
                       @RequestParam(value = "force", required = false, defaultValue = "false") boolean force) {
        provisioningTagService.delete(id, force);
    }

    @Operation(summary = "Decompose a products request into provisioning tags")
    @ApiResponse(responseCode = "200", description = "Products have been decomposed into corresponding tags")
    @ApiResponse(responseCode = "404", description = "No tags have been found")
    @PatchMapping(value = "/decomposeproductsrequest")
    public List<ProvisioningProductDTO> decomposeProductsRequest(@Valid @RequestBody DecompositionRequestDTO decompositionRequestDTO) {
        return provisioningTagService.decomposeProductsRequest(decompositionRequestDTO);
    }

    @Operation(summary = "Export provisioning tag as excel file", operationId = "export")
    @GetMapping("/export")
    public HttpEntity<ByteArrayResource> export() throws IOException {
        InputStreamResource file = new InputStreamResource(provisioningTagService.export());

        final String filename = "provisioningTags";
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".xlsx");

        return new HttpEntity<>(new ByteArrayResource(file.getInputStream().readAllBytes()), header);
    }

    @Operation(summary = "Retrieve provisioning tags not declared in the provisioning model but expected by existing services")
    @ApiResponse(responseCode = "200", description = "Missing provisioning tags returned")
    @GetMapping("/orphans")
    public List<String> getOrphans() {
        return provisioningTagService.getOrphans();
    }
}
