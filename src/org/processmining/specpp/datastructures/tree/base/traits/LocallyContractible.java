package org.processmining.specpp.datastructures.tree.base.traits;

import org.processmining.specpp.datastructures.tree.base.TreeNode;

public interface LocallyContractible<N extends TreeNode & LocallyContractible<N>> {

    N generateParent();

    boolean canContract();

}
