package org.processmining.specpp.datastructures.tree.base.impls;

import java.util.List;

public class BiDiNodeImpl extends AbstractBiDiNode<BiDiNodeImpl> {
    public BiDiNodeImpl(BiDiNodeImpl parent) {
        super(parent);
    }

    public BiDiNodeImpl(BiDiNodeImpl parent, List<BiDiNodeImpl> children) {
        super(parent, children);
    }

    public BiDiNodeImpl() {
    }


}
