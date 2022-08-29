package org.processmining.specpp.datastructures.tree.base;

import org.processmining.specpp.datastructures.tree.base.traits.HasProperties;

public interface PropertyNode<P extends NodeProperties> extends TreeNode, HasProperties<P> {
}
