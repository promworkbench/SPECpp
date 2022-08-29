package org.processmining.specpp.datastructures.tree.base;

import org.processmining.specpp.datastructures.tree.base.impls.LocalNodeWithExternalizedLogic;

public interface LocalNodeGenerator<P extends NodeProperties, S extends NodeState, N extends LocalNodeWithExternalizedLogic<P, S, N>> extends ChildGenerationLogic<P, S, N>, ParentGenerationLogic<P, S, N> {

    N generateRoot();

}
