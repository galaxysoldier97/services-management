package mc.monacotelecom.services.repository;

import mc.monacotelecom.inventory.common.recycling.domain.BaseJobConfigurationRepository;
import mc.monacotelecom.services.entity.JobConfiguration;
import mc.monacotelecom.services.enums.JobRecyclingOperation;

public interface JobConfigurationRepository extends BaseJobConfigurationRepository<JobRecyclingOperation, JobConfiguration> {
}
