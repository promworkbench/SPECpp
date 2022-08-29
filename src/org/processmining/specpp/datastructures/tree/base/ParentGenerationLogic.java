package org.processmining.specpp.datastructures.tree.base;

import org.processmining.specpp.datastructures.tree.base.impls.AbstractLocalNode;

public interface ParentGenerationLogic<P extends NodeProperties, S extends NodeState, N extends AbstractLocalNode<P, S, N>> {

    N generateParent(N child);

}
