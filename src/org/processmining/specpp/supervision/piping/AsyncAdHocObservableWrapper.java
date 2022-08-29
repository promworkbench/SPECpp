package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

import java.util.function.Supplier;

public class AsyncAdHocObservableWrapper<O extends Observation> extends AdHocObservableWrapper<O> implements AsyncAdHocObservable<O> {
    public AsyncAdHocObservableWrapper(Supplier<O> supplier) {
        super(supplier);
    }
}
