package mc.monacotelecom.services.configuration;

import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.inventory.common.recycling.process.JobConfigurationProcess;
import mc.monacotelecom.inventory.common.recycling.service.JobConfigurationService;
import mc.monacotelecom.services.dto.AddJobConfigurationDTO;
import mc.monacotelecom.services.dto.JobConfigurationDTO;
import mc.monacotelecom.services.dto.JobConfigurationSearchDTO;
import mc.monacotelecom.services.entity.JobConfiguration;
import mc.monacotelecom.services.enums.JobRecyclingOperation;
import mc.monacotelecom.services.mapper.JobMapper;
import mc.monacotelecom.services.repository.JobConfigurationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.Specification;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static mc.monacotelecom.inventory.common.CommonFunctions.addSpecification;
import static mc.monacotelecom.inventory.common.recycling.domain.BaseJobSpecification.hasOperation;
import static mc.monacotelecom.inventory.common.recycling.domain.BaseJobSpecification.isEnabled;
import static mc.monacotelecom.services.repository.specification.JobSpecification.*;
import static org.springframework.data.jpa.domain.Specification.where;

@Configuration
public class JobAdminConfiguration {

    private static class JobConfigurationController extends mc.monacotelecom.inventory.job.web.controler.JobConfigurationController<JobRecyclingOperation, JobConfiguration, JobConfigurationDTO, JobConfigurationSearchDTO, AddJobConfigurationDTO> {
        public JobConfigurationController(final JobConfigurationService<JobRecyclingOperation, JobConfiguration, JobConfigurationDTO, JobConfigurationSearchDTO, AddJobConfigurationDTO> jobConfigurationService) {
            super(jobConfigurationService);
        }
    }

    @Bean
    public JobConfigurationProcess<JobRecyclingOperation, JobConfiguration, JobConfigurationDTO, JobConfigurationSearchDTO, AddJobConfigurationDTO> jobConfigurationProcess(final JobConfigurationRepository jobRecyclingConfigurationRepository,
                                                                                                                                                    final JobMapper jobMapper,
                                                                                                                                                    final LocalizedMessageBuilder localizedMessageBuilder) {
        return new JobConfigurationProcess<>(jobRecyclingConfigurationRepository, localizedMessageBuilder, jobMapper) {

            @Override
            public JobConfiguration patchEntity(final JobConfiguration jobRecyclingConfiguration, final JobConfigurationDTO jobConfigurationDTO) {

                ofNullable(jobConfigurationDTO.getActivity()).ifPresent(jobRecyclingConfiguration::setActivity);
                ofNullable(jobConfigurationDTO.getCategory()).ifPresent(jobRecyclingConfiguration::setCategory);
                ofNullable(jobConfigurationDTO.getStatus()).ifPresent(jobRecyclingConfiguration::setStatus);
                ofNullable(jobConfigurationDTO.getDays()).ifPresent(jobRecyclingConfiguration::setDays);
                ofNullable(jobConfigurationDTO.getEnabled()).ifPresent(jobRecyclingConfiguration::setEnabled);
                ofNullable(jobConfigurationDTO.getOperation()).ifPresent(jobRecyclingConfiguration::setOperation);

                return jobRecyclingConfiguration;
            }

            @Override
            public Specification<JobConfiguration> prepareSpecification(final JobConfigurationSearchDTO jobConfigurationSearchDTO) {

                Specification<JobConfiguration> specification = null;
                specification = nonNull(jobConfigurationSearchDTO.getOperation()) ? where(hasOperation(jobConfigurationSearchDTO.getOperation())) : specification;
                specification = nonNull(jobConfigurationSearchDTO.getEnabled()) ? addSpecification(specification, isEnabled(jobConfigurationSearchDTO.getEnabled())) : specification;
                specification = nonNull(jobConfigurationSearchDTO.getCategory()) ? addSpecification(specification, hasCategory(jobConfigurationSearchDTO.getCategory())) : specification;
                specification = nonNull(jobConfigurationSearchDTO.getActivity()) ? addSpecification(specification, hasActivity(jobConfigurationSearchDTO.getActivity())) : specification;
                specification = nonNull(jobConfigurationSearchDTO.getStatus()) ? addSpecification(specification, hasStatus(jobConfigurationSearchDTO.getStatus())) : specification;

                return specification;
            }
        };
    }

    @Bean
    public JobConfigurationService<JobRecyclingOperation, JobConfiguration, JobConfigurationDTO, JobConfigurationSearchDTO, AddJobConfigurationDTO> jobConfigurationService(final JobConfigurationProcess<JobRecyclingOperation, JobConfiguration, JobConfigurationDTO, JobConfigurationSearchDTO, AddJobConfigurationDTO> process) {
        return new JobConfigurationService<>(process) {
        };
    }

    @Bean
    public JobConfigurationController jobConfigurationController(final JobConfigurationService<JobRecyclingOperation, JobConfiguration, JobConfigurationDTO, JobConfigurationSearchDTO, AddJobConfigurationDTO> service) {
        return new JobConfigurationController(service);
    }
}
