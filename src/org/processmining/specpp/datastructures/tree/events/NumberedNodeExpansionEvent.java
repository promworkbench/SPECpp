package org.processmining.specpp.datastructures.tree.events;

import org.processmining.specpp.datastructures.tree.base.TreeNode;

public class NumberedNodeExpansionEvent<N extends TreeNode> extends NodeExpansionEvent<N> {
    private final int number;

    public NumberedNodeExpansionEvent(N source, N child, int number) {
        super(source, child);
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return number + ": " + super.toString();
    }
}
