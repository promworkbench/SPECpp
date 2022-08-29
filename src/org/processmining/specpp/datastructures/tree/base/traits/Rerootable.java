package org.processmining.specpp.datastructures.tree.base.traits;

import org.processmining.specpp.datastructures.tree.base.TreeNode;

public interface Rerootable<N extends TreeNode> {

    void setRoot(N newRoot);

}
