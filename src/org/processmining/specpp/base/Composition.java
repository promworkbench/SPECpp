package org.processmining.specpp.base;

import org.processmining.specpp.datastructures.util.SequentialCollection;

/**
 * The base interface for a composition, i.e. an intermediate result of collected candidates.
 * @param <C>
 */
public interface Composition<C extends Candidate> extends IntermediateResult, SequentialCollection<C> {


}
