package mc.monacotelecom.services.configuration;

import lombok.RequiredArgsConstructor;
import mc.monacotelecom.inventory.common.recycling.process.JobProcess;
import mc.monacotelecom.inventory.common.recycling.service.JobService;
import mc.monacotelecom.inventory.job.web.controler.JobController;
import mc.monacotelecom.services.entity.JobHistory;
import mc.monacotelecom.services.repository.JobHistoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobApiConfiguration {

    @Bean
    public JobProcess<JobHistory> jobProcess(final JobHistoryRepository jobHistoryRepository) {
        return new JobProcess<>(jobHistoryRepository);
    }

    @Bean
    public JobService<JobHistory> jobService(final JobProcess<JobHistory> process) {
        return new JobService<>(process);
    }

    @Bean
    public JobController<JobHistory> jobController(final JobService<JobHistory> service) {
        return new JobController<>(service);
    }
}
