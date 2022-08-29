package org.processmining.specpp.datastructures.tree.events;

import org.processmining.specpp.datastructures.tree.base.TreeNode;

public class NodeExhaustionEvent<N extends TreeNode> extends TreeNodeEvent<N> {

    public NodeExhaustionEvent(N source) {
        super(source);
    }

    @Override
    public String toString() {
        return "NodeExhaustionEvent(" + source + ")";
    }

}
