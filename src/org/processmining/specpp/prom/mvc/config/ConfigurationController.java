package org.processmining.specpp.prom.mvc.config;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.impls.IdentityPostProcessor;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.componenting.system.link.ComposerComponent;
import org.processmining.specpp.composition.ConstrainingPlaceCollection;
import org.processmining.specpp.composition.LightweightPlaceComposition;
import org.processmining.specpp.composition.StatefulPlaceComposition;
import org.processmining.specpp.composition.composers.*;
import org.processmining.specpp.config.ComponentConfigImpl;
import org.processmining.specpp.config.ConfigFactory;
import org.processmining.specpp.config.InputProcessingConfig;
import org.processmining.specpp.config.SPECppConfigBundle;
import org.processmining.specpp.config.components.*;
import org.processmining.specpp.config.parameters.*;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.tree.base.impls.EnumeratingTree;
import org.processmining.specpp.datastructures.tree.base.impls.EventingEnumeratingTree;
import org.processmining.specpp.datastructures.tree.base.impls.VariableExpansion;
import org.processmining.specpp.datastructures.tree.heuristic.*;
import org.processmining.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.specpp.evaluation.heuristics.DirectlyFollowsHeuristic;
import org.processmining.specpp.evaluation.heuristics.TreeHeuristicThreshold;
import org.processmining.specpp.evaluation.implicitness.LPBasedImplicitnessCalculator;
import org.processmining.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.specpp.prom.alg.FrameworkBridge;
import org.processmining.specpp.prom.alg.LiveEvents;
import org.processmining.specpp.prom.alg.LivePerformance;
import org.processmining.specpp.prom.mvc.AbstractStageController;
import org.processmining.specpp.prom.mvc.SPECppController;
import org.processmining.specpp.proposal.ConstrainablePlaceProposer;
import org.processmining.specpp.proposal.RestartablePlaceProposer;
import org.processmining.specpp.supervision.supervisors.BaseSupervisor;
import org.processmining.specpp.supervision.supervisors.DetailedHeuristicsSupervisor;
import org.processmining.specpp.supervision.supervisors.TerminalSupervisor;

import javax.swing.*;

public class ConfigurationController extends AbstractStageController {


    public ConfigurationController(SPECppController parentController) {
        super(parentController);
    }

