package org.processmining.specpp.config.presets;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.composition.ConstrainingPlaceCollection;
import org.processmining.specpp.composition.StatefulPlaceComposition;
import org.processmining.specpp.composition.composers.PlaceAccepter;
import org.processmining.specpp.composition.composers.AbsoluteFitnessFilter;
import org.processmining.specpp.composition.composers.UniwiredComposer;
import org.processmining.specpp.config.components.Configurators;
import org.processmining.specpp.config.components.EfficientTreeConfiguration;
import org.processmining.specpp.config.components.PostProcessingConfiguration;
import org.processmining.specpp.config.components.ProposerComposerConfiguration;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.tree.base.impls.EventingEnumeratingTree;
import org.processmining.specpp.datastructures.tree.heuristic.EventingHeuristicTreeExpansion;
import org.processmining.specpp.datastructures.tree.heuristic.HeuristicUtils;
import org.processmining.specpp.datastructures.tree.heuristic.TreeNodeScore;
import org.processmining.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.specpp.evaluation.fitness.BaselineFitnessEvaluator;
import org.processmining.specpp.evaluation.heuristics.DirectlyFollowsHeuristic;
import org.processmining.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.specpp.postprocessing.ProMConverter;
import org.processmining.specpp.proposal.RestartablePlaceProposer;

public class UniwiredComponentConfig extends BaseComponentConfig {

    @Override
    public EvaluatorConfiguration getEvaluatorConfiguration(GlobalComponentRepository gcr) {
        return Configurators.evaluators()
                            .addEvaluatorProvider(LogHistoryMaker::new)
                            .addEvaluatorProvider(new BaselineFitnessEvaluator.Builder())
                            .addEvaluatorProvider(new DirectlyFollowsHeuristic.Builder())
                            .build(gcr);
    }

    @Override
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, CollectionOfPlaces> getProposerComposerConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<Place, AdvancedComposition<Place>, CollectionOfPlaces>proposerComposer()
                            .proposer(new RestartablePlaceProposer.Builder())
                            .terminalComposition(StatefulPlaceComposition::new)
                            .recursiveCompositions(ConstrainingPlaceCollection::new)
                            .terminalComposer(PlaceAccepter::new)
                            .recursiveComposers(AbsoluteFitnessFilter::new, UniwiredComposer::new)
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
    public PostProcessingConfiguration<CollectionOfPlaces, ProMPetrinetWrapper> getPostProcessingConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<CollectionOfPlaces>postProcessing()
                            //.instrumentedProcessor("ReplayBasedImplicitness", new ReplayBasedImplicitnessPostProcessing.Builder())
                            //.instrumentedProcessor("SelfLoopPlaceMerger", SelfLoopPlaceMerger::new)
                            .addPostProcessor(ProMConverter::new).build(gcr);
    }

}
