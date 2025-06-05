package mc.monacotelecom.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@Configuration
public class MockTimeConfiguration {
    @Primary
    @Bean
    public ZoneId mockedZoneId() {
        return ZoneId.of("Europe/Paris");
    }

    @Primary
    @Bean
    public Clock mockedClock(ZoneId zoneId) {
        return Clock.fixed(Instant.parse("2020-05-31T18:35:24.00Z"), zoneId);
    }
}
