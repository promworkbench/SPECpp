package org.processmining.specpp.orchestra;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.impls.PlaceAccepter;
import org.processmining.specpp.base.impls.PlaceFitnessFilter;
import org.processmining.specpp.base.impls.UniwiredComposer;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.composition.ConstrainingPlaceCollection;
import org.processmining.specpp.composition.TrackingPlaceCollection;
import org.processmining.specpp.config.Configurators;
import org.processmining.specpp.config.EfficientTreeConfiguration;
import org.processmining.specpp.config.PostProcessingConfiguration;
import org.processmining.specpp.config.ProposerComposerConfiguration;
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
import org.processmining.specpp.evaluation.heuristics.DirectlyFollowsHeuristic;
import org.processmining.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.specpp.postprocessing.ProMConverter;
import org.processmining.specpp.proposal.RestartablePlaceProposer;

public class UniwiredComponentConfig extends BaseSPECppComponentConfig {

    @Override
    public EvaluatorConfiguration getEvaluatorConfiguration(GlobalComponentRepository gcr) {
        return Configurators.evaluators()
                            .addEvaluatorProvider(LogHistoryMaker::new)
                            .addEvaluatorProvider(new AbsolutelyNoFrillsFitnessEvaluator.Builder())
                            .addEvaluatorProvider(new DirectlyFollowsHeuristic.Builder())
                            .build(gcr);
    }

    @Override
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, PetriNet> getProposerComposerConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<Place, AdvancedComposition<Place>, PetriNet>proposerComposer()
                            .proposer(new RestartablePlaceProposer.Builder())
                            .nestedComposition(TrackingPlaceCollection::new, ConstrainingPlaceCollection::new)
                            .terminalComposer(PlaceAccepter::new)
                            .composerChain(PlaceFitnessFilter::new, UniwiredComposer::new)
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
                            //.instrumentedProcessor("ReplayBasedImplicitness", new ReplayBasedImplicitnessPostProcessing.Builder())
                            //.instrumentedProcessor("SelfLoopPlaceMerger", SelfLoopPlaceMerger::new)
                            .addPostProcessor(ProMConverter::new).build(gcr);
    }

}
