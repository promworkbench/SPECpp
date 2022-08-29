package org.processmining.specpp.postprocessing;

import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.specpp.componenting.system.FullComponentSystemUser;
import org.processmining.specpp.componenting.system.link.AbstractBaseClass;
import org.processmining.specpp.componenting.system.link.PostProcessorComponent;
import org.processmining.specpp.componenting.traits.UsesGlobalComponentSystem;
import org.processmining.specpp.config.SimpleBuilder;

public class WrappedPostProcessor<R extends Result, F extends Result> extends AbstractBaseClass implements PostProcessorComponent<R, F> {

    private final PostProcessor<R, F> delegate;

    public WrappedPostProcessor(PostProcessor<R, F> delegate) {
        this.delegate = delegate;
        if (delegate instanceof FullComponentSystemUser)
            registerSubComponent(((FullComponentSystemUser) delegate));
    }

    @Override
    public F postProcess(R result) {
        return delegate.postProcess(result);
    }

    @Override
    protected void initSelf() {

    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public String getLabel() {
        return delegate.getLabel();
    }

    public static class Builder<R extends Result, F extends Result> extends ComponentSystemAwareBuilder<PostProcessorComponent<? super R, F>> {
        private final SimpleBuilder<? extends PostProcessor<? super R, F>> wrappedBuilder;

        public Builder(SimpleBuilder<? extends PostProcessor<? super R, F>> wrappedBuilder) {
            this.wrappedBuilder = wrappedBuilder;
            if (wrappedBuilder instanceof UsesGlobalComponentSystem)
                globalComponentSystem().consumeEntirely(((UsesGlobalComponentSystem) wrappedBuilder).globalComponentSystem());
        }

        @Override
        protected WrappedPostProcessor<? super R, F> buildIfFullySatisfied() {
            return new WrappedPostProcessor<>(wrappedBuilder.build());
        }
    }
}
