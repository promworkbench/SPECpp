package org.processmining.specpp.headless;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.composition.ConstrainingPlaceCollection;
import org.processmining.specpp.composition.StatefulPlaceComposition;
import org.processmining.specpp.composition.composers.PlaceComposerWithCIPR;
import org.processmining.specpp.composition.composers.PlaceFitnessFilter;
import org.processmining.specpp.config.*;
import org.processmining.specpp.config.parameters.ParameterProvider;
import org.processmining.specpp.config.parameters.PlaceGeneratorParameters;
import org.processmining.specpp.config.parameters.SupervisionParameters;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.tree.base.impls.EnumeratingTree;
import org.processmining.specpp.datastructures.tree.heuristic.HeuristicTreeExpansion;
import org.processmining.specpp.datastructures.tree.heuristic.TreeNodeScore;
import org.processmining.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.specpp.evaluation.fitness.AbsolutelyNoFrillsFitnessEvaluator;
import org.processmining.specpp.evaluation.heuristics.DirectlyFollowsHeuristic;
import org.processmining.specpp.evaluation.heuristics.EventuallyFollowsTreeHeuristic;
import org.processmining.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.orchestra.SPECppOperations;
import org.processmining.specpp.postprocessing.LPBasedImplicitnessPostProcessing;
import org.processmining.specpp.postprocessing.ProMConverter;
import org.processmining.specpp.postprocessing.ReplayBasedImplicitnessPostProcessing;
import org.processmining.specpp.postprocessing.SelfLoopPlaceMerger;
import org.processmining.specpp.preprocessing.InputData;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.prom.mvc.config.ConfiguratorCollection;
import org.processmining.specpp.proposal.ConstrainablePlaceProposer;
import org.processmining.specpp.supervision.supervisors.AltEventCountsSupervisor;
import org.processmining.specpp.supervision.supervisors.BaseSupervisor;
import org.processmining.specpp.supervision.supervisors.PerformanceSupervisor;
import org.processmining.specpp.supervision.supervisors.TerminalSupervisor;
import org.processmining.specpp.util.PublicPaths;

public class CodeDefinedConfigurationSample {

    public static void main(String[] args) {
        String path = PublicPaths.SAMPLE_EVENTLOG_2;
        PreProcessingParameters prePar = PreProcessingParameters.getDefault();
        DataSource<InputDataBundle> dataSource = InputData.loadData(path, prePar);
        ConfiguratorCollection configuration = CodeDefinedConfigurationSample.createConfiguration();
        SPECppOperations.configureAndExecute(configuration, dataSource.getData(), true);
    }


    public static ConfiguratorCollection createConfiguration() {
        // ** Supervision ** //

        SupervisionConfiguration.Configurator svConfig = Configurators.supervisors().addSupervisor(BaseSupervisor::new);
        svConfig.addSupervisor(PerformanceSupervisor::new).addSupervisor(AltEventCountsSupervisor::new);
        // detailed heuristics logger
        // svConfig.addSupervisor(DetailedHeuristicsSupervisor::new);
        svConfig.addSupervisor(TerminalSupervisor::new);

        // ** Evaluation ** //

        EvaluatorConfiguration.Configurator evConfig = Configurators.evaluators()
                                                                    .addEvaluatorProvider(new AbsolutelyNoFrillsFitnessEvaluator.Builder())
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
                                                                                                                                  .nestedComposition(StatefulPlaceComposition::new, ConstrainingPlaceCollection::new)
                                                                                                                                  .proposer(new ConstrainablePlaceProposer.Builder());

        pcConfig.terminalComposer(PlaceComposerWithCIPR::new);
        // without concurrent implicit place removal
        // pcConfig.terminalComposer(PlaceAccepter::new);
        pcConfig.composerChain(PlaceFitnessFilter::new);
        // pcConfig.composerChain(PlaceFitnessFilter::new, UniwiredComposer::new);
        // pcConfig.composerChain(PlaceFitnessFilter::new, DeltaComposer::new);

        // ** Post Processing ** //

        PostProcessingConfiguration.Configurator<CollectionOfPlaces, CollectionOfPlaces> temp_ppConfig = Configurators.postProcessing();
        // ppConfig.processor(new UniwiredSelfLoopAdditionPostProcessing.Builder());
        // ppConfig.processor(SelfLoopPlaceMerger::new);
        temp_ppConfig.addPostProcessor(new ReplayBasedImplicitnessPostProcessing.Builder())
                     .addPostProcessor(new LPBasedImplicitnessPostProcessing.Builder())
                     .addPostProcessor(SelfLoopPlaceMerger::new);
        PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper> ppConfig = temp_ppConfig.addPostProcessor(ProMConverter::new);

        // ** Parameters ** //

        ParameterProvider parProv = new ParameterProvider() {
            @Override
            public void init() {
                globalComponentSystem()
                        //.provide(ParameterRequirements.DELTA_PARAMETERS.fulfilWithStatic(DeltaParameters.delta(0.75)))
                        .provide(ParameterRequirements.PLACE_GENERATOR_PARAMETERS.fulfilWithStatic(new PlaceGeneratorParameters(6, true, false, false, false)))
                        .provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWithStatic(SupervisionParameters.instrumentNone(false, false)));
            }
        };

        return new ConfiguratorCollection(svConfig, pcConfig, evConfig, htConfig, ppConfig, parProv);
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
