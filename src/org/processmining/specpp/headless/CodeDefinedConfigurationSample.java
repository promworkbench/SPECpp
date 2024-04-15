package org.processmining.specpp.headless;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.composition.BasePlaceComposition;
import org.processmining.specpp.composition.ConstrainingPlaceCollection;
import org.processmining.specpp.composition.StatefulPlaceComposition;
import org.processmining.specpp.composition.composers.AbsoluteFitnessFilter;
import org.processmining.specpp.composition.composers.PlaceAccepter;
import org.processmining.specpp.composition.composers.CachingFitnessFilter;
import org.processmining.specpp.composition.composers.UniwiredComposer;
import org.processmining.specpp.config.*;
import org.processmining.specpp.config.components.*;
import org.processmining.specpp.config.parameters.*;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.PetrinetVisualization;
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
import org.processmining.specpp.orchestra.ExecutionEnvironment;
import org.processmining.specpp.postprocessing.LPBasedImplicitnessPostProcessing;
import org.processmining.specpp.postprocessing.NaiveUniwiredSelfLoopAdditionPostProcessing;
import org.processmining.specpp.postprocessing.ProMConverter;
import org.processmining.specpp.postprocessing.SelfLoopPlaceMerger;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.preprocessing.orderings.Lexicographic;
import org.processmining.specpp.proposal.ConstrainablePlaceProposer;
import org.processmining.specpp.supervision.supervisors.AltEventCountsSupervisor;
import org.processmining.specpp.supervision.supervisors.BaseSupervisor;
import org.processmining.specpp.supervision.supervisors.PerformanceSupervisor;
import org.processmining.specpp.supervision.supervisors.TerminalSupervisor;
import org.processmining.specpp.util.PublicPaths;
import org.processmining.specpp.util.VizUtils;

public class CodeDefinedConfigurationSample {

    public static void main(String[] args) {
        String path = PublicPaths.SAMPLE_EVENTLOG_2;
        SPECppConfigBundle cfg = createConfiguration();
        InputDataBundle data = InputDataBundle.loadAndProcess(path, cfg.getInputProcessingConfig());
        try (ExecutionEnvironment ee = new ExecutionEnvironment()) {
            ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> execution = ee.execute(SPECpp.build(cfg, data), ExecutionParameters.noTimeouts());
            ee.addCompletionCallback(execution, ex -> {
                ProMPetrinetWrapper petrinetWrapper = execution.getSPECpp().getPostProcessedResult();
                VizUtils.showVisualization(PetrinetVisualization.of(petrinetWrapper));
            });
        } catch (InterruptedException ignored) {
        }
    }

    public static SPECppConfigBundle createConfiguration() {
        return ConfigFactory.create(new PreProcessingParameters(new XEventNameClassifier(), true), new DataExtractionParameters(Lexicographic.class), createComponentConfiguration(), new DefaultParameters(), createSpecificParameters());
    }


    public static ComponentConfig createComponentConfiguration() {
        // ** Supervision ** //

        SupervisionConfiguration.Configurator svConfig = Configurators.supervisors().addSupervisor(BaseSupervisor::new);
        svConfig.addSupervisor(PerformanceSupervisor::new).addSupervisor(AltEventCountsSupervisor::new);
        // detailed heuristics logger
        // svConfig.addSupervisor(DetailedHeuristicsSupervisor::new);
        svConfig.addSupervisor(TerminalSupervisor::new);

        // ** Evaluation ** //

        EvaluatorConfiguration.Configurator evConfig = Configurators.evaluators()
                                                                    .addEvaluatorProvider(new BaselineFitnessEvaluator.Builder())
                                                                    .addEvaluatorProvider(new LPBasedImplicitnessCalculator.Builder())
                                                                    .addEvaluatorProvider(new LogHistoryMaker.Builder());
        // delta adaptation function
        // evConfig.addEvaluatorProvider(new SigmoidDelta.Builder());
        // make heuristics available
        evConfig.addEvaluatorProvider(new DirectlyFollowsHeuristic.Builder());

        HeuristicTreeConfiguration.Configurator<Place, PlaceState, PlaceNode, TreeNodeScore> htConfig = Configurators.<Place, PlaceState, PlaceNode, TreeNodeScore>heuristicTree()
                                                                                                                     .heuristicExpansion(HeuristicTreeExpansion::new)
                                                                                                                     .childGenerationLogic(new MonotonousPlaceGenerationLogic.Builder())
                                                                                                                     .tree(EnumeratingTree::new);
        // tree node heuristic
        htConfig.heuristic(new EventuallyFollowsTreeHeuristic.Builder());

        // ** Proposal & Composition ** //

        ProposerComposerConfiguration.Configurator<Place, AdvancedComposition<Place>, CollectionOfPlaces> pcConfig = Configurators.<Place, AdvancedComposition<Place>, CollectionOfPlaces>proposerComposer()
                                                                                                                                  .terminalComposition(StatefulPlaceComposition::new)
                                                                                                                                  .recursiveCompositions(ConstrainingPlaceCollection::new)
                                                                                                                                  .proposer(new ConstrainablePlaceProposer.Builder());

        pcConfig.terminalComposer(PlaceAccepter::new)
                .recursiveComposers(AbsoluteFitnessFilter::new, UniwiredComposer::new);
        // without concurrent implicit place removal
        // pcConfig.terminalComposer(PlaceAccepter::new);
        // pcConfig.composerChain(PlaceFitnessFilter::new, UniwiredComposer::new);
        // pcConfig.composerChain(PlaceFitnessFilter::new, DeltaComposer::new);

        // ** Post Processing ** //

        PostProcessingConfiguration.Configurator<CollectionOfPlaces, CollectionOfPlaces> temp_ppConfig = Configurators.postProcessing();
        temp_ppConfig.addPostProcessor(new NaiveUniwiredSelfLoopAdditionPostProcessing.Builder());
        // ppConfig.processor(SelfLoopPlaceMerger::new);
        temp_ppConfig.addPostProcessor(new LPBasedImplicitnessPostProcessing.Builder())
                     .addPostProcessor(SelfLoopPlaceMerger::new);
        PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper> ppConfig = temp_ppConfig.addPostProcessor(ProMConverter::new);

        // ** Parameters ** //


        return new ComponentConfigImpl(svConfig, pcConfig, evConfig, htConfig, ppConfig);
    }


    public static ParameterProvider createSpecificParameters() {
        return new ParameterProvider() {
            @Override
            public void init() {
                globalComponentSystem()
                        //.provide(ParameterRequirements.DELTA_PARAMETERS.fulfilWithStatic(DeltaParameters.delta(0.75)))
                        .provide(ParameterRequirements.PLACE_GENERATOR_PARAMETERS.fulfilWithStatic(new PlaceGeneratorParameters(12, true, true, false, false)))
                        .provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWithStatic(SupervisionParameters.instrumentNone(false, false)));
            }
        };
    }



    /*
    DoubleFunction<ParameterProvider> f = d -> new ParameterProvider() {
            @Override
            public void init() {
                globalComponentSystem().provide(ParameterRequirements.DELTA_PARAMETERS.fulfilWithStatic(DeltaParameters.delta(d)));
            }
        };
        DoubleFunction<ConfiguratorCollection> deltarized = d -> confSource.getData().reparameterize(f.apply(d));
     */

}
