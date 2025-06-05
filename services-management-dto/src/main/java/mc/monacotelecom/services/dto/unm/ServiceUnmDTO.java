package mc.monacotelecom.services.dto.unm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import mc.monacotelecom.services.enums.ServiceActivity;
import mc.monacotelecom.services.enums.unm.ActionType;
import mc.monacotelecom.services.enums.unm.CustomerServiceStatus;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Data
public class ServiceUnmDTO implements Serializable  {
    private ServiceActivity activity;
    private String identifier;
    private CustomerServiceStatus status;
    private String accessId;
    private Long serviceTechnicalId;
}
