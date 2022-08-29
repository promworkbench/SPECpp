package org.processmining.specpp.datastructures.tree.base.traits;

import org.processmining.specpp.datastructures.tree.base.EdgeFactory;
import org.processmining.specpp.datastructures.tree.base.TreeEdge;
import org.processmining.specpp.datastructures.tree.base.UniDiTreeNode;
import org.processmining.specpp.datastructures.tree.iterators.LevelwiseTreeTraversal;
import org.processmining.specpp.datastructures.tree.iterators.TreeEdgeTraversal;
import org.processmining.specpp.datastructures.tree.iterators.TreeNodeTraversal;
import org.processmining.specpp.datastructures.util.IndexedItem;

import java.util.Iterator;

public interface TreeTraversable<N extends UniDiTreeNode<N>, E extends TreeEdge<N>> {


    Iterator<N> traverse();

    Iterator<N> traverse(Class<? extends TreeNodeTraversal<N>> strategyClass);

    Iterator<IndexedItem<N>> traverseLevelwise();

    Iterator<IndexedItem<N>> traverseLevelwise(Class<? extends LevelwiseTreeTraversal<N>> strategyClass);

    Iterator<TreeEdge<N>> traverseEdges();

    Iterator<E> traverseEdges(Class<? extends TreeEdgeTraversal<N, E>> strategyClass, EdgeFactory<N, E> edgeFactory, Iterator<N> nodeIterator);

}
