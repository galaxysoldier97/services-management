package mc.monacotelecom.services.dto;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mc.monacotelecom.inventory.common.recycling.dtos.BaseAddJobConfigurationDTO;
import mc.monacotelecom.services.enums.JobRecyclingOperation;
import mc.monacotelecom.services.enums.ServiceActivity;
import mc.monacotelecom.services.enums.ServiceCategory;
import mc.monacotelecom.services.enums.Status;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AddJobConfigurationDTO extends BaseAddJobConfigurationDTO<JobRecyclingOperation> {

    private Status status;
    private ServiceCategory category;
    private ServiceActivity activity;
}
