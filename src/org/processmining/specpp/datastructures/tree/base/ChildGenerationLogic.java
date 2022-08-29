package org.processmining.specpp.datastructures.tree.base;

public interface ChildGenerationLogic<P extends NodeProperties, S extends NodeState, N extends LocalNode<P, S, N>> extends TreeNodeGenerator<N> {

    N generateChild(N parent);

    boolean hasChildrenLeft(N parent);

    int potentialChildrenCount(N parent);

    Iterable<N> potentialFutureChildren(N parent);
}
