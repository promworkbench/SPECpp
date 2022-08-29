package org.processmining.specpp.componenting.system;

import org.processmining.specpp.componenting.data.DataSourceCollection;
import org.processmining.specpp.componenting.data.ParameterSourceCollection;
import org.processmining.specpp.componenting.evaluation.EvaluatorCollection;
import org.processmining.specpp.componenting.supervision.GlobalSupervisorCollection;

public enum ComponentType {

    Evaluation(EvaluatorCollection.class), Data(DataSourceCollection.class), Supervision(GlobalSupervisorCollection.class), Parameters(ParameterSourceCollection.class);

    private final Class<? extends FulfilledRequirementsCollection<?>> collectionClass;

    ComponentType(Class<? extends FulfilledRequirementsCollection<?>> collectionClass) {
        this.collectionClass = collectionClass;
    }

    public Class<? extends FulfilledRequirementsCollection<?>> getCollectionClass() {
        return collectionClass;
    }

    public FulfilledRequirementsCollection<?> createCollection() {
        try {
            return collectionClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
