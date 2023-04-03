package org.processmining.specpp.base;

/**
 * The base interface for 'advanced compositions', i.e. those that are additionally mutable.
 *
 * @param <C> candidate type
 * @see Composition
 */
public interface AdvancedComposition<C extends Candidate> extends MutableCappedComposition<C> {
}
