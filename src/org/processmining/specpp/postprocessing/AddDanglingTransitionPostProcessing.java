package org.processmining.specpp.postprocessing;

import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.petri.Transition;

import java.util.Set;
import java.util.stream.Collectors;

public class AddDanglingTransitionPostProcessing implements PostProcessor<ProMPetrinetWrapper, ProMPetrinetWrapper> {

    private final Set<Transition> allTransitions;

    public static class Builder extends ComponentSystemAwareBuilder<AddDanglingTransitionPostProcessing> {

        private final DelegatingDataSource<IntEncodings<Transition>> encTransSource = new DelegatingDataSource<>();

        public Builder() {
            globalComponentSystem().require(DataRequirements.ENC_TRANS, encTransSource);
        }

        @Override
        protected AddDanglingTransitionPostProcessing buildIfFullySatisfied() {
            return new AddDanglingTransitionPostProcessing(encTransSource.getData().domainUnion());
        }
    }

    public AddDanglingTransitionPostProcessing(Set<Transition> allTransitions) {
        this.allTransitions = allTransitions;
    }

    @Override
    public ProMPetrinetWrapper postProcess(ProMPetrinetWrapper input) {
        ProMPetrinetWrapper copy = input.copy();
        Set<String> collect = copy.getTransitions()
                                  .stream()
                                  .map(AbstractGraphElement::getLabel)
                                  .collect(Collectors.toSet());
        for (Transition transition : allTransitions) {
            String s = transition.toString();
            if (!collect.contains(s)) {
                copy.addTransition(s);
            }
        }
        return copy;
    }

    @Override
    public Class<ProMPetrinetWrapper> getInputClass() {
        return ProMPetrinetWrapper.class;
    }

    @Override
    public Class<ProMPetrinetWrapper> getOutputClass() {
        return ProMPetrinetWrapper.class;
    }
}
