package org.processmining.specpp.datastructures.tree.base.impls;


import org.processmining.specpp.datastructures.tree.base.TreeEdge;
import org.processmining.specpp.datastructures.tree.base.TreeNode;

public class TreeEdgeImpl<N extends TreeNode> implements TreeEdge<N> {

    private final N parent, child;

    public TreeEdgeImpl(N parent, N child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    public N predecessor() {
        return parent;
    }

    @Override
    public N successor() {
        return child;
    }
}
