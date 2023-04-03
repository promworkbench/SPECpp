package org.processmining.specpp.base;

import org.processmining.specpp.supervision.piping.Observer;

/**
 * Trait interface for classes that somehow handle constraint events.
 *
 * @param <L> type of constraint events handled
 */
public interface Constrainable<L extends ConstraintEvent> extends Observer<L> {

    void acceptConstraint(L constraint);

    @Override
    default void observe(L constraint) {
        acceptConstraint(constraint);
    }

    Class<L> getAcceptedConstraintClass();
}
