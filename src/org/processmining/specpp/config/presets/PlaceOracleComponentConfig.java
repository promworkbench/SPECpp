package org.processmining.specpp.config.presets;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.composition.BasePlaceComposition;
import org.processmining.specpp.composition.composers.PlaceAccepter;
import org.processmining.specpp.composition.composers.PlaceFitnessFilter;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.config.components.Configurators;
import org.processmining.specpp.config.components.PostProcessingConfiguration;
import org.processmining.specpp.config.components.ProposerComposerConfiguration;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.postprocessing.PlaceExporter;
import org.processmining.specpp.postprocessing.ProMConverter;
import org.processmining.specpp.proposal.ConstrainablePlaceProposer;

public class PlaceOracleComponentConfig extends LightweightComponentConfig {
    @Override
    public PostProcessingConfiguration<CollectionOfPlaces, ProMPetrinetWrapper> getPostProcessingConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<CollectionOfPlaces>postProcessing()
                            .addPostProcessor(new PlaceExporter.Builder())
                            .addPostProcessor(ProMConverter::new)
                            .build(gcr);
    }

    @Override
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, CollectionOfPlaces> getProposerComposerConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<Place, AdvancedComposition<Place>, CollectionOfPlaces>proposerComposer()
                            .proposer(new ConstrainablePlaceProposer.Builder())
                            .composition(BasePlaceComposition::new)
                            .composer(PlaceAccepter::new)
                            .recursiveComposers(PlaceFitnessFilter::new)
                            .build(gcr);
    }

}
