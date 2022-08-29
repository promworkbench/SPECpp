package org.processmining.specpp.datastructures.tree.base.traits;

import org.processmining.specpp.datastructures.tree.base.TreeNode;

public interface KnowsParent<N extends TreeNode & KnowsParent<N>> {

    N getParent();

}
