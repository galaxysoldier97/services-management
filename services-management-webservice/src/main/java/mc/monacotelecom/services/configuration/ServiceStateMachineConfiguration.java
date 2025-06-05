package mc.monacotelecom.services.configuration;

import lombok.extern.java.Log;
import mc.monacotelecom.services.enums.Event;
import mc.monacotelecom.services.enums.Status;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Log
@Configuration
@EnableStateMachineFactory(name = "serviceStateMachineFactory")
public class ServiceStateMachineConfiguration extends StateMachineConfigurerAdapter<Status, Event> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<Status, Event> config) throws Exception {
        StateMachineListenerAdapter<Status, Event> adapter = new StateMachineListenerAdapter<Status, Event>() {
            @Override
            public void stateChanged(State<Status, Event> from, State<Status, Event> to) {
                log.info(String.format("stateChanged(from: %s, to: %s)", from + "", to + ""));
            }
        };
        config.withConfiguration()
                .autoStartup(false)
                .listener(adapter);
    }


    @Override
    public void configure(StateMachineStateConfigurer<Status, Event> states) throws Exception {
        states.withStates()
                .initial(Status.PENDING)
                .state(Status.CANCELED)
                .state(Status.ACTIVATED)
                .state(Status.BARRED)
                .state(Status.SUSPENDED)
                .state(Status.DEACTIVATED)
                .end(Status.FINALSTATE);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<Status, Event> transitions) throws Exception {
        transitions
                .withExternal().source(Status.PENDING).target(Status.CANCELED).event(Event.cancel)
                .and()
                .withExternal().source(Status.PENDING).target(Status.ACTIVATED).event(Event.activate)
                .and()
                .withExternal().source(Status.CANCELED).target(Status.PENDING).event(Event.rollbackCancellation)
                .and()
                .withExternal().source(Status.ACTIVATED).target(Status.BARRED).event(Event.barring)
                .and()
                .withExternal().source(Status.ACTIVATED).target(Status.PENDING).event(Event.rollbackActivation)
                .and()
                .withExternal().source(Status.ACTIVATED).target(Status.SUSPENDED).event(Event.susp_cli)
                .and()
                .withExternal().source(Status.ACTIVATED).target(Status.SUSPENDED).event(Event.susp_oper)
                .and()
                .withExternal().source(Status.ACTIVATED).target(Status.DEACTIVATED).event(Event.deactivate)
                .and()
                .withExternal().source(Status.DEACTIVATED).target(Status.ACTIVATED).event(Event.rollbackDeactivation)
                .and()
                .withExternal().source(Status.SUSPENDED).target(Status.ACTIVATED).event(Event.resum_oper)
                .and()
                .withExternal().source(Status.SUSPENDED).target(Status.DEACTIVATED).event(Event.deactivate)
                .and()
                .withExternal().source(Status.SUSPENDED).target(Status.SUSPENDED).event(Event.unBarring)
                .and()
                .withExternal().source(Status.SUSPENDED).target(Status.ACTIVATED).event(Event.resum_cli)
                .and()
                .withExternal().source(Status.BARRED).target(Status.SUSPENDED).event(Event.susp_oper)
                .and()
                .withExternal().source(Status.BARRED).target(Status.ACTIVATED).event(Event.unBarring)
                .and()
                .withExternal().source(Status.BARRED).target(Status.DEACTIVATED).event(Event.deactivate);
    }
}
