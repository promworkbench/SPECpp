package org.processmining.specpp.componenting.system.link;

import org.processmining.specpp.componenting.system.FullComponentSystemUser;
import org.processmining.specpp.datastructures.tree.base.ExpansionStrategy;
import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;

public interface ExpansionStrategyComponent<N extends TreeNode & LocallyExpandable<N>> extends ExpansionStrategy<N>, FullComponentSystemUser {
}
