package org.processmining.specpp.datastructures.tree.base;

import org.processmining.specpp.datastructures.tree.base.traits.LocallyContractible;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;

public interface EfficientBacktrackableTree<N extends TreeNode & LocallyExpandable<N> & LocallyContractible<N>> extends EfficientTree<N> {

    N contractTree();

}
