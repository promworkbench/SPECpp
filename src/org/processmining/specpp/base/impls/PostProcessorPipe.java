package org.processmining.specpp.base.impls;

import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.base.Result;

public class PostProcessorPipe<R extends Result, I extends Result, F extends Result> implements PostProcessor<R, F> {

    private final PostProcessor<R, I> first;
    private final PostProcessor<? super I, F> second;

    public PostProcessorPipe(PostProcessor<R, I> first, PostProcessor<? super I, F> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public F postProcess(R result) {
        return second.postProcess(first.postProcess(result));
    }

    @Override
    public Class<R> getInputClass() {
        return first.getInputClass();
    }

    @Override
    public Class<F> getOutputClass() {
        return second.getOutputClass();
    }

    public <S extends Result> PostProcessorPipe<R, F, S> add(PostProcessor<? super F, S> next) {
        return new PostProcessorPipe<>(this, next);
    }


}
