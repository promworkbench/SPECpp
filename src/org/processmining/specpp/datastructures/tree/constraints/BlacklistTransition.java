package org.processmining.specpp.datastructures.tree.constraints;

import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.tree.base.GenerationConstraint;

public class BlacklistTransition implements GenerationConstraint {
    private final Transition transition;

    public BlacklistTransition(Transition transition) {
        this.transition = transition;
    }

    public Transition getTransition() {
        return transition;
    }

    @Override
    public String toString() {
        return "BlacklistTransitions(" + transition + ")";
    }

}
