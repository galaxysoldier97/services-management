package mc.monacotelecom.services.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import mc.monacotelecom.inventory.common.recycling.dtos.BaseJobConfigurationSearchDTO;
import mc.monacotelecom.services.enums.JobRecyclingOperation;
import mc.monacotelecom.services.enums.ServiceActivity;
import mc.monacotelecom.services.enums.ServiceCategory;
import mc.monacotelecom.services.enums.Status;

@Data
@EqualsAndHashCode(callSuper = true)
public class JobConfigurationSearchDTO extends BaseJobConfigurationSearchDTO<JobRecyclingOperation> {
    private ServiceCategory category;
    private ServiceActivity activity;
    private Status status;
}
