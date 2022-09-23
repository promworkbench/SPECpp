package org.processmining.specpp.datastructures.petri;

public class InitialFinalTransition extends Transition implements Initial, Final {
    public InitialFinalTransition(String label) {
        super(label);
    }
}
