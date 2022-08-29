package org.processmining.specpp.orchestra;

import org.processmining.specpp.config.parameters.Parameters;
import org.processmining.specpp.preprocessing.AverageTracePositionOrdering;
import org.processmining.specpp.preprocessing.TransitionEncodingsBuilder;

public class PreProcessingParameters implements Parameters {

    private final boolean addStartEndTransitions;
    private final Class<? extends TransitionEncodingsBuilder> transitionEncodingsBuilderClass;

    public PreProcessingParameters(boolean addStartEndTransitions, Class<? extends TransitionEncodingsBuilder> transitionEncodingsBuilderClass) {
        this.addStartEndTransitions = addStartEndTransitions;
        this.transitionEncodingsBuilderClass = transitionEncodingsBuilderClass;
    }

    public static PreProcessingParameters getDefault() {
        return new PreProcessingParameters(true, AverageTracePositionOrdering.class);
    }


    @Override
    public String toString() {
        return "PreProcessingParameters{" +
                "addStartEndTransitions=" + addStartEndTransitions +
                ", transitionEncodingsBuilderClass=" + transitionEncodingsBuilderClass +
                '}';
    }

    public Class<? extends TransitionEncodingsBuilder> getTransitionEncodingsBuilderClass() {
        return transitionEncodingsBuilderClass;
    }

    public boolean isAddStartEndTransitions() {
        return addStartEndTransitions;
    }
}
