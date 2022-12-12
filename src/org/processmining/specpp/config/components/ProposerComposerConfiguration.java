package org.processmining.specpp.config.components;

import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.componenting.system.link.ComposerComponent;
import org.processmining.specpp.componenting.system.link.CompositionComponent;
import org.processmining.specpp.componenting.system.link.ProposerComponent;
import org.processmining.specpp.supervision.instrumentators.InstrumentedComposer;
import org.processmining.specpp.supervision.instrumentators.InstrumentedProposer;

public class ProposerComposerConfiguration<C extends Candidate, I extends CompositionComponent<C>, R extends Result> extends Configuration {
    private final SimpleBuilder<? extends ProposerComponent<C>> proposerBuilder;
    private final InitializingBuilder<? extends ComposerComponent<C, I, R>, ? super I> terminalComposerBuilder;
    private final InitializingBuilder<? extends ComposerComponent<C, I, R>, ComposerComponent<C, I, R>>[] composerBuilderChain;

    private final SimpleBuilder<? extends I> terminalCompositionBuilder;
    private final InitializingBuilder<? extends I, ? super I>[] compositionBuilderChain;


    public ProposerComposerConfiguration(GlobalComponentRepository gcr, SimpleBuilder<? extends ProposerComponent<C>> proposerBuilder, SimpleBuilder<? extends I> terminalCompositionBuilder, InitializingBuilder<? extends I, ? super I>[] compositionBuilderChain, InitializingBuilder<? extends ComposerComponent<C, I, R>, ? super I> terminalComposerBuilder, InitializingBuilder<? extends ComposerComponent<C, I, R>, ComposerComponent<C, I, R>>[] composerBuilderChain) {
        super(gcr);
        this.proposerBuilder = proposerBuilder;
        this.terminalCompositionBuilder = terminalCompositionBuilder;
        this.compositionBuilderChain = compositionBuilderChain;
        this.terminalComposerBuilder = terminalComposerBuilder;
        this.composerBuilderChain = composerBuilderChain;
    }

    public ProposerComponent<C> createProposer() {
        return createFrom(proposerBuilder);
    }

    public ProposerComponent<C> createPossiblyInstrumentedProposer() {
        ProposerComponent<C> proposer = createProposer();
        return shouldBeInstrumented(proposer) ? checkout(new InstrumentedProposer<>(proposer)) : proposer;
    }

    public I createTerminalComposition() {
        return createFrom(terminalCompositionBuilder);
    }

    public I createRecursiveComposition() {
        I prev = createTerminalComposition();
        for (int i = compositionBuilderChain.length - 1; i >= 0; i--) {
            prev = createFrom(compositionBuilderChain[i], prev);
        }
        return prev;
    }

    public I createComposition() {
        if (compositionBuilderChain == null || composerBuilderChain.length < 1) return createTerminalComposition();
        else return createRecursiveComposition();
    }


    public ComposerComponent<C, I, R> createTerminalComposer() {
        return createFrom(terminalComposerBuilder, createComposition());
    }

    public ComposerComponent<C, I, R> createRecursiveComposer() {
        ComposerComponent<C, I, R> prev = createTerminalComposer();
        for (int i = composerBuilderChain.length - 1; i >= 0; i--) {
            prev = createFrom(composerBuilderChain[i], prev);
        }
        return prev;
    }

    public ComposerComponent<C, I, R> createComposer() {
        if (composerBuilderChain == null || composerBuilderChain.length < 1) return createTerminalComposer();
        else return createRecursiveComposer();
    }

    public ComposerComponent<C, I, R> createPossiblyInstrumentedComposer() {
        ComposerComponent<C, I, R> composer = createComposer();
        return shouldBeInstrumented(composer) ? checkout(new InstrumentedComposer<>(composer)) : composer;
    }

    public static class Configurator<C extends Candidate, I extends CompositionComponent<C>, R extends Result> implements ComponentInitializerBuilder<ProposerComposerConfiguration<C, I, R>> {

        private SimpleBuilder<? extends ProposerComponent<C>> proposerBuilder;
        private InitializingBuilder<? extends ComposerComponent<C, I, R>, ? super I> terminalComposerBuilder;
        private InitializingBuilder<? extends ComposerComponent<C, I, R>, ComposerComponent<C, I, R>>[] composerBuilderChain;

        private InitializingBuilder<? extends I, ? super I>[] compositionBuilderChain;
        private SimpleBuilder<? extends I> terminalCompositionBuilder;

        public Configurator<C, I, R> proposer(SimpleBuilder<? extends ProposerComponent<C>> proposerBuilder) {
            this.proposerBuilder = proposerBuilder;
            return this;
        }

        public Configurator<C, I, R> composition(SimpleBuilder<? extends I> compositionBuilder) {
            return terminalComposition(compositionBuilder);
        }

        public Configurator<C, I, R> terminalComposition(SimpleBuilder<? extends I> compositionBuilder) {
            this.terminalCompositionBuilder = compositionBuilder;
            return this;
        }

        @SafeVarargs
        public final Configurator<C, I, R> recursiveCompositions(final InitializingBuilder<? extends I, ? super I>... compositionBuilderChain) {
            this.compositionBuilderChain = compositionBuilderChain;
            return this;
        }

        public Configurator<C, I, R> composer(InitializingBuilder<? extends ComposerComponent<C, I, R>, ? super I> composerBuilder) {
            return terminalComposer(composerBuilder);
        }

        public Configurator<C, I, R> terminalComposer(InitializingBuilder<? extends ComposerComponent<C, I, R>, ? super I> composerBuilder) {
            this.terminalComposerBuilder = composerBuilder;
            return this;
        }

        @SafeVarargs
        public final Configurator<C, I, R> recursiveComposers(final InitializingBuilder<? extends ComposerComponent<C, I, R>, ComposerComponent<C, I, R>>... composerBuilderChain) {
            this.composerBuilderChain = composerBuilderChain;
            return this;
        }

        public ProposerComposerConfiguration<C, I, R> build(GlobalComponentRepository gcr) {
            return new ProposerComposerConfiguration<>(gcr, proposerBuilder, terminalCompositionBuilder, compositionBuilderChain, terminalComposerBuilder, composerBuilderChain);
        }

    }


}
