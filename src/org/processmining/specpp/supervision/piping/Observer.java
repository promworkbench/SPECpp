package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

import java.util.function.Consumer;

/**
 * Base interface for observers.
 *
 * @param <O> the type of observations observed by this Observer
 */
@FunctionalInterface
public interface Observer<O extends Observation> extends Consumer<O> {

    void observe(O observation);

    @Override
    default void accept(O o) {
        observe(o);
    }

}
