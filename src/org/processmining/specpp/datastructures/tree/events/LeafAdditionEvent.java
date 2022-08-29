package org.processmining.specpp.datastructures.tree.events;

import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;

public class LeafAdditionEvent<N extends TreeNode & LocallyExpandable<N>> extends LeafEvent<N> {

    public LeafAdditionEvent(N source) {
        super(source);
    }

    @Override
    public String toString() {
        return "LeafAddedEvent(" + source + ")";
    }

    @Override
    public int getDelta() {
        return 1;
    }
}
