package mc.monacotelecom.services.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ServiceActivity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(name = "Enumeration API")
@CrossOrigin
@RestController
@RequestMapping("public/enums")
public class EnumController {

    @Operation(summary = "Get Network (Access Type) values", operationId = "getNetworks")
    @GetMapping("/network")
    public List<Network> getNetworks() {
        return List.of(Network.values());
    }

    /**
     * @return list of possible component types
     * @deprecated This API call is no more needed after switching componentType from enum to String type
     */
    @Deprecated(since = "2.18", forRemoval = true)
    @Operation(summary = "Get Component Type values", operationId = "getComponentTypes")
    @GetMapping("/componentType")
    public List<String> getComponentTypes() {
        return List.of("TEAMS", "CAS");
    }

    @Operation(summary = "Get Activity values", operationId = "getActivities")
    @GetMapping("/activity")
    public List<ServiceActivity> getActivities() {
        return List.of(ServiceActivity.values());
    }
}
