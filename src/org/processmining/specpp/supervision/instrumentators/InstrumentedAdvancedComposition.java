package org.processmining.specpp.supervision.instrumentators;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.Candidate;

public class InstrumentedAdvancedComposition<C extends Candidate> extends InstrumentedComposition<C> implements AdvancedComposition<C> {
    private final AdvancedComposition<C> advDelegate;

    public InstrumentedAdvancedComposition(AdvancedComposition<C> delegate) {
        super(delegate);
        this.advDelegate = delegate;
    }

    @Override
    public int maxSize() {
        return advDelegate.maxSize();
    }

    @Override
    public void remove(C item) {
        advDelegate.remove(item);
    }

    @Override
    public C removeLast() {
        return advDelegate.removeLast();
    }

}
