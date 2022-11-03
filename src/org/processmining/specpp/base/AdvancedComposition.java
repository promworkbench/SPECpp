package org.processmining.specpp.base;

/**
 * The base interface for 'advanced compositions', i.e. those that are additionally mutable.
 * @see Composition
 * @param <C> candidate type
 */
public interface AdvancedComposition<C extends Candidate> extends MutableCappedComposition<C> {
}
