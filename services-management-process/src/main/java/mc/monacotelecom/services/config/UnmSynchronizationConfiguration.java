package mc.monacotelecom.services.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "synchronization", name = "unm.enabled", havingValue = "true")
public class UnmSynchronizationConfiguration {

    @Value("${synchronization.unm.exchange:svc-unm-sync}")
    public String exchange;

    @Value("${synchronization.unm.routing-key:#}")
    public String routingKey;

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(exchange);
    }
}
