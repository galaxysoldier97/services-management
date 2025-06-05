package mc.monacotelecom.services.configuration;

import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.enums.ServiceActivity;
import mc.monacotelecom.services.enums.ServiceCategory;
import mc.monacotelecom.services.enums.Status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class RestConfiguration implements WebMvcConfigurer {

    @Value("${SVCMGMT_ALLOWED_ORIGIN:*}")
    String corsAllowedOrigin;

    @Bean
    @Primary
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList(corsAllowedOrigin));
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public WebMvcConfigurer customerConverterConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addFormatters(FormatterRegistry registry) {
                final StringToServiceCategoryConverter serviceCategoryConverter = s -> ServiceCategory.valueOf(s.toUpperCase());
                registry.addConverter(serviceCategoryConverter);
                final StringToServiceActivityConverter serviceActivityConverter = s -> ServiceActivity.valueOf(s.toUpperCase());
                registry.addConverter(serviceActivityConverter);
                final StringToServiceStatusConverter serviceStatusConverter = s -> Status.valueOf(s.toUpperCase());
                registry.addConverter(serviceStatusConverter);
                final StringToNetworkConverter accessType = s -> Network.valueOf(s.toUpperCase());
                registry.addConverter(accessType);

            }
        };
    }

    private interface StringToServiceCategoryConverter extends Converter<String, ServiceCategory> {
    }

    private interface StringToServiceActivityConverter extends Converter<String, ServiceActivity> {
    }

    private interface StringToServiceStatusConverter extends Converter<String, Status> {
    }

    private interface StringToNetworkConverter extends Converter<String, Network> {
    }
}