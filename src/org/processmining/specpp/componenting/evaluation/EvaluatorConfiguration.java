package org.processmining.specpp.componenting.evaluation;

import com.google.common.collect.ImmutableList;
import org.processmining.specpp.base.Evaluable;
import org.processmining.specpp.base.Evaluation;
import org.processmining.specpp.base.Evaluator;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.specpp.config.ComponentInitializerBuilder;
import org.processmining.specpp.config.Configuration;
import org.processmining.specpp.config.SimpleBuilder;
import org.processmining.specpp.evaluation.fitness.AbstractBasicFitnessEvaluator;
import org.processmining.specpp.evaluation.fitness.InstrumentedBasicFitnessEvaluator;
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
        return (from instanceof AbstractBasicFitnessEvaluator && shouldBeInstrumented(from)) ? checkout(new InstrumentedBasicFitnessEvaluator((AbstractBasicFitnessEvaluator) from)) : checkout(from);
    }

    public List<ProvidesEvaluators> createEvaluators() {
        return evaluatorProviderBuilders.stream().map(this::createFrom).collect(Collectors.toList());
    }

    public List<ProvidesEvaluators> createPossiblyInstrumentedEvaluators() {
        return evaluatorProviderBuilders.stream().map(this::createPossiblyInstrumented).collect(Collectors.toList());
    }

    public static class Configurator implements ComponentInitializerBuilder<EvaluatorConfiguration> {
        private final List<SimpleBuilder<? extends ProvidesEvaluators>> evaluatorProviderBuilders;

        public Configurator() {
            evaluatorProviderBuilders = new LinkedList<>();
        }


        public <I extends Evaluable, E extends Evaluation> Configurator evaluator(Class<I> evaluableClass, Class<E> evaluationClass, Class<Evaluator<? super I, ? extends E>> evaluatorClass) {
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

        public Configurator evaluatorProvider(SimpleBuilder<? extends ProvidesEvaluators> builder) {
            evaluatorProviderBuilders.add(builder);
            return this;
        }

        public EvaluatorConfiguration build(GlobalComponentRepository gcr) {
            return new EvaluatorConfiguration(gcr, ImmutableList.copyOf(evaluatorProviderBuilders));
        }
    }

}
