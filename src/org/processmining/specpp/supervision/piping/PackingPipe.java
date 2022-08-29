package org.processmining.specpp.supervision.piping;

import org.apache.commons.collections4.IteratorUtils;
import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.traits.OneToMany;

public class PackingPipe<O extends Observation> extends TransformingPipe<O, Observations<O>> implements OneToMany<O, O> {
    public PackingPipe() {
        super(o -> new ObservationIterable<>(IteratorUtils.singletonIterator(o)));
    }

}
