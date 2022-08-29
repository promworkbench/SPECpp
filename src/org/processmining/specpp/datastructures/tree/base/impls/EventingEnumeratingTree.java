package org.processmining.specpp.datastructures.tree.base.impls;

import org.processmining.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.specpp.componenting.system.link.ExpansionStrategyComponent;
import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;
import org.processmining.specpp.datastructures.tree.events.*;
import org.processmining.specpp.supervision.EventSupervision;
import org.processmining.specpp.supervision.piping.AsyncAdHocObservableWrapper;
import org.processmining.specpp.supervision.piping.PipeWorks;

public class EventingEnumeratingTree<N extends TreeNode & LocallyExpandable<N>> extends EnumeratingTree<N> {


    private final EventSupervision<TreeEvent> eventSupervision = PipeWorks.eventSupervision();


    public EventingEnumeratingTree(N root, ExpansionStrategyComponent<N> expansionStrategy) {
        super(root, expansionStrategy);
        makeProvisions();
    }

    public EventingEnumeratingTree(ExpansionStrategyComponent<N> expansionStrategy) {
        super(expansionStrategy);
        makeProvisions();
    }

    protected void makeProvisions() {
        globalComponentSystem().provide(SupervisionRequirements.observable("tree.events", TreeEvent.class, eventSupervision))
                               .provide(SupervisionRequirements.adHocObservable("tree.stats", EnumeratingTreeStatsEvent.class, AsyncAdHocObservableWrapper.wrap(() -> new EnumeratingTreeStatsEvent(leaves.size()))));
    }

    @Override
    protected N expand() {
        return super.expand();
    }

    @Override
    protected void nodeExpanded(N node, N child) {
        eventSupervision.observe(new NodeExpansionEvent<>(node, child));
        super.nodeExpanded(node, child);
    }

    @Override
    protected void notExpandable(N node) {
        eventSupervision.observe(new NodeExhaustionEvent<>(node));
        super.notExpandable(node);
    }

    @Override
    protected boolean addLeaf(N node) {
        boolean actuallyAdded = super.addLeaf(node);
        if (actuallyAdded) eventSupervision.observe(new LeafAdditionEvent<>(node));
        return actuallyAdded;
    }

    @Override
    protected boolean removeLeaf(N node) {
        boolean actuallyRemoved = super.removeLeaf(node);
        if (actuallyRemoved) eventSupervision.observe(new LeafRemovalEvent<>(node));
        return actuallyRemoved;
    }

}
