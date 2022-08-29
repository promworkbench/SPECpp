package org.processmining.specpp.supervision.instrumentators;

import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.componenting.system.link.ExpansionStrategyComponent;
import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;
import org.processmining.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.specpp.supervision.observations.performance.TaskDescription;

public class InstrumentedExpansionStrategy<N extends TreeNode & LocallyExpandable<N>> extends AbstractInstrumentingDelegator<ExpansionStrategyComponent<N>> implements ExpansionStrategyComponent<N> {

    public static final TaskDescription TREE_EXPANSION_SELECTION = new TaskDescription("Tree Expansion Selection");

    public InstrumentedExpansionStrategy(ExpansionStrategyComponent<N> delegate) {
        super(delegate);
        globalComponentSystem().provide(SupervisionRequirements.observable("tree.strategy.performance", PerformanceEvent.class, timeStopper));
    }

    public N nextExpansion() {
        timeStopper.start(TREE_EXPANSION_SELECTION);
        N n = delegate.nextExpansion();
        timeStopper.stop(TREE_EXPANSION_SELECTION);
        return n;
    }

    public boolean hasNextExpansion() {
        return delegate.hasNextExpansion();
    }

    public N deregisterPreviousProposal() {
        return delegate.deregisterPreviousProposal();
    }

    public void registerNode(N node) {
        delegate.registerNode(node);
    }

    public void registerPotentialNodes(Iterable<N> potentialNodes) {
        delegate.registerPotentialNodes(potentialNodes);
    }

    public void deregisterNode(N node) {
        delegate.deregisterNode(node);
    }
}
