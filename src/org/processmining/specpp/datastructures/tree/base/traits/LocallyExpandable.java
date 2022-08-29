package org.processmining.specpp.datastructures.tree.base.traits;

import org.processmining.specpp.datastructures.tree.base.TreeNode;

public interface LocallyExpandable<N extends TreeNode & LocallyExpandable<N>> {

    Iterable<N> generatePotentialChildren();

    boolean didExpand();

    boolean canExpand();

    N generateChild();

}
