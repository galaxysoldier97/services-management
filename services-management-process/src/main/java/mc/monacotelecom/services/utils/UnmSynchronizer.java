package mc.monacotelecom.services.utils;

import mc.monacotelecom.services.assembler.ServiceAccessResourceAssembler;
import mc.monacotelecom.services.entity.ServiceAccess;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.messaging.SvcMessageSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UnmSynchronizer {

    private final SvcMessageSender svcMessageSender;
    private final ServiceAccessResourceAssembler serviceAccessResourceAssembler;

    public UnmSynchronizer(SvcMessageSender svcMessageSender) {
        this.svcMessageSender = svcMessageSender;
        this.serviceAccessResourceAssembler = ServiceAccessResourceAssembler.of(UnmSynchronizer.class);
    }

    // The UNM SYNCHRONIZATION feature is only activated for FTTH context
    @Value("${synchronization.unm.enabled:false}")
    private boolean unmSynchronizationActivationState;

    public void synchronizeUnmService(ServiceAccess serviceAccess) {
        if (unmSynchronizationActivationState && Network.FTTH.equals(serviceAccess.getAccessType())) {
            svcMessageSender.sendServiceUpdatedMessage(serviceAccessResourceAssembler.toUnmResource((serviceAccess)));
        }
    }
}
