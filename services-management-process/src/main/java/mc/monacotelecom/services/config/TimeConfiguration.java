package mc.monacotelecom.services.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class TimeConfiguration {
    @Bean
    public ZoneId zoneId() {
        return java.time.ZoneId.systemDefault();
    }

    @Bean
    public Clock clock(ZoneId zoneId) {
        return Clock.system(zoneId);
    }
}
