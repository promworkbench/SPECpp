package org.processmining.specpp.config;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.config.components.EfficientTreeConfiguration;
import org.processmining.specpp.config.components.PostProcessingConfiguration;
import org.processmining.specpp.config.components.ProposerComposerConfiguration;
import org.processmining.specpp.config.components.SupervisionConfiguration;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;

public class ComponentConfigImpl implements ComponentConfig {
    private final SupervisionConfiguration.Configurator svCfg;
    private final ProposerComposerConfiguration.Configurator<Place, AdvancedComposition<Place>, CollectionOfPlaces> pcCfg;
    private final EvaluatorConfiguration.Configurator evCfg;
    private final EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode> etCfg;
    private final PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper> ppCfg;

    public ComponentConfigImpl(SupervisionConfiguration.Configurator svCfg, ProposerComposerConfiguration.Configurator<Place, AdvancedComposition<Place>, CollectionOfPlaces> pcCfg, EvaluatorConfiguration.Configurator evCfg, EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode> etCfg, PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper> ppCfg) {
        this.svCfg = svCfg;
        this.pcCfg = pcCfg;
        this.evCfg = evCfg;
        this.etCfg = etCfg;
        this.ppCfg = ppCfg;
    }

    @Override
    public EvaluatorConfiguration getEvaluatorConfiguration(GlobalComponentRepository gcr) {
        return evCfg.build(gcr);
    }

    @Override
    public SupervisionConfiguration getSupervisionConfiguration(GlobalComponentRepository gcr) {
        return svCfg.build(gcr);
    }

    @Override
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, CollectionOfPlaces> getProposerComposerConfiguration(GlobalComponentRepository gcr) {
        return pcCfg.build(gcr);
    }

    @Override
    public PostProcessingConfiguration<CollectionOfPlaces, ProMPetrinetWrapper> getPostProcessingConfiguration(GlobalComponentRepository gcr) {
        return ppCfg.build(gcr);
    }

    @Override
    public EfficientTreeConfiguration<Place, PlaceState, PlaceNode> getEfficientTreeConfiguration(GlobalComponentRepository gcr) {
        return etCfg.build(gcr);
    }

    @Override
    public String toString() {
        return "ComponentConfigImpl{}";
    }

}
