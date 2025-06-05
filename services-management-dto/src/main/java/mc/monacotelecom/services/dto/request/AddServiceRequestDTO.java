package mc.monacotelecom.services.dto.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ServiceActivity;
import mc.monacotelecom.services.enums.ServiceCategory;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddServiceRequestDTO {

    private Long subscriptionId;

    @NotNull
    private ServiceCategory serviceCategory;

    @NotNull
    private ServiceActivity serviceActivity;

    private Network accessType;

    private String componentType;



}