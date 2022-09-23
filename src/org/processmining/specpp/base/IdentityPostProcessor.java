package org.processmining.specpp.base;

import org.processmining.specpp.util.JavaTypingUtils;

public class IdentityPostProcessor<R extends Result> implements PostProcessor<R, R> {
    @Override
    public R postProcess(R result) {
        return result;
    }

    @Override
    public Class<R> getInputClass() {
        return JavaTypingUtils.castClass(Object.class);
    }

    @Override
    public Class<R> getOutputClass() {
        return JavaTypingUtils.castClass(Object.class);
    }
}
