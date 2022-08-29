package org.processmining.specpp.supervision.supervisors;

import org.processmining.specpp.componenting.delegators.ContainerUtils;
import org.processmining.specpp.componenting.delegators.ListContainer;
import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.supervision.AbstractSupervisor;
import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.Observable;
import org.processmining.specpp.supervision.piping.PipeSystemFlusher;
import org.processmining.specpp.traits.Joinable;

public class TerminalSupervisor extends AbstractSupervisor implements Joinable {

    private final ListContainer<Observable<?>> observables = ContainerUtils.listContainer();

    public TerminalSupervisor() {
        globalComponentSystem().require(SupervisionRequirements.observable(SupervisionRequirements.regex("\\w+"), Observation.class), observables);
    }

    @Override
    public void initSelf() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void join() {
        PipeSystemFlusher.flush(observables.getContents());
    }
}
