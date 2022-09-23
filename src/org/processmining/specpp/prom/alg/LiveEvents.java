package org.processmining.specpp.prom.alg;

import org.processmining.specpp.base.ConstraintEvent;
import org.processmining.specpp.supervision.monitoring.KeepLastMonitor;
import org.processmining.specpp.supervision.observations.Event;
import org.processmining.specpp.supervision.observations.EventCountStatistics;
import org.processmining.specpp.supervision.piping.ConcurrencyBridge;
import org.processmining.specpp.supervision.piping.LayingPipe;
import org.processmining.specpp.supervision.piping.PipeWorks;
import org.processmining.specpp.supervision.supervisors.MonitoringSupervisor;
import org.processmining.specpp.supervision.transformers.Transformers;

import java.time.Duration;

import static org.processmining.specpp.componenting.delegators.ContainerUtils.observeResults;
import static org.processmining.specpp.componenting.supervision.SupervisionRequirements.observable;
import static org.processmining.specpp.componenting.supervision.SupervisionRequirements.regex;

public class LiveEvents extends MonitoringSupervisor {

    private final ConcurrencyBridge<Event> eventConcurrencyBridge = PipeWorks.concurrencyBridge();
    private static final Duration REFRESH_INTERVAL = Duration.ofMillis(100);
    private final KeepLastMonitor<Event> monitor;

    public LiveEvents() {
        globalComponentSystem().require(observable(regex(".*events.*"), Event.class), observeResults(eventConcurrencyBridge))
                               .require(observable(regex(".*constraints.*"), ConstraintEvent.class), observeResults(eventConcurrencyBridge));
        monitor = new KeepLastMonitor<>();
        createMonitor("events", monitor);
    }

    @Override
    protected void instantiateObservationHandlingPartiallySatisfied() {
        LayingPipe lp = beginLaying().source(eventConcurrencyBridge)
                                     .giveBackgroundThread()
                                     .pipe(PipeWorks.summarizingBuffer(Transformers.eventCounter()))
                                     .schedule(REFRESH_INTERVAL)
                                     .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new));
        if (fileLogger.isSet())
            lp.sink(PipeWorks.loggingSink("events.count", EventCountStatistics::toPrettyString, fileLogger));
        lp.sink(getMonitor("events")).apply();
    }

}
