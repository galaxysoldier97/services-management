package mc.monacotelecom.services.mapper;

import mc.monacotelecom.inventory.common.recycling.mapper.JobConfigurationMapper;
import mc.monacotelecom.services.dto.AddJobConfigurationDTO;
import mc.monacotelecom.services.dto.JobConfigurationDTO;
import mc.monacotelecom.services.entity.JobConfiguration;
import mc.monacotelecom.services.enums.JobRecyclingOperation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobMapper extends JobConfigurationMapper<JobRecyclingOperation, JobConfiguration, JobConfigurationDTO, AddJobConfigurationDTO> {
    JobConfiguration toEntity(final JobConfigurationDTO jobConfigurationDTO);

    JobConfigurationDTO toDto(final JobConfiguration jobConfiguration);

    JobConfiguration toEntity(final AddJobConfigurationDTO jobConfigurationDTO);
}
