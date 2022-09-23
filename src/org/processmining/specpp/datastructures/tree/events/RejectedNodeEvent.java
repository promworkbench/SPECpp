package org.processmining.specpp.datastructures.tree.events;

import org.processmining.specpp.base.Evaluable;
import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;

public class RejectedNodeEvent<N extends TreeNode & Evaluable & LocallyExpandable<N>> extends TreeHeuristicQueueingEvent<N> {
    public RejectedNodeEvent(N source) {
        super(source);
    }

    @Override
    public int getDelta() {
        return 0;
    }

    @Override
    public String toString() {
        return "RejectedNodeEvent(" + source + ")";
    }
}
