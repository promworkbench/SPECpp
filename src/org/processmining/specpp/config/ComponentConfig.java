package org.processmining.specpp.config;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.DataSourceCollection;
import org.processmining.specpp.componenting.data.StaticDataSource;
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

public interface ComponentConfig {

    default void registerConfigurations(GlobalComponentRepository cr) {
        DataSourceCollection dc = cr.dataSources();
        dc.register(DataRequirements.EVALUATOR_CONFIG, StaticDataSource.of(getEvaluatorConfiguration(cr)));
        dc.register(DataRequirements.SUPERVISOR_CONFIG, StaticDataSource.of(getSupervisionConfiguration(cr)));
        dc.register(DataRequirements.proposerComposerConfiguration(), StaticDataSource.of(getProposerComposerConfiguration(cr)));
        dc.register(DataRequirements.postprocessingConfiguration(), StaticDataSource.of(getPostProcessingConfiguration(cr)));
        dc.register(DataRequirements.efficientTreeConfiguration(), StaticDataSource.of(getEfficientTreeConfiguration(cr)));
    }

    EvaluatorConfiguration getEvaluatorConfiguration(GlobalComponentRepository gcr);

    SupervisionConfiguration getSupervisionConfiguration(GlobalComponentRepository gcr);

    ProposerComposerConfiguration<Place, AdvancedComposition<Place>, CollectionOfPlaces> getProposerComposerConfiguration(GlobalComponentRepository gcr);

    PostProcessingConfiguration<CollectionOfPlaces, ProMPetrinetWrapper> getPostProcessingConfiguration(GlobalComponentRepository gcr);

    EfficientTreeConfiguration<Place, PlaceState, PlaceNode> getEfficientTreeConfiguration(GlobalComponentRepository gcr);


}
