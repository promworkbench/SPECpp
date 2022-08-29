package org.processmining.specpp.datastructures.tree.base;

import org.processmining.specpp.base.Evaluable;
import org.processmining.specpp.datastructures.tree.base.traits.KnowsDepth;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;
import org.processmining.specpp.datastructures.tree.base.traits.StateNode;

public interface LocalNode<P extends NodeProperties, S extends NodeState, N extends LocalNode<P, S, N>> extends PropertyNode<P>, StateNode<S>, LocallyExpandable<N>, KnowsDepth, Evaluable {

    boolean isRoot();
}
