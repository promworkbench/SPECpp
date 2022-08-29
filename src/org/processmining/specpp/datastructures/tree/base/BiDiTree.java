package org.processmining.specpp.datastructures.tree.base;

import org.processmining.specpp.datastructures.graph.CompleteTree;
import org.processmining.specpp.datastructures.tree.base.traits.Rerootable;

public interface BiDiTree<N extends BiDiTreeNode<N>> extends CompleteTree<N, TreeEdge<N>>, Iterable<N>, Rerootable<N> {


}
