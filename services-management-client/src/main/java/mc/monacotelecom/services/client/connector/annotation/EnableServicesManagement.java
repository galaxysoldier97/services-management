package mc.monacotelecom.services.client.connector.annotation;

import mc.monacotelecom.services.client.connector.config.ServicesManagementClientConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ServicesManagementClientConfiguration.class)
public @interface EnableServicesManagement {
}
