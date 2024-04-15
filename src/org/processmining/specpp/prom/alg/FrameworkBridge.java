package org.processmining.specpp.prom.alg;

import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.base.impls.IdentityPostProcessor;
import org.processmining.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.specpp.config.components.SimpleBuilder;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.specpp.datastructures.tree.heuristic.HeuristicUtils;
import org.processmining.specpp.datastructures.tree.heuristic.TreeNodeScore;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.evaluation.fitness.BaselineFitnessEvaluator;
import org.processmining.specpp.evaluation.fitness.parallelized.ForkJoinFitnessEvaluator;
import org.processmining.specpp.evaluation.fitness.MarkingHistoryBasedFitnessEvaluator;
import org.processmining.specpp.evaluation.heuristics.*;
import org.processmining.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.specpp.postprocessing.*;
import org.processmining.specpp.preprocessing.orderings.ActivityOrderingStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FrameworkBridge {

    public static final List<AnnotatedActivityOrderingStrategy> ORDERING_STRATEGIES = Arrays
            .stream(BridgedActivityOrderingStrategies.values())
            .map(BridgedActivityOrderingStrategies::getBridge)
            .collect(Collectors.toList());

    public static final List<AnnotatedTreeHeuristic> HEURISTICS = Arrays
            .stream(BridgedHeuristics.values())
            .map(BridgedHeuristics::getBridge)
            .collect(Collectors.toList());
    public static final List<AnnotatedEvaluator> EVALUATORS = Arrays
            .stream(BridgedEvaluators.values())
            .map(BridgedEvaluators::getBridge)
            .collect(Collectors.toList());

    public static final List<AnnotatedEvaluator> FITNESS_METRICS = Arrays
            .stream(BridgedEvaluators.values())
            .map(BridgedEvaluators::getBridge)
            .collect(Collectors.toList());

    public static final List<AnnotatedEvaluator> DELTA_FUNCTIONS = Arrays
            .stream(BridgedDeltaAdaptationFunctions.values())
            .map(BridgedDeltaAdaptationFunctions::getBridge)
            .collect(Collectors.toList());

    public static final List<AnnotatedPostProcessor> POST_PROCESSORS = Arrays.asList(BridgedPostProcessors.ReplayBasedImplicitPlaceRemoval.getBridge(), BridgedPostProcessors.LPBasedImplicitPlaceRemoval.getBridge(), BridgedPostProcessors.SelfLoopPlacesMerging.getBridge(), BridgedPostProcessors.UniwiredSelfLoopAddition.getBridge(), BridgedPostProcessors.DanglingTransitionsAddition.getBridge(), BridgedPostProcessors.PlaceTxtExport.getBridge(), BridgedPostProcessors.ProMPetrinetConversion.getBridge());

    public enum BridgedHeuristics {
        PlaceInterestingness(new AnnotatedTreeHeuristic("Place Interestingness", EventuallyFollowsTreeHeuristic.Builder::new)), BFS_Emulation(new AnnotatedTreeHeuristic("BFS Emulation", () -> () -> HeuristicUtils.<PlaceNode>bfs())), DFS_Emulation(new AnnotatedTreeHeuristic("DFS Emulation", () -> () -> HeuristicUtils.<PlaceNode>dfs()));

        private final AnnotatedTreeHeuristic bth;

        BridgedHeuristics(AnnotatedTreeHeuristic bth) {
            this.bth = bth;
        }

        @Override
        public String toString() {
            return bth.toString();
        }

        public AnnotatedTreeHeuristic getBridge() {
            return bth;
        }
    }

    public enum BridgedPostProcessors {
        Identity(new AnnotatedPostProcessor("Identity", CollectionOfPlaces.class, CollectionOfPlaces.class, () -> IdentityPostProcessor::new)), ReplayBasedImplicitPlaceRemoval(new AnnotatedPostProcessor("Replay-Based Implicit Place Removal", CollectionOfPlaces.class, CollectionOfPlaces.class, ReplayBasedImplicitnessPostProcessing.Builder::new)), LPBasedImplicitPlaceRemoval(new AnnotatedPostProcessor("LP-Based Implicit Place Removal", CollectionOfPlaces.class, CollectionOfPlaces.class, LPBasedImplicitnessPostProcessing.Builder::new)), SelfLoopPlacesMerging(new AnnotatedPostProcessor("Self-Loop Places Merging", CollectionOfPlaces.class, CollectionOfPlaces.class, () -> SelfLoopPlaceMerger::new)), UniwiredSelfLoopAddition(new AnnotatedPostProcessor("Uniwired Self-Loop Addition", CollectionOfPlaces.class, CollectionOfPlaces.class, StrictUniwiredSelfLoopAdditionPostProcessing.Builder::new)), DanglingTransitionsAddition(new AnnotatedPostProcessor("Dangling Transitions Addition", ProMPetrinetWrapper.class, ProMPetrinetWrapper.class, AddDanglingTransitionPostProcessing.Builder::new)), PlaceTxtExport(new AnnotatedPostProcessor("Basic txt based Place Export", CollectionOfPlaces.class, CollectionOfPlaces.class, PlaceExporter.Builder::new)), ProMPetrinetConversion(new AnnotatedPostProcessor("Conversion to ProM Petri net", CollectionOfPlaces.class, ProMPetrinetWrapper.class, () -> ProMConverter::new));
        private final AnnotatedPostProcessor bpp;

        BridgedPostProcessors(AnnotatedPostProcessor bpp) {
            this.bpp = bpp;
        }

        public AnnotatedPostProcessor getBridge() {
            return bpp;
        }


        @Override
        public String toString() {
            return bpp.toString();
        }
    }

    public enum BridgedEvaluators {
        BaseFitness(new AnnotatedEvaluator("Base Fitness Evaluator", BaselineFitnessEvaluator.Builder::new)), ForkJoinFitness(new AnnotatedEvaluator("Concurrent Fitness Evaluator", ForkJoinFitnessEvaluator.Builder::new)), MarkingHistoryBasedFitness(new AnnotatedEvaluator("Marking History Based Fitness Evaluator", MarkingHistoryBasedFitnessEvaluator.Builder::new)), MarkingHistory(new AnnotatedEvaluator("Marking History Computer", () -> LogHistoryMaker::new));

        private final AnnotatedEvaluator be;

        BridgedEvaluators(AnnotatedEvaluator be) {
            this.be = be;
        }

        public AnnotatedEvaluator getBridge() {
            return be;
        }

        @Override
        public String toString() {
            return be.toString();
        }

    }

    public enum BridgedFitnessMetrics {

    }

    public enum BridgedDeltaAdaptationFunctions {
        None(new AnnotatedEvaluator("None", NoDelta.Builder::new)), Constant(new AnnotatedEvaluator("Constant Delta", ConstantDelta.Builder::new)), Linear(new AnnotatedEvaluator("Linear Delta", LinearDelta.Builder::new)), Sigmoid(new AnnotatedEvaluator("Sigmoid Delta", SigmoidDelta.Builder::new));

        private final AnnotatedEvaluator be;

        BridgedDeltaAdaptationFunctions(AnnotatedEvaluator be) {
            this.be = be;
        }

        public AnnotatedEvaluator getBridge() {
            return be;
        }

        @Override
        public String toString() {
            return be.toString();
        }
    }

    public enum BridgedActivityOrderingStrategies {
        AverageFirstOccurrenceIndex(new AnnotatedActivityOrderingStrategy("Average First Occurrence Index", org.processmining.specpp.preprocessing.orderings.AverageFirstOccurrenceIndex.class)), AverageTraceOccurrence(new AnnotatedActivityOrderingStrategy("Average Trace Occurrence", org.processmining.specpp.preprocessing.orderings.AverageTraceOccurrence.class)), AbsoluteTraceFrequency(new AnnotatedActivityOrderingStrategy("Absolute Trace Frequency", org.processmining.specpp.preprocessing.orderings.AbsoluteTraceFrequency.class)), AbsoluteActivityFrequency(new AnnotatedActivityOrderingStrategy("Absolute Activity Frequency", org.processmining.specpp.preprocessing.orderings.AbsoluteActivityFrequency.class)), Lexicographic(new AnnotatedActivityOrderingStrategy("Lexicographic", org.processmining.specpp.preprocessing.orderings.Lexicographic.class)), Random(new AnnotatedActivityOrderingStrategy("Random", org.processmining.specpp.preprocessing.orderings.RandomOrdering.class));

        private final AnnotatedActivityOrderingStrategy strategy;

        BridgedActivityOrderingStrategies(AnnotatedActivityOrderingStrategy strategy) {
            this.strategy = strategy;
        }

        public AnnotatedActivityOrderingStrategy getBridge() {
            return strategy;
        }

        public Class<? extends ActivityOrderingStrategy> getStrategyClass() {
            return strategy.getStrategyClass();
        }

        @Override
        public String toString() {
            return strategy.toString();
        }
    }

    public static class Annotated<T> {
        private final String printableName;
        private final Supplier<SimpleBuilder<? extends T>> builderSupplier;

        Annotated(String printableName, Supplier<SimpleBuilder<? extends T>> builderSupplier) {
            this.printableName = printableName;
            this.builderSupplier = builderSupplier;
        }

        @Override
        public String toString() {
            return printableName;
        }

        public String getPrintableName() {
            return printableName;
        }

        public SimpleBuilder<? extends T> getBuilder() {
            return builderSupplier.get();
        }
    }

    public static class AnnotatedEvaluator extends Annotated<ProvidesEvaluators> {

        AnnotatedEvaluator(String printableName, Supplier<SimpleBuilder<? extends ProvidesEvaluators>> simpleBuilderSupplier) {
            super(printableName, simpleBuilderSupplier);
        }

    }

    public static class AnnotatedTreeHeuristic extends Annotated<HeuristicStrategy<PlaceNode, TreeNodeScore>> {

        AnnotatedTreeHeuristic(String printableName, Supplier<SimpleBuilder<? extends HeuristicStrategy<PlaceNode, TreeNodeScore>>> simpleBuilderSupplier) {
            super(printableName, simpleBuilderSupplier);
        }
    }

    public static class AnnotatedActivityOrderingStrategy {

        private final Class<? extends ActivityOrderingStrategy> strategyClass;
        private final String printableName;

        AnnotatedActivityOrderingStrategy(String printableName, Class<? extends ActivityOrderingStrategy> strategyClass) {
            this.printableName = printableName;
            this.strategyClass = strategyClass;
        }

        @Override
        public String toString() {
            return printableName;
        }

        public Class<? extends ActivityOrderingStrategy> getStrategyClass() {
            return strategyClass;
        }
    }

    public static class AnnotatedPostProcessor extends Annotated<PostProcessor<?, ?>> {

        private final Class<?> inType;
        private final Class<?> outType;

        AnnotatedPostProcessor(String printableName, Class<?> inType, Class<?> outType, Supplier<SimpleBuilder<? extends PostProcessor<?, ?>>> simpleBuilderSupplier) {
            super(printableName, simpleBuilderSupplier);
            this.inType = inType;
            this.outType = outType;
        }

        public Class<?> getInType() {
            return inType;
        }

        public Class<?> getOutType() {
            return outType;
        }

        @Override
        public String toString() {
            return "[" + getInType().getSimpleName() + " => " + getOutType().getSimpleName() + "]" + " " + super.toString();
        }
    }


}
