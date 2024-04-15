package org.processmining.specpp.config.presets;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.composition.LightweightPlaceComposition;
import org.processmining.specpp.composition.composers.ETCBasedComposer;
import org.processmining.specpp.composition.composers.AbsoluteFitnessFilter;
import org.processmining.specpp.config.components.Configurators;
import org.processmining.specpp.config.components.ProposerComposerConfiguration;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.proposal.ConstrainablePlaceProposer;

public class ETCComponentConfig extends BaseComponentConfig {

    @Override
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, CollectionOfPlaces> getProposerComposerConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<Place, AdvancedComposition<Place>, CollectionOfPlaces>proposerComposer()
                            .proposer(new ConstrainablePlaceProposer.Builder())
                            .composition(LightweightPlaceComposition::new)
                            .terminalComposer(ETCBasedComposer::new)
                            .recursiveComposers(AbsoluteFitnessFilter::new)
                            .build(gcr);
    }

}
