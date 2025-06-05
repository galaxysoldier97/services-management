package mc.monacotelecom.services.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mc.monacotelecom.services.dto.ProvisioningProductDTO;
import mc.monacotelecom.services.dto.request.CreateProvisioningProductDTO;
import mc.monacotelecom.services.dto.request.UpdateProvisioningProductDTO;
import mc.monacotelecom.services.dto.search.SearchProvisioningProductDTO;
import mc.monacotelecom.services.service.ProvisioningProductService;
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

@Tag(name = "Provisioning Product API")
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("private/auth/provisioningactions")
public class ProvisioningProductController {
    private final ProvisioningProductService provisioningProductService;

    @Operation(summary = "Find a Provisioning Product by ID")
    @ApiResponse(responseCode = "302", description = "Provisioning Product found")
    @ApiResponse(responseCode = "404", description = "Not found")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @GetMapping("/{actionId}/provisioningsproduct/{productId}")
    public ProvisioningProductDTO getProvisioningProductById(@PathVariable("actionId") Long actionId,
                                                             @PathVariable("productId") Long productId) {
        return provisioningProductService.getById(actionId, productId);
    }

    @Operation(summary = "Search Provisioning Products")
    @GetMapping("/provisioningsproduct")
    @PageableAsQueryParam
    public Page<ProvisioningProductDTO> search(@Parameter(hidden = true) Pageable pageable, SearchProvisioningProductDTO dto) {
        return provisioningProductService.search(pageable, dto);
    }

    @Operation(summary = "Get all Provisioning Products for a Provisioning Action ID")
    @GetMapping("/{actionId}/provisioningsproduct")
    @PageableAsQueryParam
    public Page<ProvisioningProductDTO> getAllProvisioningProductsForProvisioningAction(@PathVariable("actionId") Long actionId,
                                                                                        @Parameter(hidden = true) Pageable pageable) {
        return provisioningProductService.getAllProvisioningProductsForProvisioningAction(actionId, pageable);
    }

    @Operation(summary = "Add a Provisioning Product")
    @ApiResponse(responseCode = "201", description = "Provisioning Product created")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PostMapping("/{actionId}/provisioningsproduct")
    public ProvisioningProductDTO add(@PathVariable("actionId") Long actionId,
                                      @Valid @RequestBody CreateProvisioningProductDTO dto) {
        var tempProduct = provisioningProductService.add(actionId, dto);
        return provisioningProductService.get(tempProduct.getProductCode(),
                tempProduct.getProductAction(),
                tempProduct.getProvAction().getProvisioningTag().getTagCode(),
                tempProduct.getProvAction().getTagAction());
    }

    @Operation(summary = "Update a Provisioning Product")
    @ApiResponse(responseCode = "200", description = "Provisioning Product updated")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PutMapping("/{actionId}/provisioningsproduct/{productId}")
    public ProvisioningProductDTO update(@PathVariable("actionId") Long actionId,
                                         @PathVariable("productId") Long productId,
                                         @Valid @RequestBody UpdateProvisioningProductDTO dto) {
        return provisioningProductService.update(actionId, productId, dto);
    }

    @Operation(summary = "Export provisioning product as excel file")
    @GetMapping("/provisioningsproduct/export")
    public HttpEntity<ByteArrayResource> export() throws IOException {
        InputStreamResource file = new InputStreamResource(provisioningProductService.export());

        final String filename = "provisioningProducts";
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".xlsx");

        return new HttpEntity<>(new ByteArrayResource(file.getInputStream().readAllBytes()), header);
    }

    @Operation(summary = "Delete a Provisioning Product")
    @DeleteMapping("/{actionId}/provisioningsproduct/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProvisioningProductById(@PathVariable("actionId") Long actionId,
                                              @PathVariable("productId") Long productId) {
        provisioningProductService.delete(actionId, productId);
    }
}