    public static SPECppConfigBundle convertToFullConfig(InputProcessingConfig inputProcessingConfig, ProMConfig pc) {
        // BUILDING CONFIGURATORS

        // ** SUPERVISION ** //

        boolean logToFile = pc.logToFile;
        SupervisionConfiguration.Configurator svCfg = new SupervisionConfiguration.Configurator();
        svCfg.addSupervisor(BaseSupervisor::new);
        boolean isSupervisingEvents = pc.supervisionSetting == ProMConfig.SupervisionSetting.PerformanceAndEvents;
        switch (pc.supervisionSetting) {
            case Nothing:
                break;
            case PerformanceOnly:
                svCfg.addSupervisor(LivePerformance::new);
                break;
            case PerformanceAndEvents:
                svCfg.addSupervisor(LivePerformance::new);
                svCfg.addSupervisor(LiveEvents::new);
                if (pc.logHeuristics) svCfg.addSupervisor(DetailedHeuristicsSupervisor::new);
                break;
        }
        svCfg.addSupervisor(TerminalSupervisor::new);

        // ** PROPOSAL, COMPOSITION ** //

        ProposerComposerConfiguration.Configurator<Place, AdvancedComposition<Place>, CollectionOfPlaces> pcCfg = new ProposerComposerConfiguration.Configurator<>();
        if (pc.supportRestart) pcCfg.proposer(new RestartablePlaceProposer.Builder());
        else pcCfg.proposer(new ConstrainablePlaceProposer.Builder());
        boolean compositionConstraintsRequired = pc.respectWiring || pc.compositionStrategy == ProMConfig.CompositionStrategy.Uniwired;
        boolean compositionStateRequired = pc.compositionStrategy == ProMConfig.CompositionStrategy.TauDelta;
        if (compositionStateRequired) {
            pcCfg.terminalComposition(StatefulPlaceComposition::new);
            if (compositionConstraintsRequired) pcCfg.recursiveCompositions(ConstrainingPlaceCollection::new);
        } else {
            pcCfg.terminalComposition(LightweightPlaceComposition::new);
            if (compositionConstraintsRequired) pcCfg.recursiveCompositions(ConstrainingPlaceCollection::new);
        }

        if (pc.useETCBasedComposer) {
            pcCfg.terminalComposer(ETCBasedComposer::new);
        } else if (pc.ciprVariant != ProMConfig.CIPRVariant.None) {
            pcCfg.terminalComposer(isSupervisingEvents ? EventingPlaceComposerWithCIPR::new : PlaceComposerWithCIPR::new);
        } else {
            pcCfg.terminalComposer(PlaceAccepter::new);
        }

        InitializingBuilder<? extends ComposerComponent<Place, AdvancedComposition<Place>, CollectionOfPlaces>, ComposerComponent<Place, AdvancedComposition<Place>, CollectionOfPlaces>> fitnessFilterBuilder = isSupervisingEvents ? EventingPlaceFitnessFilter::new : PlaceFitnessFilter::new;
        switch (pc.compositionStrategy) {
            case Standard:
                pcCfg.recursiveComposers(fitnessFilterBuilder);
                break;
            case TauDelta:
                pcCfg.recursiveComposers(fitnessFilterBuilder, DeltaComposer::new);
                break;
            case Uniwired:
                pcCfg.recursiveComposers(fitnessFilterBuilder, UniwiredComposer::new);
                break;
        }

        // ** EVALUATION ** //
        EvaluatorConfiguration.Configurator evCfg = new EvaluatorConfiguration.Configurator();
        evCfg.addEvaluatorProvider(LogHistoryMaker::new);
        evCfg.addEvaluatorProvider(new LPBasedImplicitnessCalculator.Builder());
        evCfg.addEvaluatorProvider(new DirectlyFollowsHeuristic.Builder());
        evCfg.addEvaluatorProvider(pc.concurrentReplay ? FrameworkBridge.BridgedEvaluators.ForkJoinFitness.getBridge()
                                                                                                          .getBuilder() : FrameworkBridge.BridgedEvaluators.BaseFitness.getBridge()
                                                                                                                                                                       .getBuilder());
        if (pc.compositionStrategy == ProMConfig.CompositionStrategy.TauDelta)
            evCfg.addEvaluatorProvider(pc.deltaAdaptationFunction.getBuilder());

        EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode> etCfg;
        if (pc.treeExpansionSetting == ProMConfig.TreeExpansionSetting.Heuristic) {
            HeuristicTreeConfiguration.Configurator<Place, PlaceState, PlaceNode, TreeNodeScore> htCfg = new HeuristicTreeConfiguration.Configurator<>();
            htCfg.heuristic(pc.treeHeuristic.getBuilder());
            if (pc.enforceHeuristicThreshold)
                htCfg.heuristicExpansion(isSupervisingEvents ? EventingDiscriminatingHeuristicTreeExpansion::new : DiscriminatingHeuristicTreeExpansion::new);
            else
                htCfg.heuristicExpansion(isSupervisingEvents ? EventingHeuristicTreeExpansion::new : HeuristicTreeExpansion::new);
            htCfg.tree(isSupervisingEvents ? EventingEnumeratingTree::new : EnumeratingTree::new);
            htCfg.childGenerationLogic(new MonotonousPlaceGenerationLogic.Builder());
            etCfg = htCfg;
        } else {
            etCfg = new EfficientTreeConfiguration.Configurator<>();
            etCfg.tree(isSupervisingEvents ? EventingEnumeratingTree::new : EnumeratingTree::new);
            etCfg.expansionStrategy(pc.treeExpansionSetting == ProMConfig.TreeExpansionSetting.BFS ? () -> VariableExpansion.<PlaceNode>bfs() : () -> VariableExpansion.<PlaceNode>dfs());
            etCfg.childGenerationLogic(new MonotonousPlaceGenerationLogic.Builder());
        }

        // ** Post Processing ** //

        PostProcessingConfiguration.Configurator configurator = new PostProcessingConfiguration.Configurator<CollectionOfPlaces, CollectionOfPlaces>(IdentityPostProcessor::new);
        for (FrameworkBridge.AnnotatedPostProcessor annotatedPostProcessor : pc.ppPipeline) {
            configurator = configurator.addPostProcessor(annotatedPostProcessor.getBuilder());
        }
        PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper> ppCfg = (PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper>) configurator;//;.processor(ProMConverter::new);

        // ** PARAMETERS ** //

        ExecutionParameters exp = new ExecutionParameters(new ExecutionParameters.ExecutionTimeLimits(pc.discoveryTimeLimit, null, pc.totalTimeLimit), ExecutionParameters.ParallelizationTarget.Moderate, ExecutionParameters.PerformanceFocus.Balanced);
        PlaceGeneratorParameters pgp = new PlaceGeneratorParameters(pc.depth < 0 ? Integer.MAX_VALUE : pc.depth, true, pc.respectWiring, false, false);

        ParameterProvider pp = new ParameterProvider() {
            @Override
            public void init() {
                globalComponentSystem().provide(ParameterRequirements.EXTERNAL_INITIALIZATION.fulfilWithStatic(new ExternalInitializationParameters(pc.initiallyWireSelfLoops)))
                                       .provide(ParameterRequirements.EXECUTION_PARAMETERS.fulfilWithStatic(exp))
                                       .provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWithStatic(pc.supervisionSetting != ProMConfig.SupervisionSetting.Nothing ? SupervisionParameters.instrumentAll(false, logToFile) : SupervisionParameters.instrumentNone(false, logToFile)))
                                       .provide(ParameterRequirements.TAU_FITNESS_THRESHOLDS.fulfilWithStatic(TauFitnessThresholds.tau(pc.tau)))
                                       .provide(ParameterRequirements.REPLAY_COMPUTATION.fulfilWithStatic(ReplayComputationParameters.permitNegative(pc.permitNegativeMarkingsDuringReplay)))
                                       .provide(ParameterRequirements.IMPLICITNESS_TESTING.fulfilWithStatic(new ImplicitnessTestingParameters(pc.ciprVariant.bridge(), pc.implicitnessReplaySubLogRestriction)))
                                       .provide(ParameterRequirements.PLACE_GENERATOR_PARAMETERS.fulfilWithStatic(pgp))
                                       .provide(ParameterRequirements.OUTPUT_PATH_PARAMETERS.fulfilWithStatic(OutputPathParameters.getDefault()));
                if (pc.useETCBasedComposer) {
                    globalComponentSystem().provide(ParameterRequirements.ETC_BASED_COMPOSER_PARAMETERS.fulfilWithStatic(new ETCBasedComposerParameters(pc.rho)));
                }
                if (pc.compositionStrategy == ProMConfig.CompositionStrategy.TauDelta) {
                    globalComponentSystem().provide(ParameterRequirements.DELTA_PARAMETERS.fulfilWithStatic(new DeltaParameters(pc.delta, pc.steepness)))
                                           .provide(ParameterRequirements.DELTA_COMPOSER_PARAMETERS.fulfilWithStatic(DeltaComposerParameters.getDefault()));
                }
                if (pc.enforceHeuristicThreshold)
                    globalComponentSystem().provide(ParameterRequirements.TREE_HEURISTIC_THRESHOLD.fulfilWithStatic(new TreeHeuristicThreshold(pc.heuristicThreshold, pc.heuristicThresholdRelation)));

            }
        };


        ComponentConfigImpl cc = new ComponentConfigImpl(svCfg, pcCfg, evCfg, etCfg, ppCfg);
        return ConfigFactory.create(inputProcessingConfig, cc, ConfigFactory.create(pp));
    }

    @Override
    public JPanel createPanel() {
        return new ConfigurationPanel(this);
    }

    @Override
    public void startup() {

    }

    public void basicConfigCompleted(ProMConfig basicConfig) {
        SPECppConfigBundle fullConfig = convertToFullConfig(parentController.getInputProcessingConfig(), basicConfig);
        parentController.configCompleted(basicConfig, fullConfig);
    }

}
