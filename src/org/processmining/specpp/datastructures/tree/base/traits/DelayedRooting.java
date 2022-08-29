package org.processmining.specpp.datastructures.tree.base.traits;

import org.processmining.specpp.datastructures.tree.base.TreeNode;

public interface DelayedRooting<N extends TreeNode> {

    void setRootOnce(N root);

    class Treexecption extends RuntimeException {
    }
}
