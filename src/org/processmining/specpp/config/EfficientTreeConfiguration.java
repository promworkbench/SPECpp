package org.processmining.specpp.config;

import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.componenting.system.link.ChildGenerationLogicComponent;
import org.processmining.specpp.componenting.system.link.EfficientTreeComponent;
import org.processmining.specpp.componenting.system.link.ExpansionStrategyComponent;
import org.processmining.specpp.datastructures.tree.base.NodeProperties;
import org.processmining.specpp.datastructures.tree.base.NodeState;
import org.processmining.specpp.datastructures.tree.base.impls.LocalNodeWithExternalizedLogic;
import org.processmining.specpp.supervision.instrumentators.InstrumentedChildGenerationLogic;

public class EfficientTreeConfiguration<P extends NodeProperties, S extends NodeState, N extends LocalNodeWithExternalizedLogic<P, S, N>> extends TreeConfiguration<N> {

    protected final SimpleBuilder<? extends ChildGenerationLogicComponent<P, S, N>> generatorBuilder;

    public EfficientTreeConfiguration(GlobalComponentRepository gcr, InitializingBuilder<? extends EfficientTreeComponent<N>, ExpansionStrategyComponent<N>> treeFunction, SimpleBuilder<? extends ExpansionStrategyComponent<N>> expansionStrategyBuilder, SimpleBuilder<? extends ChildGenerationLogicComponent<P, S, N>> generatorBuilder) {
        super(gcr, treeFunction, expansionStrategyBuilder);
        this.generatorBuilder = generatorBuilder;
    }

    public ChildGenerationLogicComponent<P, S, N> createPossiblyInstrumentedChildGenerationLogic() {
        ChildGenerationLogicComponent<P, S, N> logic = createChildGenerationLogic();
        return shouldBeInstrumented(logic) ? checkout(new InstrumentedChildGenerationLogic<>(logic)) : logic;
    }

    public ChildGenerationLogicComponent<P, S, N> createChildGenerationLogic() {
        return createFrom(generatorBuilder);
    }

    public static class Configurator<P extends NodeProperties, S extends NodeState, N extends LocalNodeWithExternalizedLogic<P, S, N>> extends TreeConfiguration.Configurator<N> {

        protected SimpleBuilder<? extends ChildGenerationLogicComponent<P, S, N>> generatorBuilder;

        public Configurator() {
        }

        @Override
        public Configurator<P, S, N> tree(InitializingBuilder<? extends EfficientTreeComponent<N>, ExpansionStrategyComponent<N>> treeFunction) {
            super.tree(treeFunction);
            return this;
        }

        @Override
        public Configurator<P, S, N> expansionStrategy(SimpleBuilder<? extends ExpansionStrategyComponent<N>> expansionStrategyBuilder) {
            super.expansionStrategy(expansionStrategyBuilder);
            return this;
        }

        public Configurator<P, S, N> childGenerationLogic(SimpleBuilder<? extends ChildGenerationLogicComponent<P, S, N>> generatorBuilder) {
            this.generatorBuilder = generatorBuilder;
            return this;
        }

        @Override
        public EfficientTreeConfiguration<P, S, N> build(GlobalComponentRepository gcr) {
            return new EfficientTreeConfiguration<>(gcr, treeFunction, expansionStrategyBuilder, generatorBuilder);
        }

    }

}
