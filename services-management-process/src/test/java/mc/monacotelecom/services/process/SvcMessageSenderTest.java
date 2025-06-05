package mc.monacotelecom.services.process;

import mc.monacotelecom.importer.ActionType;
import mc.monacotelecom.services.dto.unm.ServiceUnmDTO;
import mc.monacotelecom.services.messaging.SvcMessageSender;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.support.GenericMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = SvcMessageSender.class)
class SvcMessageSenderTest {

    @SpyBean
    private SvcMessageSender svcMessageSender;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    void sendMessage() {
        var message = new ServiceUnmDTO();
        message.setIdentifier("test-01");
        svcMessageSender.sendServiceUpdatedMessage(message);

        ArgumentCaptor<GenericMessage<ServiceUnmDTO>> captor = ArgumentCaptor.forClass(GenericMessage.class);

        verify(rabbitTemplate).convertAndSend(any(), any(), captor.capture());
        verify(svcMessageSender).generateExchangedMessage(message);

        var res = (GenericMessage<ServiceUnmDTO>) captor.getValue();
        var headers = res.getHeaders();

        assertEquals("ServiceUnmDTO", headers.get("x-class"));
        assertEquals(ActionType.PERSIST.toString(), headers.get("x-persistence-action"));
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, headers.get("contentType"));
    }
}
