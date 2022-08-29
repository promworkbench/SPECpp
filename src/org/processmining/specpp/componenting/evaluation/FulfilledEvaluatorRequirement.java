package org.processmining.specpp.componenting.evaluation;

import org.processmining.specpp.base.Evaluable;
import org.processmining.specpp.base.Evaluation;
import org.processmining.specpp.base.Evaluator;
import org.processmining.specpp.componenting.system.AbstractFulfilledRequirement;
import org.processmining.specpp.componenting.system.ComponentType;
import org.processmining.specpp.util.JavaTypingUtils;

public class FulfilledEvaluatorRequirement<I extends Evaluable, E extends Evaluation> extends AbstractFulfilledRequirement<Evaluator<I, E>, EvaluatorRequirement<?, ?>> {

    public FulfilledEvaluatorRequirement(EvaluatorRequirement<?, ?> requirement, Evaluator<I, E> delegate) {
        super(requirement, JavaTypingUtils.castClass(Evaluator.class), delegate);
    }

    @Override
    public ComponentType componentType() {
        return requirement.componentType();
    }


}
