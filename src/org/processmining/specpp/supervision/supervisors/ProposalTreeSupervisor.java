package org.processmining.specpp.supervision.supervisors;

import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.delegators.DelegatingObservable;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.config.parameters.TreeTrackerParameters;
import org.processmining.specpp.datastructures.tree.events.TreeEvent;
import org.processmining.specpp.datastructures.tree.events.TreeNodeEvent;
import org.processmining.specpp.supervision.monitoring.TreeMonitor;
import org.processmining.specpp.supervision.piping.PipeWorks;

public class ProposalTreeSupervisor extends MonitoringSupervisor {

    private final DelegatingObservable<TreeEvent> treeEvents = new DelegatingObservable<>();
    private final DelegatingDataSource<TreeTrackerParameters> paramSource = new DelegatingDataSource<>();

    public ProposalTreeSupervisor() {
        globalComponentSystem().require(SupervisionRequirements.observable("tree.events", TreeEvent.class), treeEvents)
                               .require(ParameterRequirements.parameters("tree.tracker.parameters", TreeTrackerParameters.class), paramSource);
    }

    @Override
    protected void instantiateObservationHandlingFullySatisfied() {
        createMonitor("tree.monitor", new TreeMonitor(paramSource.getData()));
        beginLaying()
                .source(treeEvents)
                .pipe(PipeWorks.concurrencyBridge())
                .giveBackgroundThread()
                .pipe(PipeWorks.predicatePipe(e -> e instanceof TreeNodeEvent))
                .sink(getMonitor("tree.monitor")).apply();
    }
}
