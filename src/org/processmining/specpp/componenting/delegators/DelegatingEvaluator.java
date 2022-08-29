package org.processmining.specpp.componenting.delegators;

import org.processmining.specpp.base.Evaluable;
import org.processmining.specpp.base.Evaluation;
import org.processmining.specpp.base.Evaluator;

public class DelegatingEvaluator<I extends Evaluable, E extends Evaluation> extends AbstractDelegator<Evaluator<? super I, ? extends E>> implements Evaluator<I, E> {

    public DelegatingEvaluator() {
    }

    public DelegatingEvaluator(Evaluator<I, E> delegate) {
        super(delegate);
    }

    @Override
    public E eval(I input) {
        return delegate.eval(input);
    }

}
