package org.processmining.specpp.datastructures.tree.events;

import org.processmining.specpp.base.Evaluable;
import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;

public class DequeueNodeEvent<N extends TreeNode & Evaluable & LocallyExpandable<N>> extends TreeHeuristicQueueingEvent<N> {

    public DequeueNodeEvent(N source) {
        super(source);
    }

    @Override
    public String toString() {
        return "DequeueNodeEvent(" + source + ")";
    }

    @Override
    public int getDelta() {
        return -1;
    }
}
