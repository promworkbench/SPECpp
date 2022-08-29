package org.processmining.specpp.supervision.instrumentators;

import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.componenting.system.link.EfficientTreeComponent;
import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;
import org.processmining.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.specpp.supervision.observations.performance.TaskDescription;

import java.util.Collection;

public class InstrumentedEfficientTree<N extends TreeNode & LocallyExpandable<N>> extends AbstractInstrumentingDelegator<EfficientTreeComponent<N>> implements EfficientTreeComponent<N> {

    public static final TaskDescription TREE_EXPANSION = new TaskDescription("Tree Expansion");

    public InstrumentedEfficientTree(EfficientTreeComponent<N> delegate) {
        super(delegate);
        globalComponentSystem().provide(SupervisionRequirements.observable("tree.performance", PerformanceEvent.class, timeStopper));
    }

    public N getRoot() {
        return delegate.getRoot();
    }

    public Collection<N> getLeaves() {
        return delegate.getLeaves();
    }

    public N tryExpandingTree() {
        timeStopper.start(TREE_EXPANSION);
        N n = delegate.tryExpandingTree();
        timeStopper.stop(TREE_EXPANSION);
        return n;
    }

    public void setRootOnce(N root) {
        delegate.setRootOnce(root);
    }

}
