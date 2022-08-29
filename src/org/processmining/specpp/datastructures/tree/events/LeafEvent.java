package org.processmining.specpp.datastructures.tree.events;

import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.traits.RepresentsChange;

public abstract class LeafEvent<N extends TreeNode> extends TreeNodeEvent<N> implements RepresentsChange {
    protected LeafEvent(N source) {
        super(source);
    }
}
