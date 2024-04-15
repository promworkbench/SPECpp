package org.processmining.specpp.config.presets;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.composition.StatefulPlaceComposition;
import org.processmining.specpp.composition.composers.PlaceComposerWithCIPR;
import org.processmining.specpp.composition.composers.AbsoluteFitnessFilter;
import org.processmining.specpp.config.components.Configurators;
import org.processmining.specpp.config.components.EfficientTreeConfiguration;
import org.processmining.specpp.config.components.ProposerComposerConfiguration;
import org.processmining.specpp.config.components.SupervisionConfiguration;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.tree.base.impls.EnumeratingTree;
import org.processmining.specpp.datastructures.tree.base.impls.VariableExpansion;
import org.processmining.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.specpp.proposal.ConstrainablePlaceProposer;
import org.processmining.specpp.supervision.supervisors.BaseSupervisor;
import org.processmining.specpp.supervision.supervisors.PerformanceSupervisor;
import org.processmining.specpp.supervision.supervisors.TerminalSupervisor;

public class LightweightComponentConfig extends BaseComponentConfig {

    @Override
    public SupervisionConfiguration getSupervisionConfiguration(GlobalComponentRepository gcr) {
        return Configurators.supervisors()
                            .addSupervisor(BaseSupervisor::new)
                            .addSupervisor(PerformanceSupervisor::new)
                            .addSupervisor(TerminalSupervisor::new)
                            .build(gcr);
    }

    @Override
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, CollectionOfPlaces> getProposerComposerConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<Place, AdvancedComposition<Place>, CollectionOfPlaces>proposerComposer()
                            .proposer(new ConstrainablePlaceProposer.Builder())
                            .composition(StatefulPlaceComposition::new)
                            .composer(PlaceComposerWithCIPR::new)
                            .recursiveComposers(AbsoluteFitnessFilter::new)
                            .build(gcr);
    }

    @Override
    public EfficientTreeConfiguration<Place, PlaceState, PlaceNode> getEfficientTreeConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<Place, PlaceState, PlaceNode>generatingTree()
                            .childGenerationLogic(new MonotonousPlaceGenerationLogic.Builder())
                            .expansionStrategy(VariableExpansion::dfs)
                            .tree(EnumeratingTree::new)
                            .build(gcr);
    }

}
