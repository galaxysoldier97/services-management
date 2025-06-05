package mc.monacotelecom.services.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mc.monacotelecom.services.dto.ActivationCodeDTO;
import mc.monacotelecom.services.dto.ServiceActivationDTO;
import mc.monacotelecom.services.dto.request.CreateActivationCodeDTO;
import mc.monacotelecom.services.dto.request.UpdateActivationCodeDTO;
import mc.monacotelecom.services.dto.search.SearchActivationCodeDTO;
import mc.monacotelecom.services.entity.ActivationCode;
import mc.monacotelecom.services.entity.ServiceActivation;
import mc.monacotelecom.services.service.ActivationCodeService;
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
@Tag(name = "Activation Code API")
@CrossOrigin
@RestController
@Validated
@RequestMapping("private/auth/activationcodes")
public class ActivationCodeController {

    private final ActivationCodeService activationCodeService;

    @Operation(summary = "Get all Activation Codes")
    @GetMapping
    @PageableAsQueryParam
    public PagedModel<ActivationCodeDTO> getAll(@Parameter(hidden = true) Pageable pageable, @Parameter(hidden = true) PagedResourcesAssembler<ActivationCode> assembler) {
        return activationCodeService.getAll(pageable, assembler);
    }

    @Operation(summary = "Get all Activation Codes for a Technical ID (techId)")
    @GetMapping("/techid/{techId}/{accessType}")
    @PageableAsQueryParam
    public PagedModel<ServiceActivationDTO> getAllActivationCodesByTechId(@PathVariable("techId") String techId,
                                                                          @PathVariable("accessType") String accessType,
                                                                          @Parameter(hidden = true) Pageable pageable,
                                                                          @Parameter(hidden = true) PagedResourcesAssembler<ServiceActivation> assembler) {
        return activationCodeService.getAllActivationCodesByTechId(techId, accessType, pageable, assembler);
    }

    @Operation(summary = "Get all Activation Codes for an Access Type")
    @GetMapping("/accessType/{accessType}")
    @PageableAsQueryParam
    public PagedModel<ServiceActivationDTO> getAllActivationCodesByAccessType(@PathVariable("accessType") String accessType,
                                                                              @Parameter(hidden = true) Pageable pageable,
                                                                              @Parameter(hidden = true) PagedResourcesAssembler<ServiceActivation> assembler) {
        return activationCodeService.getAllActivationCodesByAccessType(accessType, pageable, assembler);
    }

    @Operation(summary = "Search Activation Codes by several criteria")
    @GetMapping("/search")
    @PageableAsQueryParam
    public PagedModel<ActivationCodeDTO> search(final SearchActivationCodeDTO searchActivationCodeDTO,
                                                @Parameter(hidden = true) Pageable pageable,
                                                @Parameter(hidden = true) PagedResourcesAssembler<ActivationCode> assembler) {
        return activationCodeService.search(searchActivationCodeDTO, pageable, assembler);
    }

    @Operation(summary = "Find an Activation Code")
    @ApiResponse(responseCode = "200", description = "Activation Code found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @GetMapping("/{id}")
    public ActivationCodeDTO getById(@PathVariable("id") Long id) {
        return activationCodeService.getByInternalId(id);
    }

    @Operation(summary = "Create an Activation Code")
    @ApiResponse(responseCode = "201", description = "Activation Code created")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActivationCodeDTO add(@Valid @RequestBody CreateActivationCodeDTO dto) {
        activationCodeService.add(dto);
        return activationCodeService.get(dto.getCode());
    }

    @Operation(summary = "Update an Activation Code")
    @ApiResponse(responseCode = "200", description = "Activation Code updated")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PutMapping("/{id}")
    public ActivationCodeDTO update(@PathVariable("id") Long id,
                                    @Valid @RequestBody UpdateActivationCodeDTO dto) {
        return activationCodeService.update(id, dto);
    }

    @Operation(summary = "Delete an Activation Code")
    @ApiResponse(responseCode = "204", description = "Activation Code deleted")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id,
                       @RequestParam(value = "force", required = false, defaultValue = "false") boolean force) {
        activationCodeService.delete(id, force);
    }

    @Operation(summary = "Export activation Codes excel file", operationId = "export")
    @GetMapping("/export")
    public HttpEntity<ByteArrayResource> export() throws IOException {
        InputStreamResource file = new InputStreamResource(activationCodeService.export());

        final String filename = "activationCode";
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".xlsx");

        return new HttpEntity<>(new ByteArrayResource(file.getInputStream().readAllBytes()), header);
    }

    @Operation(summary = "Retrieve activation responseCodes not declared in the provisioning model but expected by existing services")
    @ApiResponse(responseCode = "200", description = "Missing activation responseCodes returned")
    @GetMapping("/orphans")
    public List<String> getOrphans() {
        return activationCodeService.getOrphans();
    }
}
