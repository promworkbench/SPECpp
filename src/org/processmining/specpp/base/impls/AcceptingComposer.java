package org.processmining.specpp.base.impls;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.base.Result;

import java.util.function.Function;

public class AcceptingComposer<C extends Candidate, I extends AdvancedComposition<C>, R extends Result> extends AbstractComposer<C, I, R> {


    public AcceptingComposer(I composition, Function<? super I, R> assembleResult) {
        super(composition, assembleResult);
    }

    @Override
    protected boolean deliberateAcceptance(C candidate) {
        return true;
    }

    @Override
    protected void acceptanceRevoked(C candidate) {

    }

    @Override
    protected void candidateAccepted(C candidate) {

    }

    @Override
    protected void candidateRejected(C candidate) {

    }

    @Override
    protected void initSelf() {

    }

    @Override
    public void candidatesAreExhausted() {

    }
}
