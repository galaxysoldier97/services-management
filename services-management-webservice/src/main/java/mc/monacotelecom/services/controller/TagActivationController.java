package mc.monacotelecom.services.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mc.monacotelecom.services.dto.TagActivationCodeDTO;
import mc.monacotelecom.services.dto.request.AddTagActivationCodeDTO;
import mc.monacotelecom.services.dto.search.SearchTagActivationCodeDTO;
import mc.monacotelecom.services.entity.TagActivation;
import mc.monacotelecom.services.service.TagActivationService;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Links between Provisioning Tags and Activation Codes API")
@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("private/auth/tagactivationcodes")
public class TagActivationController {

    private final TagActivationService tagActivationService;

    @Operation(summary = "Add a link between a Provisioning Tag and an Activation Code")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagActivationCodeDTO add(@Valid @RequestBody AddTagActivationCodeDTO dto) {
        return tagActivationService.add(dto);
    }

    @Operation(summary = "Get all links between Provisioning Tags and Activation Codes")
    @GetMapping
    @PageableAsQueryParam
    public PagedModel<TagActivationCodeDTO> getAll(@Parameter(hidden = true) Pageable pageable,
                                                   @Parameter(hidden = true) PagedResourcesAssembler<TagActivation> assembler) {
        return tagActivationService.getAll(pageable, assembler);
    }

    @Operation(summary = "Search links between Provisioning Tags and Activation Codes")

    @GetMapping(value = "/search")
    @PageableAsQueryParam
    public PagedModel<TagActivationCodeDTO> searchTags(final SearchTagActivationCodeDTO searchTagActivationCodeDTO,
                                                       @Parameter(hidden = true) Pageable pageable,
                                                       @Parameter(hidden = true) PagedResourcesAssembler<TagActivation> assembler) {
        return tagActivationService.search(searchTagActivationCodeDTO, pageable, assembler);
    }

    @Operation(summary = "Delete the link between a Provisioning Tag and an Activation Code")
    @DeleteMapping("/{provisioningTagId}/{activationCodeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("provisioningTagId") Long provisioningTagId,
                       @PathVariable("activationCodeId") Long activationCodeId) {
        tagActivationService.delete(provisioningTagId, activationCodeId);
    }
}
