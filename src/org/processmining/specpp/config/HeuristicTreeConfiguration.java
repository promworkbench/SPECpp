package org.processmining.specpp.config;

import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.componenting.system.link.ChildGenerationLogicComponent;
import org.processmining.specpp.componenting.system.link.EfficientTreeComponent;
import org.processmining.specpp.componenting.system.link.ExpansionStrategyComponent;
import org.processmining.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.specpp.datastructures.tree.base.NodeProperties;
import org.processmining.specpp.datastructures.tree.base.NodeState;
import org.processmining.specpp.datastructures.tree.base.impls.LocalNodeWithExternalizedLogic;
import org.processmining.specpp.datastructures.tree.heuristic.HeuristicTreeExpansion;
import org.processmining.specpp.datastructures.tree.heuristic.HeuristicValue;

public class HeuristicTreeConfiguration<P extends NodeProperties, S extends NodeState, N extends LocalNodeWithExternalizedLogic<P, S, N>, H extends HeuristicValue<H>> extends EfficientTreeConfiguration<P, S, N> {

    private final SimpleBuilder<? extends HeuristicStrategy<N, H>> heuristicStrategySupplier;
    private final InitializingBuilder<? extends HeuristicTreeExpansion<N, H>, HeuristicStrategy<N, H>> treeExpansionFunction;

    public HeuristicTreeConfiguration(GlobalComponentRepository gcr, SimpleBuilder<? extends HeuristicStrategy<N, H>> heuristicStrategySupplier, InitializingBuilder<? extends HeuristicTreeExpansion<N, H>, HeuristicStrategy<N, H>> treeExpansionFunction, InitializingBuilder<? extends EfficientTreeComponent<N>, ExpansionStrategyComponent<N>> enumeratingTreeFunction, SimpleBuilder<? extends ChildGenerationLogicComponent<P, S, N>> generatorSupplier) {
        super(gcr, enumeratingTreeFunction, null, generatorSupplier);
        this.heuristicStrategySupplier = heuristicStrategySupplier;
        this.treeExpansionFunction = treeExpansionFunction;
    }

    public HeuristicStrategy<N, H> createHeuristicStrategy() {
        return createFrom(heuristicStrategySupplier);
    }

    public HeuristicTreeExpansion<N, H> createHeuristicTreeExpansion() {
        return createFrom(treeExpansionFunction, createHeuristicStrategy());
    }

    @Override
    public ExpansionStrategyComponent<N> createExpansionStrategy() {
        return createHeuristicTreeExpansion();
    }

    public static class Configurator<P extends NodeProperties, S extends NodeState, N extends LocalNodeWithExternalizedLogic<P, S, N>, H extends HeuristicValue<H>> extends EfficientTreeConfiguration.Configurator<P, S, N> {

        protected SimpleBuilder<HeuristicStrategy<N, H>> heuristicStrategyBuilder;
        protected InitializingBuilder<HeuristicTreeExpansion<N, H>, HeuristicStrategy<N, H>> heuristicExpansionBuilder;

        public Configurator() {
        }

        public Configurator<P, S, N, H> heuristic(SimpleBuilder<HeuristicStrategy<N, H>> heuristicStrategySupplier) {
            this.heuristicStrategyBuilder = heuristicStrategySupplier;
            return this;
        }

        public Configurator<P, S, N, H> heuristicExpansion(InitializingBuilder<HeuristicTreeExpansion<N, H>, HeuristicStrategy<N, H>> heuristicExpansionBuilder) {
            this.heuristicExpansionBuilder = heuristicExpansionBuilder;
            return this;
        }

        @Override
        public Configurator<P, S, N, H> tree(InitializingBuilder<? extends EfficientTreeComponent<N>, ExpansionStrategyComponent<N>> enumeratingTreeFunction) {
            super.tree(enumeratingTreeFunction);
            return this;
        }

        @Override
        public Configurator<P, S, N, H> childGenerationLogic(SimpleBuilder<? extends ChildGenerationLogicComponent<P, S, N>> generatorBuilder) {
            super.childGenerationLogic(generatorBuilder);
            return this;
        }


        @Override
        public HeuristicTreeConfiguration<P, S, N, H> build(GlobalComponentRepository gcr) {
            return new HeuristicTreeConfiguration<>(gcr, heuristicStrategyBuilder, heuristicExpansionBuilder, treeFunction, generatorBuilder);
        }
    }
}
