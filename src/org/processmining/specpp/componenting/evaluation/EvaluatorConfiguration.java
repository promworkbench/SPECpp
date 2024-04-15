package org.processmining.specpp.componenting.evaluation;

import com.google.common.collect.ImmutableList;
import org.processmining.specpp.base.Evaluable;
import org.processmining.specpp.base.Evaluation;
import org.processmining.specpp.base.Evaluator;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.specpp.config.components.ComponentInitializerBuilder;
import org.processmining.specpp.config.components.Configuration;
import org.processmining.specpp.config.components.SimpleBuilder;
import org.processmining.specpp.evaluation.fitness.AbstractFullFitnessEvaluator;
import org.processmining.specpp.evaluation.fitness.InstrumentedFullFitnessEvaluator;
import org.processmining.specpp.util.Reflection;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class EvaluatorConfiguration extends Configuration {

    private final ImmutableList<SimpleBuilder<? extends ProvidesEvaluators>> evaluatorProviderBuilders;

    public EvaluatorConfiguration(GlobalComponentRepository gcr, ImmutableList<SimpleBuilder<? extends ProvidesEvaluators>> evaluatorProviderBuilders) {
        super(gcr);
        this.evaluatorProviderBuilders = evaluatorProviderBuilders;
    }

    public ProvidesEvaluators createPossiblyInstrumented(SimpleBuilder<? extends ProvidesEvaluators> builder) {
        ProvidesEvaluators from = checkout(builder).build();
        return (from instanceof AbstractFullFitnessEvaluator && shouldBeInstrumented(from)) ? checkout(new InstrumentedFullFitnessEvaluator((AbstractFullFitnessEvaluator) from)) : checkout(from);
    }

    public List<ProvidesEvaluators> createEvaluators() {
        return evaluatorProviderBuilders.stream().map(this::createFrom).collect(Collectors.toList());
    }

    public List<ProvidesEvaluators> createPossiblyInstrumentedEvaluators() {
        return evaluatorProviderBuilders.stream().map(this::createPossiblyInstrumented).collect(Collectors.toList());
    }

    public List<ProvidesEvaluators> reCheckoutEvaluators(List<ProvidesEvaluators> list) {
        if (list == null) return null;
        return list.stream().map(this::checkout).collect(Collectors.toList());
    }

    public static class Configurator implements ComponentInitializerBuilder<EvaluatorConfiguration> {
        private final List<SimpleBuilder<? extends ProvidesEvaluators>> evaluatorProviderBuilders;

        public Configurator() {
            evaluatorProviderBuilders = new LinkedList<>();
        }


        public <I extends Evaluable, E extends Evaluation> Configurator addEvaluator(Class<I> evaluableClass, Class<E> evaluationClass, Class<Evaluator<? super I, ? extends E>> evaluatorClass) {
            SimpleBuilder<ProvidesEvaluators> builder = () -> {
                class Wrap extends AbstractGlobalComponentSystemUser implements ProvidesEvaluators {
                    public Wrap() {
                        globalComponentSystem().provide(EvaluationRequirements.evaluator(evaluableClass, evaluationClass, Reflection.instance(evaluatorClass)::eval));
                    }
                }
                return new Wrap();
            };
            evaluatorProviderBuilders.add(builder);
            return this;
        }

        public Configurator addEvaluatorProvider(SimpleBuilder<? extends ProvidesEvaluators> builder) {
            evaluatorProviderBuilders.add(builder);
            return this;
        }

        public EvaluatorConfiguration build(GlobalComponentRepository gcr) {
            return new EvaluatorConfiguration(gcr, ImmutableList.copyOf(evaluatorProviderBuilders));
        }
    }

}
