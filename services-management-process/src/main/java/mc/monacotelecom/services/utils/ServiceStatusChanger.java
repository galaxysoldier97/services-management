package mc.monacotelecom.services.utils;

import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.inventory.common.Lambdas;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.inventory.common.sm.StatusChanger;
import mc.monacotelecom.services.entity.Service;
import mc.monacotelecom.services.enums.Event;
import mc.monacotelecom.services.enums.Status;
import mc.monacotelecom.services.exceptions.SvcValidationException;
import mc.monacotelecom.services.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static mc.monacotelecom.services.enums.Event.resum_cli;
import static mc.monacotelecom.services.enums.Event.resum_oper;
import static mc.monacotelecom.services.translation.TranslationMessages.SERVICE_LIFECYCLE_ERROR;

@Slf4j
@Component
public class ServiceStatusChanger implements StatusChanger<Status, Event> {

    private final StateMachineFactory<Status, Event> stateMachineFactory;
    private final LocalizedMessageBuilder localizedMessageBuilder;
    private final ServiceRepository<Service> serviceRepository;

    public ServiceStatusChanger(final @Qualifier("serviceStateMachineFactory") StateMachineFactory<Status, Event> serviceStateMachineFactory,
                                final LocalizedMessageBuilder localizedMessageBuilder,
                                final ServiceRepository<Service> serviceRepository){
        this.stateMachineFactory = serviceStateMachineFactory;
        this.localizedMessageBuilder = localizedMessageBuilder;
        this.serviceRepository = serviceRepository;
    }
    @Override
    public Status updateStatus(final String id, final Status initialStatus, final Event event) {
        final Status updatedStatus;
        if (List.of(resum_oper, resum_cli).contains(event)) {
            updatedStatus = getTargetStatus(id, initialStatus, event);
        } else {
            updatedStatus = this.changeStatus(this.stateMachineFactory, id, initialStatus, event);
        }
        Lambdas.verifyOrThrow.test(Objects.isNull(updatedStatus), new SvcValidationException(localizedMessageBuilder, SERVICE_LIFECYCLE_ERROR, event, initialStatus, id));
        return updatedStatus;
    }

    /**
     * Return target status for unsuspend case.
     * When a service has been suspended, upon unsuspend/unbarring it needs to go back to its original state: BARRED or ACTIVATED
     */
    private Status getTargetStatus(final String id, final Status initialStatus, final Event event) {
        final Status updatedStatus;
        Revisions<Integer, Service> revisions = serviceRepository.findRevisions(Long.valueOf(id));
        final List<Revision<Integer, Service>> revisionEntities = revisions.getContent();
        if (revisionEntities.size() > 1) {
            // Target status will be the status before suspension (activated or barred)
            updatedStatus = revisionEntities.stream()
                    .sorted(Collections.reverseOrder())
                    .filter(x -> !Status.SUSPENDED.equals(x.getEntity().getStatus()))
                    .findFirst()
                    .map(x -> x.getEntity().getStatus())
                    .orElse(Status.ACTIVATED);
        } else {
            // Target status will be processed as usual because of the lack of revision data
            log.warn(String.format("Target status for service '%s' will be processed as usual because of the lack of revision data", id));
            updatedStatus = this.changeStatus(this.stateMachineFactory, id, initialStatus, event);
        }
        return updatedStatus;
    }

    @Override
    public Collection<Event> getAvailableEventsWithId(final String id, final Status status) {
        return this.getAvailableEvents(stateMachineFactory.getStateMachine(id), status);
    }
}
