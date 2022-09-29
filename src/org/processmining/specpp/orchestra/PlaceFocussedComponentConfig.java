package org.processmining.specpp.orchestra;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.impls.BasePlaceComposition;
import org.processmining.specpp.base.impls.PlaceAccepter;
import org.processmining.specpp.base.impls.PlaceFitnessFilter;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.config.Configurators;
import org.processmining.specpp.config.PostProcessingConfiguration;
import org.processmining.specpp.config.ProposerComposerConfiguration;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.postprocessing.PlaceExporter;
import org.processmining.specpp.postprocessing.ProMConverter;
import org.processmining.specpp.proposal.ConstrainablePlaceProposer;

public class PlaceFocussedComponentConfig extends LightweightComponentConfig {
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
                            .composerChain(PlaceFitnessFilter::new)
                            .build(gcr);
    }

}
