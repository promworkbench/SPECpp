package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.traits.ManyToOne;

public class UnpackingPipe<O extends Observation> extends InflatingPipe<Observations<O>, O> implements ManyToOne<O, O> {
    public UnpackingPipe() {
        super(ObservationIterable::new);
    }

}
