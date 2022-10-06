package org.processmining.specpp.prom.mvc.config;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.componenting.traits.ProvidesParameters;
import org.processmining.specpp.config.EfficientTreeConfiguration;
import org.processmining.specpp.config.PostProcessingConfiguration;
import org.processmining.specpp.config.ProposerComposerConfiguration;
import org.processmining.specpp.config.SupervisionConfiguration;
import org.processmining.specpp.config.parameters.ParameterProvider;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.specpp.orchestra.*;
import org.processmining.specpp.preprocessing.InputDataBundle;

public class ConfiguratorCollection implements SPECppConfigBundle, SPECppComponentConfig, AlgorithmParameterConfig, DataExtractionConfig {
    private final SupervisionConfiguration.Configurator svCfg;
    private final ProposerComposerConfiguration.Configurator<Place, AdvancedComposition<Place>, CollectionOfPlaces> pcCfg;
    private final EvaluatorConfiguration.Configurator evCfg;
    private final EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode> etCfg;
    private final PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper> ppCfg;
    private final ParameterProvider parProv;
    private final BaseDataExtractionConfig deCfg;

    public ConfiguratorCollection(SupervisionConfiguration.Configurator svCfg, ProposerComposerConfiguration.Configurator<Place, AdvancedComposition<Place>, CollectionOfPlaces> pcCfg, EvaluatorConfiguration.Configurator evCfg, EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode> etCfg, PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper> ppCfg, ParameterProvider parProv) {
        this.svCfg = svCfg;
        this.pcCfg = pcCfg;
        this.evCfg = evCfg;
        this.etCfg = etCfg;
        this.ppCfg = ppCfg;
        this.parProv = parProv;
        deCfg = new BaseDataExtractionConfig();
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
    public void registerAlgorithmParameters(GlobalComponentRepository cr) {
        new AdaptedAlgorithmParameterConfig(parProv).registerAlgorithmParameters(cr);
    }

    @Override
    public void registerDataSources(GlobalComponentRepository cr, InputDataBundle bundle) {
        deCfg.registerDataSources(cr, bundle);
    }

    @Override
    public String getTitle() {
        return "ProM Customized Config";
    }

    @Override
    public String getDescription() {
        return getTitle();
    }

    @Override
    public void instantiate(GlobalComponentRepository cr, InputDataBundle bundle) {
        registerDataSources(cr, bundle);
        registerConfigurations(cr);
        registerAlgorithmParameters(cr);
    }

    public ConfiguratorCollection reparameterize(ParameterProvider newParProv) {
        return new ConfiguratorCollection(svCfg, pcCfg, evCfg, etCfg, ppCfg, newParProv);
    }

}
