package org.processmining.specpp.base;

import org.processmining.specpp.base.impls.CandidateConstraint;

/**
 * A base interface for proposers that are also constrainable.
 * @param <C> candidate type
 * @param <L> constraint event type
 */
public interface ConstrainableProposer<C extends Candidate, L extends CandidateConstraint<C>> extends Proposer<C>, Constrainable<L> {

}
