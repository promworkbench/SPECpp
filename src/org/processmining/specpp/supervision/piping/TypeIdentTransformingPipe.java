package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

public class TypeIdentTransformingPipe<O extends Observation> extends TransformingPipe<O, O> implements TypeIdentPipe<O> {
    public TypeIdentTransformingPipe(ObservationTransformer<? super O, ? extends O> transformer) {
        super(transformer);
    }
}
