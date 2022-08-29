package org.processmining.specpp.datastructures.tree.events;

import org.processmining.specpp.datastructures.tree.base.TreeNode;

public class NodeExpansionEvent<N extends TreeNode> extends TreeNodeEvent<N> {
    private final N child;

    public NodeExpansionEvent(N source, N child) {
        super(source);
        this.child = child;
    }

    public N getChild() {
        return child;
    }

    @Override
    public String toString() {
        return "ExpansionEvent(" + getSource() + ", " + getChild() + ")";
    }
}
