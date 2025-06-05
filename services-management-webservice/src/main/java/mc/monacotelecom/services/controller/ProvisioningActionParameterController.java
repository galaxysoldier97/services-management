package mc.monacotelecom.services.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mc.monacotelecom.services.dto.ProvisioningActionParameterDetailsDTO;
import mc.monacotelecom.services.dto.request.AddProvisioningActionParameterDTO;
import mc.monacotelecom.services.dto.search.SearchProvisioningActionDTO;
import mc.monacotelecom.services.entity.ProvisioningActionParameter;
import mc.monacotelecom.services.service.ProvisioningActionParameterService;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Provisioning Action Parameters API")
@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("private/auth/tagparametervalues")
public class ProvisioningActionParameterController {

    private final ProvisioningActionParameterService provisioningActionParameterService;

    @Operation(summary = "Add Provisioning Action Parameter")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProvisioningActionParameterDetailsDTO add(@Valid @RequestBody AddProvisioningActionParameterDTO dto) {
        return provisioningActionParameterService.add(dto);
    }

    @Operation(summary = "Get all Provisioning Action Parameters")
    @GetMapping
    @PageableAsQueryParam
    public PagedModel<ProvisioningActionParameterDetailsDTO> getAll(@Parameter(hidden = true) Pageable pageable,
                                                                    @Parameter(hidden = true) PagedResourcesAssembler<ProvisioningActionParameter> assembler) {
        return provisioningActionParameterService.getAll(pageable, assembler);
    }

    @Operation(summary = "Search Provisioning Action Parameters by several criteria")
    @GetMapping(value = "/search")
    @PageableAsQueryParam
    public PagedModel<ProvisioningActionParameterDetailsDTO> searchTags(SearchProvisioningActionDTO searchProvisioningActionDTO,
                                                                        @Parameter(hidden = true) Pageable pageable,
                                                                        @Parameter(hidden = true) PagedResourcesAssembler<ProvisioningActionParameter> assembler) {
        return provisioningActionParameterService.search(searchProvisioningActionDTO, pageable, assembler);
    }
}
