package org.processmining.specpp.prom.mvc.config;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.config.EfficientTreeConfiguration;
import org.processmining.specpp.config.PostProcessingConfiguration;
import org.processmining.specpp.config.ProposerComposerConfiguration;
import org.processmining.specpp.config.SupervisionConfiguration;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.specpp.orchestra.*;
import org.processmining.specpp.preprocessing.InputDataBundle;

public class ConfiguratorCollection implements SPECppConfigBundle, SPECppComponentConfig, AlgorithmParameterConfig, DataExtractionConfig {
    private final SupervisionConfiguration.Configurator svCfg;
    private final ProposerComposerConfiguration.Configurator<Place, AdvancedComposition<Place>, PetriNet> pcCfg;
    private final EvaluatorConfiguration.Configurator evCfg;
    private final EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode> etCfg;
    private final PostProcessingConfiguration.Configurator<PetriNet, ProMPetrinetWrapper> ppCfg;
    private final AdaptedAlgorithmParameterConfig parCfg;
    private final BaseDataExtractionConfig deCfg;

    public ConfiguratorCollection(SupervisionConfiguration.Configurator svCfg, ProposerComposerConfiguration.Configurator<Place, AdvancedComposition<Place>, PetriNet> pcCfg, EvaluatorConfiguration.Configurator evCfg, EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode> etCfg, PostProcessingConfiguration.Configurator<PetriNet, ProMPetrinetWrapper> ppCfg, AdaptedAlgorithmParameterConfig parCfg) {
        this.svCfg = svCfg;
        this.pcCfg = pcCfg;
        this.evCfg = evCfg;
        this.etCfg = etCfg;
        this.ppCfg = ppCfg;
        this.parCfg = parCfg;
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
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, PetriNet> getProposerComposerConfiguration(GlobalComponentRepository gcr) {
        return pcCfg.build(gcr);
    }

    @Override
    public PostProcessingConfiguration<PetriNet, ProMPetrinetWrapper> getPostProcessingConfiguration(GlobalComponentRepository gcr) {
        return ppCfg.build(gcr);
    }

    @Override
    public EfficientTreeConfiguration<Place, PlaceState, PlaceNode> getEfficientTreeConfiguration(GlobalComponentRepository gcr) {
        return etCfg.build(gcr);
    }

    @Override
    public void registerAlgorithmParameters(GlobalComponentRepository cr) {
        parCfg.registerAlgorithmParameters(cr);
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
}
