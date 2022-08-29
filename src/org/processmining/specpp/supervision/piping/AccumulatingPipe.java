package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.traits.OneToOne;
import org.processmining.specpp.supervision.transformers.AccumulatingTransformer;
import org.processmining.specpp.traits.Mergeable;

import java.util.function.Supplier;

public class AccumulatingPipe<O extends Observation & Mergeable<? super O>> extends TypeIdentTransformingPipe<O> implements OneToOne<O, O> {
    public AccumulatingPipe(Supplier<O> initial) {
        super(new AccumulatingTransformer<>(initial));
    }

}
