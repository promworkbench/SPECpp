package org.processmining.specpp.componenting.system.link;

import org.processmining.specpp.componenting.system.FullComponentSystemUser;
import org.processmining.specpp.datastructures.tree.base.ChildGenerationLogic;
import org.processmining.specpp.datastructures.tree.base.LocalNode;
import org.processmining.specpp.datastructures.tree.base.NodeProperties;
import org.processmining.specpp.datastructures.tree.base.NodeState;

public interface ChildGenerationLogicComponent<P extends NodeProperties, S extends NodeState, N extends LocalNode<P, S, N>> extends ChildGenerationLogic<P, S, N>, FullComponentSystemUser {
}
