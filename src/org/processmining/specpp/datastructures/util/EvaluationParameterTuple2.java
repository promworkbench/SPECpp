package org.processmining.specpp.datastructures.util;

import org.processmining.specpp.base.Evaluable;

public class EvaluationParameterTuple2<T1, T2> extends ImmutableTuple2<T1, T2> implements Evaluable {
    public EvaluationParameterTuple2(T1 t1, T2 t2) {
        super(t1, t2);
    }
}
