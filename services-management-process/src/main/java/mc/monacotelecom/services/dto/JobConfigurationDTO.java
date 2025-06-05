package mc.monacotelecom.services.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import mc.monacotelecom.inventory.common.recycling.dtos.BaseJobConfigurationDTO;
import mc.monacotelecom.services.enums.JobRecyclingOperation;
import mc.monacotelecom.services.enums.ServiceActivity;
import mc.monacotelecom.services.enums.ServiceCategory;
import mc.monacotelecom.services.enums.Status;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class JobConfigurationDTO extends BaseJobConfigurationDTO<JobRecyclingOperation> {

    private Status status;
    private ServiceCategory category;
    private ServiceActivity activity;
}
