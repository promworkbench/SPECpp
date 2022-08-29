package org.processmining.specpp.supervision.supervisors;

import org.processmining.specpp.componenting.delegators.ContainerUtils;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.composition.events.CandidateCompositionEvent;
import org.processmining.specpp.supervision.monitoring.EventCounterMonitor;
import org.processmining.specpp.supervision.piping.ConcurrencyBridge;
import org.processmining.specpp.supervision.piping.PipeWorks;

public class DetailedComposerSupervisor extends MonitoringSupervisor {

    private final ConcurrencyBridge<CandidateCompositionEvent<?>> collector = PipeWorks.concurrencyBridge();

    public DetailedComposerSupervisor() {
        globalComponentSystem().require(SupervisionRequirements.observable(SupervisionRequirements.regex("^composer.*\\.events$"), CandidateCompositionEvent.class), ContainerUtils.observeResults(collector));
        createMonitor("composer.events", new EventCounterMonitor());
    }

    @Override
    protected void instantiateObservationHandlingFullySatisfied() {
        beginLaying().source(collector)
                     .giveBackgroundThread()
                     .sink(getMonitor("composer.events"))
                     .apply();
    }

}
