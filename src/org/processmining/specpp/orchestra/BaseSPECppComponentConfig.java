package org.processmining.specpp.orchestra;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.impls.EventingPlaceComposerWithCIPR;
import org.processmining.specpp.base.impls.EventingPlaceFitnessFilter;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.composition.TrackingPlaceCollection;
import org.processmining.specpp.config.*;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.tree.base.impls.EventingEnumeratingTree;
import org.processmining.specpp.datastructures.tree.heuristic.EventingHeuristicTreeExpansion;
import org.processmining.specpp.datastructures.tree.heuristic.HeuristicUtils;
import org.processmining.specpp.datastructures.tree.heuristic.TreeNodeScore;
import org.processmining.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.specpp.evaluation.fitness.AbsolutelyNoFrillsFitnessEvaluator;
import org.processmining.specpp.evaluation.implicitness.LPBasedImplicitnessCalculator;
import org.processmining.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.specpp.postprocessing.PlaceExporter;
import org.processmining.specpp.postprocessing.ProMConverter;
import org.processmining.specpp.postprocessing.ReplayBasedImplicitnessPostProcessing;
import org.processmining.specpp.postprocessing.SelfLoopPlaceMerger;
import org.processmining.specpp.proposal.RestartablePlaceProposer;
import org.processmining.specpp.supervision.supervisors.AltEventCountsSupervisor;
import org.processmining.specpp.supervision.supervisors.BaseSupervisor;
import org.processmining.specpp.supervision.supervisors.PerformanceSupervisor;
import org.processmining.specpp.supervision.supervisors.TerminalSupervisor;

public class BaseSPECppComponentConfig implements SPECppComponentConfig {

    @Override
    public SupervisionConfiguration getSupervisionConfiguration(GlobalComponentRepository gcr) {
        return Configurators.supervisors()
                            .addSupervisor(BaseSupervisor::new)
                            .addSupervisor(PerformanceSupervisor::new)
                            .addSupervisor(AltEventCountsSupervisor::new)
                            .addSupervisor(TerminalSupervisor::new)
                            .build(gcr);
    }

    @Override
    public EvaluatorConfiguration getEvaluatorConfiguration(GlobalComponentRepository gcr) {
        return Configurators.evaluators()
                            .addEvaluatorProvider(LogHistoryMaker::new)
                            .addEvaluatorProvider(new AbsolutelyNoFrillsFitnessEvaluator.Builder())
                            .addEvaluatorProvider(new LPBasedImplicitnessCalculator.Builder())
                            .build(gcr);
    }

    @Override
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, PetriNet> getProposerComposerConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<Place, AdvancedComposition<Place>, PetriNet>proposerComposer()
                            .proposer(new RestartablePlaceProposer.Builder())
                            .composition(TrackingPlaceCollection::new)
                            .terminalComposer(EventingPlaceComposerWithCIPR::new)
                            .composerChain(EventingPlaceFitnessFilter::new)
                            .build(gcr);
    }

    @Override
    public EfficientTreeConfiguration<Place, PlaceState, PlaceNode> getEfficientTreeConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<Place, PlaceState, PlaceNode, TreeNodeScore>heuristicTree()
                            .heuristic(HeuristicUtils::bfs)
                            .heuristicExpansion(EventingHeuristicTreeExpansion::new)
                            .tree(EventingEnumeratingTree::new)
                            .childGenerationLogic(new MonotonousPlaceGenerationLogic.Builder())
                            .build(gcr);
    }

    @Override
    public PostProcessingConfiguration<PetriNet, ProMPetrinetWrapper> getPostProcessingConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<PetriNet>postProcessing()
                            .addPostProcessor(new ReplayBasedImplicitnessPostProcessing.Builder())
                            .addPostProcessor(SelfLoopPlaceMerger::new)
                            .addPostProcessor(new PlaceExporter.Builder())
                            .addPostProcessor(ProMConverter::new)
                            .build(gcr);
    }
}
