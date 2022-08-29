package org.processmining.specpp.datastructures.tree.iterators;

import org.processmining.specpp.datastructures.tree.base.Tree;
import org.processmining.specpp.datastructures.tree.base.UniDiTreeNode;
import org.processmining.specpp.datastructures.util.IndexedItem;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class LevelwiseTreeTraversal<N extends UniDiTreeNode<N>> implements Iterator<IndexedItem<N>> {

    protected int depth;
    protected N currentNode;
    protected Queue<N> currentLevel, nextLevel;

    public LevelwiseTreeTraversal(Tree<N> tree) {
        currentLevel = new LinkedList<>();
        nextLevel = new LinkedList<>();
        depth = 0;
        if (tree.getRoot() != null) {
            currentLevel.add(tree.getRoot());
        }
        forward();
    }

    protected boolean proceedToNextLevel() {
        if (!nextLevel.isEmpty()) {
            currentLevel = nextLevel;
            nextLevel = new LinkedList<>();
            depth++;
            return true;
        } else return false;
    }

    protected void forward() {
        if (!currentLevel.isEmpty()) {
            currentNode = currentLevel.poll();
            nextLevel.addAll(currentNode.getChildren());
        } else if (proceedToNextLevel()) {
            forward();
        } else currentNode = null;
    }

    @Override
    public boolean hasNext() {
        return currentNode != null;
    }

    @Override
    public IndexedItem<N> next() {
        IndexedItem<N> t = new IndexedItem<>(depth, currentNode);
        forward();
        return t;
    }

}
