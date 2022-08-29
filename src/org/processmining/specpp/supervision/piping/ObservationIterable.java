package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

import java.util.Iterator;

public class ObservationIterable<O extends Observation> implements Observations<O> {

    private final Iterator<O> internal;

    public ObservationIterable(Iterable<O> internal) {
        this.internal = internal.iterator();
    }

    public ObservationIterable(Iterator<O> internal) {
        this.internal = internal;
    }


    @Override
    public Iterator<O> iterator() {
        return internal;
    }


}
