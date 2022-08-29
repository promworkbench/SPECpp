package org.processmining.specpp.base.impls;

import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.componenting.system.link.ComposerComponent;
import org.processmining.specpp.componenting.system.link.CompositionComponent;

public abstract class FilteringComposer<C extends Candidate, I extends CompositionComponent<C>, R extends Result> extends RecursiveComposer<C, I, R> {
    public FilteringComposer(ComposerComponent<C, I, R> childComposer) {
        super(childComposer);
    }

    @Override
    public abstract void accept(C c);

}
