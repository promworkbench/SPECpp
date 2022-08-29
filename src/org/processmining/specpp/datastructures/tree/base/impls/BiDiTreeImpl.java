package org.processmining.specpp.datastructures.tree.base.impls;

import org.apache.commons.collections4.IteratorUtils;
import org.processmining.specpp.datastructures.tree.base.BiDiTree;
import org.processmining.specpp.datastructures.tree.base.BiDiTreeNode;
import org.processmining.specpp.datastructures.tree.base.EdgeFactory;
import org.processmining.specpp.datastructures.tree.base.TreeEdge;
import org.processmining.specpp.datastructures.tree.iterators.*;
import org.processmining.specpp.datastructures.util.IndexedItem;
import org.processmining.specpp.util.Reflection;

import java.util.Iterator;

public class BiDiTreeImpl<N extends BiDiTreeNode<N>> extends RerootableTreeImpl<N> implements BiDiTree<N> {

    public BiDiTreeImpl(N root) {
        super(root);
    }

    protected BiDiTreeImpl() {
    }

    @Override
    public Iterator<N> traverse() {
        return new TopDownTraversal<>(this);
    }

    @Override
    public Iterator<N> traverse(Class<? extends TreeNodeTraversal<N>> strategyClass) {
        return Reflection.instance(strategyClass, this);
    }

    @Override
    public Iterator<IndexedItem<N>> traverseLevelwise() {
        return new LevelwiseTreeTraversal<>(this);
    }

    @Override
    public Iterator<IndexedItem<N>> traverseLevelwise(Class<? extends LevelwiseTreeTraversal<N>> strategyClass) {
        return Reflection.instance(strategyClass, this);
    }

    @Override
    public Iterator<TreeEdge<N>> traverseEdges() {
        return new AllChildrenEdgeTraversal<>(traverse(), new EdgeFactory.BasicTreeEdgeFactory<>());
    }

    @Override
    public Iterator<TreeEdge<N>> traverseEdges(Class<? extends TreeEdgeTraversal<N, TreeEdge<N>>> strategyClass, EdgeFactory<N, TreeEdge<N>> edgeFactory, Iterator<N> nodeIterator) {
        return Reflection.instance(strategyClass, nodeIterator, edgeFactory);
    }

    @Override
    public Iterator<N> iterator() {
        return traverse();
    }

    @Override
    public Iterable<N> getVertices() {
        return IteratorUtils.asIterable(traverse());
    }

    @Override
    public Iterable<TreeEdge<N>> getEdges() {
        return IteratorUtils.asIterable(traverseEdges());
    }


    @Override
    public String limitedToString(int fromLevel, int toLevel, long nodeLimit) {
        assert fromLevel <= toLevel;
        int levelLimit = toLevel < 0 ? Integer.MAX_VALUE : toLevel;
        long limit = nodeLimit < 0 ? Long.MAX_VALUE : nodeLimit;

        StringBuilder sb = new StringBuilder();
        sb.append("BiDiTree{");
        Iterator<IndexedItem<N>> levelwise = traverseLevelwise();
        int currentLevel = -1;
        long count = 0;
        while (levelwise.hasNext()) {
            IndexedItem<N> next = levelwise.next();
            int level = next.getIndex();
            if (level < fromLevel) continue;
            if (level > currentLevel) {
                if (level > levelLimit) break;
                currentLevel = level;
                sb.append("\n").append(currentLevel).append(":");
            }
            sb.append("\t").append(next.getItem());
            if (++count >= limit) break;
        }
        sb.append("\n").append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BiDiTree{").append("\n");
        Iterator<IndexedItem<N>> levelwise = traverseLevelwise();

        int currentLevel = -1;
        while (levelwise.hasNext()) {
            IndexedItem<N> next = levelwise.next();
            int level = next.getIndex();
            if (level > currentLevel) {
                if (currentLevel >= 0) sb.append("\n");
                sb.append(level).append(":");
                currentLevel = level;
            }
            sb.append("\t").append(next.getItem());
        }
        sb.append("\n").append("}");
        return sb.toString();
    }
}
