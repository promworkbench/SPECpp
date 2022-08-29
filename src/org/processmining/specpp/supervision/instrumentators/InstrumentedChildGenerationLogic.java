package org.processmining.specpp.supervision.instrumentators;

import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.componenting.system.link.ChildGenerationLogicComponent;
import org.processmining.specpp.datastructures.tree.base.LocalNode;
import org.processmining.specpp.datastructures.tree.base.NodeProperties;
import org.processmining.specpp.datastructures.tree.base.NodeState;
import org.processmining.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.specpp.supervision.observations.performance.TaskDescription;

public class InstrumentedChildGenerationLogic<P extends NodeProperties, S extends NodeState, N extends LocalNode<P, S, N>> extends AbstractInstrumentingDelegator<ChildGenerationLogicComponent<P, S, N>> implements ChildGenerationLogicComponent<P, S, N> {
    private static final TaskDescription CHILD_NODE_GENERATION = new TaskDescription("Child Node Generation");

    public InstrumentedChildGenerationLogic(ChildGenerationLogicComponent<P, S, N> delegate) {
        super(delegate);
        globalComponentSystem().provide(SupervisionRequirements.observable("child_generation_logic.performance", PerformanceEvent.class, timeStopper));
    }

    public N generateChild(N parent) {
        timeStopper.start(CHILD_NODE_GENERATION);
        N child = delegate.generateChild(parent);
        timeStopper.stop(CHILD_NODE_GENERATION);
        return child;
    }

    public boolean hasChildrenLeft(N parent) {
        return delegate.hasChildrenLeft(parent);
    }

    public int potentialChildrenCount(N parent) {
        return delegate.potentialChildrenCount(parent);
    }

    public Iterable<N> potentialFutureChildren(N parent) {
        return delegate.potentialFutureChildren(parent);
    }

    public N generateRoot() {
        return delegate.generateRoot();
    }
}
