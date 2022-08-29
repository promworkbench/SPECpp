package org.processmining.specpp.datastructures.tree.events;

import org.processmining.specpp.datastructures.tree.base.TreeNode;

public class NodeRemovalEvent<N extends TreeNode> extends TreeNodeEvent<N> {
    public NodeRemovalEvent(N source) {
        super(source);
    }

    @Override
    public String toString() {
        return "NodeRemovalEvent(" + source + ")";
    }
}
