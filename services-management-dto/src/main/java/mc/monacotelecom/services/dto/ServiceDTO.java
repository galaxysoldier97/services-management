package mc.monacotelecom.services.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mc.monacotelecom.services.enums.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.List;


@EqualsAndHashCode(callSuper = false)
@Data
@JsonIdentityInfo(generator = JSOGGenerator.class)
@Relation(collectionRelation = "services")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceDTO extends RepresentationModel<ServiceDTO> {

    @Schema(description = "Database auto-generated serviceId", example = "1")
    private Long serviceId;

    @Schema(description = "CRM Service Id", example = "1")
    private String crmServiceId;

    @Schema(description = "Subscription Id", example = "1")
    private Long subscriptionId;

    @Schema(description = "Number", example = "37799920004")
    private String number;

    @Schema(description = "Related range of numbers", example = "1")
    private Long mainRangeId;

    @Schema(description = "Subtype of the service")
    private ServiceCategory serviceCategory;

    @Schema(description = "Activity")
    private ServiceActivity serviceActivity;

    @Schema(description = "Creation Date", example = "2019-04-14T00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime creationDate;

    @Schema(description = "Activation Date", example = "2019-04-14T00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime activationDate;

    @Schema(description = "Deactivation Date", example = "2019-04-14T00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deactivationDate;

    @Schema(description = "Suspension Date", example = "2019-04-14T00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime suspensionDate;

    @Schema(description = "Resumed Date", example = "2019-04-14T00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime resumedDate;

    @Schema(description = "Cancellation Date", example = "2019-04-14T00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime cancellationDate;

    @Schema(description = "Barring Date", example = "2019-04-14T00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime barringDate;

    @Schema(description = "Service Order ID")
    private String serviceOrderId;

    @Schema(description = "Status of the service")
    private Status status = Status.PENDING;

    @Schema(description = "Customer number", example = "1")
    private Long customerNo;

    @Schema(description = "Action requested on the service")
    private ServiceActionRequest actionRequest;

    @Schema(description = "When the request has been added", example = "2019-04-14T00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateRequest;

    @Schema(description = "Reason of the request")
    private String reasonRequest;

    @Schema(description = "Whether the action has been planned")
    private Boolean actionPlanned;

    @Schema(description = "Planned date for the action to be done", example = "2019-04-14T00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime plannedDate;

    @Schema(description = "Status of the current action")
    private ServiceActionStatus serviceActionStatus;

    @Schema(description = "Start of the current action", example = "2019-04-14T00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime actionStart;

    @Schema(description = "End of the current action", example = "2019-04-14T00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime actionEnd;

    @Schema(description = "Related service tags")
    private List<ServiceTagDTO> serviceTags;

    @Schema(description = "Related service activations")
    private List<ServiceActivationDTO> serviceActivations;

    @Schema(description = "Parameters set on the service")
    private List<ServiceParameterDTO> serviceParameters;

    @Schema(description = "List of all possible transitions")
    private List<Event> serviceTransitions;

    @Schema(description = "If an action is ongoing on the service")
    private Boolean onGoingAction;

    @Schema(description = "If the service has service tags referencing non-existing provisioning tags")
    private boolean hasOrphanTags;

    @Schema(description = "If the service has service activations referencing non-existing activation codes")
    private boolean hasOrphanCodes;
}
