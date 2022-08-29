package org.processmining.specpp.supervision.supervisors;

import org.processmining.specpp.base.ConstraintEvent;
import org.processmining.specpp.composition.events.CandidateCompositionEvent;
import org.processmining.specpp.datastructures.tree.base.GenerationConstraint;
import org.processmining.specpp.datastructures.tree.events.HeuristicComputationEvent;
import org.processmining.specpp.datastructures.tree.events.TreeEvent;
import org.processmining.specpp.supervision.monitoring.KeepLastMonitor;
import org.processmining.specpp.supervision.observations.EventCountStatistics;
import org.processmining.specpp.supervision.piping.ConcurrencyBridge;
import org.processmining.specpp.supervision.piping.Observable;
import org.processmining.specpp.supervision.piping.PipeWorks;
import org.processmining.specpp.supervision.transformers.Transformers;

import static org.processmining.specpp.componenting.delegators.ContainerUtils.observeResults;
import static org.processmining.specpp.componenting.supervision.SupervisionRequirements.observable;
import static org.processmining.specpp.componenting.supervision.SupervisionRequirements.regex;

public class EventCountsSupervisor extends MonitoringSupervisor {

    protected final ConcurrencyBridge<ConstraintEvent> composerConstraints = PipeWorks.concurrencyBridge();
    protected final ConcurrencyBridge<ConstraintEvent> compositionConstraints = PipeWorks.concurrencyBridge();
    protected final ConcurrencyBridge<ConstraintEvent> proposerConstraints = PipeWorks.concurrencyBridge();
    protected final ConcurrencyBridge<TreeEvent> treeEvents = PipeWorks.concurrencyBridge();
    protected final ConcurrencyBridge<CandidateCompositionEvent<?>> composerEvents = PipeWorks.concurrencyBridge();
    protected final ConcurrencyBridge<HeuristicComputationEvent<?>> heuristicsEvents = PipeWorks.concurrencyBridge();

    public EventCountsSupervisor() {
        globalComponentSystem().require(observable(regex("tree\\.events.*"), TreeEvent.class), observeResults(treeEvents))
                               .require(observable(regex("composer.*\\.events.*"), CandidateCompositionEvent.class), observeResults(composerEvents))
                               .require(observable(regex("composer.*\\.constraints.*"), ConstraintEvent.class), observeResults(composerConstraints))
                               .require(observable(regex("composition\\.constraints.*"), ConstraintEvent.class), observeResults(compositionConstraints))
                               .require(observable(regex("proposer\\.constraints.*"), GenerationConstraint.class), observeResults(proposerConstraints))
                               .require(observable(regex("heuristics\\.events.*"), HeuristicComputationEvent.class), observeResults(heuristicsEvents));
        createMonitor("tree.events.accumulation", new KeepLastMonitor<>());
        createMonitor("composer.events.accumulation", new KeepLastMonitor<>());
        createMonitor("composition.events.accumulation", new KeepLastMonitor<>());
        createMonitor("heuristics.events.accumulation", new KeepLastMonitor<>());
        createMonitor("proposer.constraints.accumulation", new KeepLastMonitor<>());
        createMonitor("composer.constraints.accumulation", new KeepLastMonitor<>());
        createMonitor("composition.constraints.accumulation", new KeepLastMonitor<>());
    }


    @Override
    protected void instantiateObservationHandlingPartiallySatisfied() {
        layConnections(treeEvents, "tree.events");
        layConnections(heuristicsEvents, "heuristics.events");
        layConnections(composerEvents, "composer.events");
        layConnections(composerConstraints, "composer.constraints");
        layConnections(compositionConstraints, "composition.constraints");
        layConnections(proposerConstraints, "proposer.constraints");
    }

    protected void layConnections(Observable<?> source, String label) {
        beginLaying().source(source)
                     .giveBackgroundThread()
                     .pipe(PipeWorks.summarizingBuffer(Transformers.eventCounter()))
                     .schedule(RefreshRates.REFRESH_INTERVAL)
                     .sinks(PipeWorks.loggingSinks(RefreshRates.REFRESH_STRING + label + ".count", fileLogger))
                     .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                     .sinks(PipeWorks.loggingSinks(label + ".accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                     .sink(getMonitor(label + ".accumulation"))
                     .apply();
    }
}
