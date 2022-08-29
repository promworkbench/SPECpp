package org.processmining.specpp.datastructures.tree.iterators;

import org.processmining.specpp.datastructures.tree.base.UniDiTreeNode;

public abstract class TreeNodeTraversal<N extends UniDiTreeNode<N>> extends PreAdvancingIterator<N> {

    public TreeNodeTraversal() {
    }

    public TreeNodeTraversal(N root) {
        super(root);
    }

    protected N getCurrentNode() {
        return current;
    }

    protected abstract N getNextNode();

    @Override
    protected N advance() {
        return getNextNode();
    }

}
