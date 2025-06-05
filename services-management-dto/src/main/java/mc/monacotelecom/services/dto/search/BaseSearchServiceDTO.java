package mc.monacotelecom.services.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mc.monacotelecom.services.enums.ServiceActivity;
import mc.monacotelecom.services.enums.Status;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseSearchServiceDTO {

    @Schema(description = "Internal ID", example = "1")
    private Long serviceId;

    @Schema(description = "Subscription/Service Order ID")
    private String subscriptionOrderId;

    @Schema(description = "CRM Service Id", example = "1")
    private String crmServiceId;

    @Schema(description = "Subscription Id", example = "1")
    private Long subscriptionId;

    @Schema(description = "Related range of numbers", example = "1")
    private Long mainRangeId;

    @Schema(description = "Number", example = "37799920004")
    private String number;

    @Schema(description = "Activity")
    private ServiceActivity serviceActivity;

    @Schema(description = "Internal ID of the related service access, for service component only", example = "1")
    private Long accessServiceId;

    @Schema(description = "CRM Service ID of the related service access, for service component only", example = "1")
    private String accessCrmServiceId;

    @Schema(description = "Customer number", example = "1")
    private Long customerNo;

    @Schema(description = "Status of the service")
    private Status status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime activationDate;

    @Schema(description = "Technical ID linked with service")
    private String techId;
}
