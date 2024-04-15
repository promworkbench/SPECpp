package org.processmining.specpp.headless;

import com.google.common.collect.Lists;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.componenting.traits.ProvidesParameters;
import org.processmining.specpp.composition.StatefulPlaceComposition;
import org.processmining.specpp.composition.composers.AbsoluteFitnessFilter;
import org.processmining.specpp.composition.composers.PlaceComposerWithCIPR;
import org.processmining.specpp.composition.composers.CachingFitnessFilter;
import org.processmining.specpp.config.*;
import org.processmining.specpp.config.components.*;
import org.processmining.specpp.config.parameters.DefaultParameters;
import org.processmining.specpp.config.parameters.ParameterProvider;
import org.processmining.specpp.config.parameters.PlaceGeneratorParameters;
import org.processmining.specpp.config.parameters.TauFitnessThresholds;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.tree.base.impls.EnumeratingTree;
import org.processmining.specpp.datastructures.tree.heuristic.HeuristicTreeExpansion;
import org.processmining.specpp.datastructures.tree.heuristic.TreeNodeScore;
import org.processmining.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.specpp.evaluation.fitness.BaselineFitnessEvaluator;
import org.processmining.specpp.evaluation.heuristics.DirectlyFollowsHeuristic;
import org.processmining.specpp.evaluation.heuristics.EventuallyFollowsTreeHeuristic;
import org.processmining.specpp.evaluation.implicitness.LPBasedImplicitnessCalculator;
import org.processmining.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.specpp.postprocessing.LPBasedImplicitnessPostProcessing;
import org.processmining.specpp.postprocessing.ProMConverter;
import org.processmining.specpp.postprocessing.ReplayBasedImplicitnessPostProcessing;
import org.processmining.specpp.postprocessing.SelfLoopPlaceMerger;
import org.processmining.specpp.preprocessing.orderings.AverageFirstOccurrenceIndex;
import org.processmining.specpp.proposal.ConstrainablePlaceProposer;

import java.util.List;

public class CodeDefinedEvaluationConfig extends SPECppConfigBundleImpl {


    public CodeDefinedEvaluationConfig() {
        super(ConfigFactory.create(createPreProcessingParameters(), createDataExtractionParameters()), createComponentConfig(), createParameterConfig());
    }

    public static PreProcessingParameters createPreProcessingParameters() {
        return new PreProcessingParameters(new XEventNameClassifier(), true);
    }

    public static DataExtractionParameters createDataExtractionParameters() {
        return new DataExtractionParameters(AverageFirstOccurrenceIndex.class);
    }

    public static AlgorithmParameterConfig createParameterConfig() {
        return ConfigFactory.create(new DefaultParameters(), new ParameterProvider() {
            @Override
            public void init() {
                globalComponentSystem().provide(ParameterRequirements.TAU_FITNESS_THRESHOLDS.fulfilWithStatic(TauFitnessThresholds.tau(1)))
                                       .provide(ParameterRequirements.PLACE_GENERATOR_PARAMETERS.fulfilWithStatic(new PlaceGeneratorParameters(6, true, false, false, false)));
            }
        });
    }

    public static ComponentConfig createComponentConfig() {
        // ** Supervision ** //

        SupervisionConfiguration.Configurator svConfig = Configurators.supervisors();

        // ** Evaluation ** //

        EvaluatorConfiguration.Configurator evConfig = Configurators.evaluators()
                                                                    .addEvaluatorProvider(new BaselineFitnessEvaluator.Builder())
                                                                    .addEvaluatorProvider(new LogHistoryMaker.Builder())
                                                                    .addEvaluatorProvider(new LPBasedImplicitnessCalculator.Builder())
                                                                    .addEvaluatorProvider(new DirectlyFollowsHeuristic.Builder());

        HeuristicTreeConfiguration.Configurator<Place, PlaceState, PlaceNode, TreeNodeScore> htConfig = Configurators.<Place, PlaceState, PlaceNode, TreeNodeScore>heuristicTree()
                                                                                                                     .heuristicExpansion(HeuristicTreeExpansion::new)
                                                                                                                     .childGenerationLogic(new MonotonousPlaceGenerationLogic.Builder())
                                                                                                                     .tree(EnumeratingTree::new);
        // tree node heuristic
        //htConfig.heuristic(HeuristicUtils::bfs);
        htConfig.heuristic(new EventuallyFollowsTreeHeuristic.Builder());

        // ** Proposal & Composition ** //

        ProposerComposerConfiguration.Configurator<Place, AdvancedComposition<Place>, CollectionOfPlaces> pcConfig = Configurators.<Place, AdvancedComposition<Place>, CollectionOfPlaces>proposerComposer()
                                                                                                                                  .composition(StatefulPlaceComposition::new)
                                                                                                                                  .proposer(new ConstrainablePlaceProposer.Builder());

        pcConfig.terminalComposer(PlaceComposerWithCIPR::new);
        // without concurrent implicit place removal
        // pcConfig.terminalComposer(PlaceAccepter::new);
        //pcConfig.composerChain(PlaceFitnessFilter::new);
        // pcConfig.composerChain(PlaceFitnessFilter::new, UniwiredComposer::new);
        pcConfig.recursiveComposers(AbsoluteFitnessFilter::new);

        // ** Post Processing ** //

        PostProcessingConfiguration.Configurator<CollectionOfPlaces, CollectionOfPlaces> temp_ppConfig = Configurators.postProcessing();
        // ppConfig.processor(new UniwiredSelfLoopAdditionPostProcessing.Builder());
        // ppConfig.processor(SelfLoopPlaceMerger::new);
        temp_ppConfig.addPostProcessor(new ReplayBasedImplicitnessPostProcessing.Builder())
                     .addPostProcessor(new LPBasedImplicitnessPostProcessing.Builder())
                     .addPostProcessor(SelfLoopPlaceMerger::new);
        PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper> ppConfig = temp_ppConfig.addPostProcessor(ProMConverter::new);

        return new ComponentConfigImpl(svConfig, pcConfig, evConfig, htConfig, ppConfig);
    }

    public static List<ProvidesParameters> createParameterVariations() {
        return Lists.newArrayList(ParameterProvider.of(ParameterRequirements.TAU_FITNESS_THRESHOLDS.fulfilWithStatic(new TauFitnessThresholds(1))));
    }
}
