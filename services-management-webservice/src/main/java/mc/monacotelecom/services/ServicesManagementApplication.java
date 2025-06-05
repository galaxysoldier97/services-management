package mc.monacotelecom.services;

import mc.monacotelecom.common.core.lib.webservice.CommonWebserviceApplicationConfiguration;
import mc.monacotelecom.inventory.common.audit.AuditEnversInfo;
import mc.monacotelecom.inventory.common.importer.domain.entity.ImportHistory;
import mc.monacotelecom.inventory.common.importer.domain.repository.ImportHistoryRepository;
import mc.monacotelecom.inventory.common.nls.EnableAcceptHeaderLocaleResolver;
import mc.monacotelecom.inventory.common.nls.EnableCommonNls;
import mc.monacotelecom.services.entity.Service;
import mc.monacotelecom.services.repository.ServiceRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCommonNls
@EnableAcceptHeaderLocaleResolver
// Need to setup packages for EnableJpaRepositories and EntityScan manually, as there are multiple sources
// (common-audit/common-importer/this application)
@EnableJpaRepositories(basePackageClasses = {ServiceRepository.class, ImportHistoryRepository.class},
        repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
@EntityScan(basePackageClasses = {Service.class, AuditEnversInfo.class, ImportHistory.class})
@Import({
        CommonWebserviceApplicationConfiguration.class
})
public class ServicesManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServicesManagementApplication.class, args);
    }
}