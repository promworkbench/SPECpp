package org.processmining.specpp.componenting.traits;

import org.processmining.specpp.componenting.evaluation.EvaluatorCollection;
import org.processmining.specpp.componenting.system.ComponentType;

public interface ProvidesEvaluators extends HasComponentCollection {

    default EvaluatorCollection evaluators() {
        return ((EvaluatorCollection) getComponentCollection().getProvisions(ComponentType.Evaluation));
    }

}
