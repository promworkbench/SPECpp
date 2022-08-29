package org.processmining.specpp.datastructures.tree.base;

public interface TreeStrategy<N extends TreeNode> {

    void registerNode(N node);

    void registerPotentialNodes(Iterable<N> potentialNodes);

    void deregisterNode(N node);


}
