package mc.monacotelecom.services.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mc.monacotelecom.services.dto.ServiceRequestDTO;
import mc.monacotelecom.services.enums.Event;

import javax.validation.constraints.NotNull;


@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceActionRequestDTO extends ServiceRequestDTO {

    @NotNull
    private Event action;

    private Long serviceId;
}
