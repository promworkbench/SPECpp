package org.processmining.specpp.componenting.system.link;

import org.processmining.specpp.componenting.system.FullComponentSystemUser;
import org.processmining.specpp.datastructures.tree.base.EfficientTree;
import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;

public interface EfficientTreeComponent<N extends TreeNode & LocallyExpandable<N>> extends EfficientTree<N>, FullComponentSystemUser {
}
