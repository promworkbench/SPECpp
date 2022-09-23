package org.processmining.specpp.supervision.supervisors;

import org.processmining.specpp.componenting.delegators.DelegatingAdHocObservable;
import org.processmining.specpp.componenting.delegators.DelegatingObservable;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.datastructures.tree.events.LeafEvent;
import org.processmining.specpp.datastructures.tree.events.TreeEvent;
import org.processmining.specpp.datastructures.tree.events.TreeStatsEvent;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.supervision.CSVWriter;
import org.processmining.specpp.supervision.monitoring.TimeSeriesMonitor;
import org.processmining.specpp.supervision.observations.EventCountStatistics;
import org.processmining.specpp.supervision.piping.PipeWorks;
import org.processmining.specpp.util.PathTools;

import java.time.Duration;
import java.time.LocalDateTime;

public class DetailedTreeSupervisor extends FileWritingMonitoringSupervisor {

    private final DelegatingObservable<TreeEvent> treeEvents = new DelegatingObservable<>();
    private final DelegatingObservable<EventCountStatistics> treeCounts = new DelegatingObservable<>();
    private final DelegatingAdHocObservable<TreeStatsEvent> treeStats = new DelegatingAdHocObservable<>();
    private CSVWriter<LeafEvent<PlaceNode>> leafCountChangesWriter;

    public DetailedTreeSupervisor() {
        globalComponentSystem().require(SupervisionRequirements.observable("tree.events", TreeEvent.class), treeEvents)
                               .require(SupervisionRequirements.adHocObservable("tree.stats", TreeStatsEvent.class), treeStats);
        createMonitor("tree.leaves.count", new TimeSeriesMonitor<>("leaves.count", TimeSeriesMonitor.<LeafEvent<PlaceNode>>delta_accumulator()));
    }

    @Override
    protected void instantiateObservationHandlingFullySatisfied() {
        if (supervisionParametersSource.getData().isUseUseFiles()) {

            leafCountChangesWriter = new CSVWriter<>(pathParametersSource.getData()
                                                                         .getFilePath(PathTools.OutputFileType.CSV_EXPORT, "tree"), new String[]{"time", "place", "change", "tree.leaves.count delta"}, e -> new String[]{LocalDateTime.now().toString(), e.getSource()
                                                                                                                                                                                                                                                           .getProperties().toString(), e.getClass().getSimpleName(), Integer.toString(e.getDelta())});


            beginLaying().source(treeEvents)
                         .pipe(PipeWorks.concurrencyBridge())
                         .giveBackgroundThread()
                         .pipe(PipeWorks.predicatePipe(e -> e instanceof LeafEvent))
                         .sink(getMonitor("tree.leaves.count"))
                         .sink(leafCountChangesWriter)
                         .schedule(Duration.ofMillis(100))
                         .apply();

        }
    }

    @Override
    public void join() {
        super.join();
        leafCountChangesWriter.stop();
    }
}
