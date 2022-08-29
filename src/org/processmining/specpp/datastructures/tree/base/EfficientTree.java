package org.processmining.specpp.datastructures.tree.base;

import org.processmining.specpp.datastructures.tree.base.traits.DelayedRooting;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;

import java.util.Collection;

public interface EfficientTree<N extends TreeNode & LocallyExpandable<N>> extends Tree<N>, DelayedRooting<N> {

    Collection<N> getLeaves();

    N tryExpandingTree();

}
