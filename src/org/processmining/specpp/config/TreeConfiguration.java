package org.processmining.specpp.config;

import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.componenting.system.link.EfficientTreeComponent;
import org.processmining.specpp.componenting.system.link.ExpansionStrategyComponent;
import org.processmining.specpp.datastructures.tree.base.TreeNode;
import org.processmining.specpp.datastructures.tree.base.traits.LocallyExpandable;
import org.processmining.specpp.supervision.instrumentators.InstrumentedEfficientTree;
import org.processmining.specpp.supervision.instrumentators.InstrumentedExpansionStrategy;

public class TreeConfiguration<N extends TreeNode & LocallyExpandable<N>> extends Configuration {

    protected final InitializingBuilder<? extends EfficientTreeComponent<N>, ExpansionStrategyComponent<N>> treeFunction;
    protected final SimpleBuilder<? extends ExpansionStrategyComponent<N>> expansionStrategyBuilder;

    public TreeConfiguration(GlobalComponentRepository gcr, InitializingBuilder<? extends EfficientTreeComponent<N>, ExpansionStrategyComponent<N>> treeFunction, SimpleBuilder<? extends ExpansionStrategyComponent<N>> expansionStrategyBuilder) {
        super(gcr);
        this.treeFunction = treeFunction;
        this.expansionStrategyBuilder = expansionStrategyBuilder;
    }


    public ExpansionStrategyComponent<N> createExpansionStrategy() {
        return createFrom(expansionStrategyBuilder);
    }

    public ExpansionStrategyComponent<N> createPossiblyInstrumentedExpansionStrategy() {
        ExpansionStrategyComponent<N> strategy = createExpansionStrategy();
        return shouldBeInstrumented(strategy) ? checkout(new InstrumentedExpansionStrategy<>(strategy)) : strategy;
    }

    public EfficientTreeComponent<N> createTree() {
        return createFrom(treeFunction, createExpansionStrategy());
    }

    public EfficientTreeComponent<N> createPossiblyInstrumentedTree() {
        EfficientTreeComponent<N> tree = createFrom(treeFunction, createPossiblyInstrumentedExpansionStrategy());
        return shouldBeInstrumented(tree) ? checkout(new InstrumentedEfficientTree<>(tree)) : tree;
    }

    public static class Configurator<N extends TreeNode & LocallyExpandable<N>> implements ComponentInitializerBuilder<TreeConfiguration<N>> {
        protected InitializingBuilder<? extends EfficientTreeComponent<N>, ExpansionStrategyComponent<N>> treeFunction;
        protected SimpleBuilder<? extends ExpansionStrategyComponent<N>> expansionStrategyBuilder;

        public Configurator() {
        }

        public Configurator(InitializingBuilder<? extends EfficientTreeComponent<N>, ExpansionStrategyComponent<N>> treeFunction, SimpleBuilder<? extends ExpansionStrategyComponent<N>> expansionStrategyBuilder) {
            this.treeFunction = treeFunction;
            this.expansionStrategyBuilder = expansionStrategyBuilder;
        }

        public Configurator<N> expansionStrategy(SimpleBuilder<? extends ExpansionStrategyComponent<N>> expansionStrategyBuilder) {
            this.expansionStrategyBuilder = expansionStrategyBuilder;
            return this;
        }

        public Configurator<N> tree(InitializingBuilder<? extends EfficientTreeComponent<N>, ExpansionStrategyComponent<N>> treeFunction) {
            this.treeFunction = treeFunction;
            return this;
        }

        public TreeConfiguration<N> build(GlobalComponentRepository gcr) {
            return new TreeConfiguration<>(gcr, treeFunction, expansionStrategyBuilder);
        }

    }

}
