package org.processmining.specpp.datastructures.tree.base.impls;

import org.processmining.specpp.datastructures.graph.Annotatable;
import org.processmining.specpp.datastructures.tree.base.AnnotatableBiDiNode;

public class AnnotatableBiDiNodeImpl<A> extends AbstractBiDiNode<AnnotatableBiDiNodeImpl<A>> implements Annotatable<A>, AnnotatableBiDiNode<A, AnnotatableBiDiNodeImpl<A>> {

    private A annotation;

    public AnnotatableBiDiNodeImpl(AnnotatableBiDiNodeImpl<A> parent, A annotation) {
        super(parent);
        this.annotation = annotation;
    }

    public AnnotatableBiDiNodeImpl(AnnotatableBiDiNodeImpl<A> parent) {
        super(parent);
    }

    public AnnotatableBiDiNodeImpl() {
    }

    @Override
    public A getAnnotation() {
        return annotation;
    }

    @Override
    public void setAnnotation(A annotation) {
        this.annotation = annotation;
    }

    @Override
    public String toString() {
        return annotation != null ? annotation.toString() : "{}";
    }
}
