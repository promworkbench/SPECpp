package org.processmining.specpp.orchestra;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.impls.BasePlaceCollection;
import org.processmining.specpp.base.impls.PlaceAccepter;
import org.processmining.specpp.base.impls.PlaceFitnessFilter;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.config.Configurators;
import org.processmining.specpp.config.PostProcessingConfiguration;
import org.processmining.specpp.config.ProposerComposerConfiguration;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.postprocessing.PlaceExporter;
import org.processmining.specpp.postprocessing.ProMConverter;
import org.processmining.specpp.proposal.ConstrainablePlaceProposer;

public class PlaceFocussedComponentConfig extends LightweightComponentConfig {
    @Override
    public PostProcessingConfiguration<PetriNet, ProMPetrinetWrapper> getPostProcessingConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<PetriNet>postProcessing()
                            .processor(new PlaceExporter.Builder())
                            .processor(ProMConverter::new)
                            .build(gcr);
    }

    @Override
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, PetriNet> getProposerComposerConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<Place, AdvancedComposition<Place>, PetriNet>proposerComposer()
                            .proposer(new ConstrainablePlaceProposer.Builder())
                            .composition(BasePlaceCollection::new)
                            .composer(PlaceAccepter::new)
                            .composerChain(PlaceFitnessFilter::new)
                            .build(gcr);
    }

}
