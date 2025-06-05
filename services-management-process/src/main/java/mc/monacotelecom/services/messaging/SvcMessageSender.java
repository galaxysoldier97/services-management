package mc.monacotelecom.services.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.importer.ActionType;
import mc.monacotelecom.services.dto.unm.ServiceUnmDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Slf4j
@Component
@RequiredArgsConstructor
public class SvcMessageSender {

    @Value("${synchronization.unm.exchange:svc-unm-sync}")
    public String exchange;

    @Value("${synchronization.unm.routing-key:#}")
    public String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public void sendServiceUpdatedMessage(ServiceUnmDTO serviceUnmDTO) {
        log.info("Sending to UNM to be synchronized " + serviceUnmDTO);
        rabbitTemplate.convertAndSend(exchange, routingKey, this.generateExchangedMessage(serviceUnmDTO));

        log.info("Data sent to UNM to be synchronized with success " + serviceUnmDTO);
    }


    // Generate message containing payload with the actual serialized security context
    public <T> Message<byte[]> generateExchangedMessage(T val) {
        return MessageBuilder.
                withPayload(Objects.requireNonNull(SerializationUtils.serialize(val)))
                .setHeader("x-persistence-action", ActionType.PERSIST.toString())
                .setHeader("x-class", val.getClass().getSimpleName())
                .setHeader("contentType", MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .build();
    }

}
