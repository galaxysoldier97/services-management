package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import mc.monacotelecom.services.enums.ServiceActionRequest;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceRequestDTO {

    private ServiceActionRequest actionRequest;

    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime plannedDate;

    private String numberRequest;

    private Long equipmentIdRequest;

    private String accessPointIdRequest;

    private String ancillaryEquipmentIdsRequest;

    private String parentServiceOrderId;

    private String subscriptionOrderId;

    private Long serviceId;
}
