package org.processmining.specpp.datastructures.tree.base.impls;

import org.processmining.specpp.datastructures.tree.base.AnnotatableBiDiNode;
import org.processmining.specpp.datastructures.tree.base.BiDiTree;
import org.processmining.specpp.supervision.observations.StringObservation;
import org.processmining.specpp.supervision.piping.AbstractAsyncAwareObservable;
import org.processmining.specpp.supervision.piping.AsyncAdHocObservable;

public class TreePrinter extends AbstractAsyncAwareObservable<StringObservation> implements AsyncAdHocObservable<StringObservation> {

    private final BiDiTree<? extends AnnotatableBiDiNode<?, ?>> tree;
    private final int fromLevel;
    private final int toLevel;
    private final long nodeLimit;

    public TreePrinter(BiDiTree<? extends AnnotatableBiDiNode<?, ?>> tree, int fromLevel, int toLevel, long nodeLimit) {
        this.tree = tree;
        this.fromLevel = fromLevel;
        this.toLevel = toLevel;
        this.nodeLimit = nodeLimit;
    }

    @Override
    public StringObservation computeObservation() {
        return new StringObservation(tree.limitedToString(fromLevel, toLevel, nodeLimit));
    }

}
