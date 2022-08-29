package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

import java.util.function.Supplier;

public class AdHocObservableWrapper<O extends Observation> extends AbstractAsyncAwareObservable<O> implements AdHocObservable<O> {

    private final Supplier<O> supplier;

    public AdHocObservableWrapper(Supplier<O> supplier) {
        this.supplier = supplier;
    }

    public static <O extends Observation> AdHocObservableWrapper<O> wrap(Supplier<O> supplier) {
        return new AdHocObservableWrapper<>(supplier);
    }

    @Override
    public O computeObservation() {
        return supplier.get();
    }

}
