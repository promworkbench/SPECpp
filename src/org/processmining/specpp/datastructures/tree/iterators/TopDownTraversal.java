package org.processmining.specpp.datastructures.tree.iterators;

import org.processmining.specpp.datastructures.tree.base.Tree;
import org.processmining.specpp.datastructures.tree.base.UniDiTreeNode;

import java.util.LinkedList;
import java.util.Queue;

public class TopDownTraversal<N extends UniDiTreeNode<N>> extends TreeNodeTraversal<N> {

    private final Queue<N> queue;

    public TopDownTraversal() {
        queue = new LinkedList<>();
    }

    public TopDownTraversal(Tree<N> tree) {
        queue = new LinkedList<>();
        queue.add(tree.getRoot());
        current = advance();
    }

    @Override
    protected N getNextNode() {
        N next = queue.poll();
        if (next != null) queue.addAll(next.getChildren());
        return next;
    }


}
