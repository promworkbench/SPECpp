package org.processmining.specpp.supervision.supervisors;

import org.processmining.specpp.componenting.delegators.DelegatingAdHocObservable;
import org.processmining.specpp.componenting.delegators.DelegatingObservable;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.config.parameters.OutputPathParameters;
import org.processmining.specpp.datastructures.tree.events.HeuristicComputationEvent;
import org.processmining.specpp.datastructures.tree.events.HeuristicStatsEvent;
import org.processmining.specpp.datastructures.tree.events.TreeHeuristicQueueingEvent;
import org.processmining.specpp.datastructures.tree.events.TreeHeuristicsEvent;
import org.processmining.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.supervision.CSVWriter;
import org.processmining.specpp.supervision.MessageLogger;
import org.processmining.specpp.supervision.monitoring.TimeSeriesMonitor;
import org.processmining.specpp.supervision.observations.TimedObservation;
import org.processmining.specpp.supervision.piping.PipeWorks;
import org.processmining.specpp.util.JavaTypingUtils;
import org.processmining.specpp.util.PathTools;

import java.time.Duration;
import java.time.LocalDateTime;

public class DetailedHeuristicsSupervisor extends FileWritingMonitoringSupervisor {

    private final DelegatingAdHocObservable<HeuristicStatsEvent> heuristicStats = new DelegatingAdHocObservable<>();
    private final DelegatingObservable<TreeHeuristicsEvent> heuristicsEvents = new DelegatingObservable<>();
    private CSVWriter<TreeHeuristicQueueingEvent<PlaceNode>> queueSizeExporter;
    private CSVWriter<TimedObservation<HeuristicComputationEvent<DoubleScore>>> heuristicsExporter;

    public DetailedHeuristicsSupervisor() {
        globalComponentSystem().require(SupervisionRequirements.observable("heuristics.events", JavaTypingUtils.<HeuristicComputationEvent<DoubleScore>>castClass(HeuristicComputationEvent.class)), heuristicsEvents)
                               .require(SupervisionRequirements.adHocObservable("heuristics.stats", HeuristicStatsEvent.class), heuristicStats);
        createMonitor("heuristics.queue.size", new TimeSeriesMonitor<>("queue.size", TimeSeriesMonitor.<TreeHeuristicQueueingEvent<PlaceNode>>delta_accumulator()));
    }

    @Override
    public void instantiateObservationHandlingFullySatisfied() {
        if (supervisionParametersSource.getData().isUseUseFiles()) {
            OutputPathParameters outputPathParameters = pathParametersSource.getData();

            queueSizeExporter = new CSVWriter<>(outputPathParameters.getFilePath(PathTools.OutputFileType.CSV_EXPORT, "queue"), new String[]{"time", "place", "change", "queue.size delta"}, e -> new String[]{LocalDateTime.now().toString(), e.getSource()
                                                                                                                                                                                                                                                .getProperties().toString(), e.getClass().getSimpleName(), Integer.toString(e.getDelta())});

            heuristicsExporter = new CSVWriter<>(outputPathParameters.getFilePath(PathTools.OutputFileType.CSV_EXPORT, "heuristics"), new String[]{"time", "candidate", "score"}, e -> new String[]{e.getLocalDateTime().toString(), e.getObservation()
                                                                                                                                                                                                                                      .getSource().toString(), e.getObservation()
                                                                                                                                                                                                                                                                .getHeuristic().toString()});

            MessageLogger heuristicsLogger = PipeWorks.fileLogger("heuristics", outputPathParameters.getFilePath(PathTools.OutputFileType.SUB_LOG, "heuristics"));

            beginLaying().source(heuristicsEvents)
                         .pipe(PipeWorks.<TreeHeuristicsEvent>concurrencyBridge())
                         .giveBackgroundThread()
                         .split(lp -> lp.pipe(PipeWorks.asyncBuffer())
                                        .schedule(Duration.ofMillis(100))
                                        .pipe(PipeWorks.unpackingPipe())
                                        .sink(PipeWorks.loggingSink("heuristics", heuristicsLogger))
                                        .apply())
                         .split(lp -> lp.pipe(PipeWorks.predicatePipe(e -> e instanceof HeuristicComputationEvent))
                                        .pipe(PipeWorks.timer())
                                        .sink(heuristicsExporter)
                                        .schedule(Duration.ofMillis(100))
                                        .apply())
                         .pipe(PipeWorks.predicatePipe(e -> e instanceof TreeHeuristicQueueingEvent))
                         .sink(getMonitor("heuristics.queue.size"))
                         .sink(queueSizeExporter)
                         .schedule(Duration.ofMillis(100))
                         .apply();
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (queueSizeExporter != null) queueSizeExporter.stop();
        if (heuristicsExporter != null) heuristicsExporter.stop();
    }

}
