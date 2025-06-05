package mc.monacotelecom.services.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mc.monacotelecom.services.dto.TechnicalParameterDTO;
import mc.monacotelecom.services.dto.request.UpdateTechnicalParameterDTO;
import mc.monacotelecom.services.dto.search.SearchTechnicalParamDTO;
import mc.monacotelecom.services.entity.TechnicalParameter;
import mc.monacotelecom.services.service.TechnicalParameterService;
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

@RequiredArgsConstructor
@Tag(name = "Technical Parameter API")
@CrossOrigin
@RestController
@Validated
@RequestMapping("private/auth/technicalparameters")
public class TechnicalParameterController {

    private final TechnicalParameterService technicalParameterService;

    @Operation(summary = "Get a Technical Parameter")
    @ApiResponse(responseCode = "200", description = "Technical Parameter found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @GetMapping("/{id}")
    public TechnicalParameterDTO get(@PathVariable("id") Long id) {
        return technicalParameterService.getByInternalId(id);
    }

    @Operation(summary = "Add a Technical Parameter")
    @ApiResponse(responseCode = "201", description = "Technical Parameter created")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TechnicalParameterDTO add(@Valid @RequestBody TechnicalParameterDTO dto) {
        technicalParameterService.add(dto);
        return technicalParameterService.get(dto.getParameterCode(), dto.getParameterType());
    }

    @Operation(summary = "Update a Technical Parameter")
    @ApiResponse(responseCode = "200", description = "Technical Parameter updated")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PutMapping("/{id}")
    public TechnicalParameterDTO update(@PathVariable("id") Long id,
                                        @Valid @RequestBody UpdateTechnicalParameterDTO dto) {
        return technicalParameterService.update(id, dto);
    }

    @Operation(summary = "Get all Technical Parameters")
    @GetMapping
    @PageableAsQueryParam
    public PagedModel<TechnicalParameterDTO> getAll(@Parameter(hidden = true) Pageable pageable,
                                                    @Parameter(hidden = true) PagedResourcesAssembler<TechnicalParameter> assembler) {
        return technicalParameterService.getAll(pageable, assembler);
    }

    @Operation(summary = "Delete a Technical Parameter")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        technicalParameterService.delete(id);
    }

    @Operation(summary = "Search Technical Parameters by several criteria")

    @GetMapping("/search")
    @PageableAsQueryParam
    public PagedModel<TechnicalParameterDTO> search(final SearchTechnicalParamDTO searchTechnicalParamDTO,
                                                    @Parameter(hidden = true) Pageable pageable,
                                                    @Parameter(hidden = true) PagedResourcesAssembler<TechnicalParameter> assembler) {
        return technicalParameterService.search(searchTechnicalParamDTO, pageable, assembler);
    }

    @Operation(summary = "Export technical parameters as excel file", operationId = "export")
    @GetMapping("/export")
    public HttpEntity<ByteArrayResource> export() throws IOException {
        InputStreamResource file = new InputStreamResource(technicalParameterService.export());

        final String filename = "technicalParameters";
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".xlsx");

        return new HttpEntity<>(new ByteArrayResource(file.getInputStream().readAllBytes()), header);
    }
}