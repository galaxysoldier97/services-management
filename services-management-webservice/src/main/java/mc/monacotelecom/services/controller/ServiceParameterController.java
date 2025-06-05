package mc.monacotelecom.services.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mc.monacotelecom.services.dto.ServiceParameterDTO;
import mc.monacotelecom.services.dto.request.AddOrUpdateServiceParameterDTO;
import mc.monacotelecom.services.dto.request.UpdateServiceParameterDTO;
import mc.monacotelecom.services.service.ServiceParameterService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Tag(name = "Service Parameters API")
@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("private/auth/services/{serviceId}/parameters")
public class ServiceParameterController {

    private final ServiceParameterService serviceParameterService;

    @Operation(summary = "Add new service parameters")
    @ApiResponse(responseCode = "201", description = "Service Parameter added")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public List<ServiceParameterDTO> addServiceParameters(@NotNull @PathVariable final long serviceId,
                                                          @RequestBody final List<AddOrUpdateServiceParameterDTO> serviceParameters) {
        return serviceParameterService.add(serviceId, serviceParameters);
    }

    @Operation(summary = "Deleting a service parameter identified by its technical parameter ID")
    @ApiResponse(responseCode = "204", description = "Service Parameter deleted")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteServiceParameter(@NotNull @PathVariable final long serviceId,
                                       @NotNull @PathVariable final long id) {
        serviceParameterService.delete(serviceId, id);
    }

    @Operation(summary = "Update a service parameter identified by its technical parameter ID")
    @ApiResponse(responseCode = "200", description = "Service Parameter modified")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PatchMapping("/{id}")
    public ServiceParameterDTO patchServiceParameter(@NotNull @PathVariable final long serviceId,
                                                     @NotNull @PathVariable final long id,
                                                     @RequestBody final UpdateServiceParameterDTO serviceParameterDTO) {
        return serviceParameterService.patch(serviceId, id, serviceParameterDTO);
    }

    @Operation(summary = "Update service parameters on a service")
    @ApiResponse(responseCode = "200", description = "Service Parameters modified")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @PatchMapping
    public List<ServiceParameterDTO> updateServiceParameters(@NotNull @PathVariable final long serviceId,
                                                             @Valid @RequestBody final List<AddOrUpdateServiceParameterDTO> serviceParameters) {
        return serviceParameterService.updateServiceParameters(serviceId, serviceParameters);
    }

    @Operation(summary = "Deleting all service parameter at a given id")
    @ApiResponse(responseCode = "204", description = "Services parameters deleted")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void deleteAllServiceParameter(@NotNull @PathVariable final long serviceId) {
        serviceParameterService.deleteAll(serviceId);
    }
}
