package mc.monacotelecom.services.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mc.monacotelecom.services.dto.ServicesStatsDto;
import mc.monacotelecom.services.service.StatsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@Tag(name = "Statistics API")
@RequestMapping("private/auth")
public class StatsController {

    private final StatsService statsService;

    @Operation(summary = "Collect statistics for the services dashboard", operationId = "servicesDashboard")
    @ApiResponse(responseCode = "201", description = "Statistics collected")
    @ApiResponse(responseCode = "500", description = "Internal error")
    @GetMapping("/servicesDashboard")
    @ResponseStatus(HttpStatus.CREATED)
    public ServicesStatsDto resourcesDashboard() {
        return statsService.resourcesDashboard();
    }
}
