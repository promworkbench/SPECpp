package org.processmining.specpp.orchestra;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.impls.PlaceAccepter;
import org.processmining.specpp.base.impls.PlaceFitnessFilter;
import org.processmining.specpp.base.impls.QueueingDeltaComposer;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.composition.TrackingPlaceCollection;
import org.processmining.specpp.config.Configurators;
import org.processmining.specpp.config.EfficientTreeConfiguration;
import org.processmining.specpp.config.ProposerComposerConfiguration;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.tree.base.impls.EventingEnumeratingTree;
import org.processmining.specpp.datastructures.tree.heuristic.EventingHeuristicTreeExpansion;
import org.processmining.specpp.datastructures.tree.heuristic.HeuristicUtils;
import org.processmining.specpp.datastructures.tree.heuristic.TreeNodeScore;
import org.processmining.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.specpp.evaluation.fitness.AbsolutelyNoFrillsFitnessEvaluator;
import org.processmining.specpp.evaluation.heuristics.ConstantDelta;
import org.processmining.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.specpp.proposal.ConstrainablePlaceProposer;

public class TauDeltaComponentConfig extends BaseSPECppComponentConfig {

    @Override
    public EvaluatorConfiguration getEvaluatorConfiguration(GlobalComponentRepository gcr) {
        return Configurators.evaluators()
                            .evaluatorProvider(LogHistoryMaker::new)
                            .evaluatorProvider(new AbsolutelyNoFrillsFitnessEvaluator.Builder())
                            .evaluatorProvider(new ConstantDelta.Builder())
                            .build(gcr);
    }

    @Override
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, PetriNet> getProposerComposerConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<Place, AdvancedComposition<Place>, PetriNet>proposerComposer()
                            .proposer(new ConstrainablePlaceProposer.Builder())
                            .composition(TrackingPlaceCollection::new)
                            .terminalComposer(PlaceAccepter::new)
                            .composerChain(PlaceFitnessFilter::new, QueueingDeltaComposer::new)
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

}
