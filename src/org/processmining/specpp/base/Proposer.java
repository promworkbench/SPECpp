package org.processmining.specpp.base;

import java.util.function.Supplier;


/**
 * This is the base interface of a candidate proposer.
 * It extends the supplier interface as it has the same functionality.
 *
 * @param <C> type of candidate that this proposer proposes
 */
@FunctionalInterface
public interface Proposer<C extends Candidate> extends Supplier<C> {

    C proposeCandidate();

    @Override
    default C get() {
        return proposeCandidate();
    }
}
