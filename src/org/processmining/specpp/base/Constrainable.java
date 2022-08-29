package org.processmining.specpp.base;

import org.processmining.specpp.supervision.piping.Observer;

public interface Constrainable<L extends ConstraintEvent> extends Observer<L> {

    void acceptConstraint(L constraint);

    @Override
    default void observe(L constraint) {
        acceptConstraint(constraint);
    }

    Class<L> getAcceptedConstraintClass();
}
