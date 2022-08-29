package org.processmining.specpp.datastructures.tree.base;

import org.processmining.specpp.datastructures.tree.base.traits.KnowsChildren;

public interface UniDiTreeNode<N extends UniDiTreeNode<N>> extends TreeNode, KnowsChildren<N> {

}
