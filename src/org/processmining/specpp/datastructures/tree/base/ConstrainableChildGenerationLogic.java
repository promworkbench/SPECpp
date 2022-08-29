package org.processmining.specpp.datastructures.tree.base;

import org.processmining.specpp.base.Constrainable;

public interface ConstrainableChildGenerationLogic<P extends NodeProperties, S extends NodeState, N extends LocalNode<P, S, N>, L extends GenerationConstraint> extends ChildGenerationLogic<P, S, N>, Constrainable<L> {
}
