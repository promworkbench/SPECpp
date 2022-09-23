package org.processmining.specpp.prom.computations;

public class ComputationCancelled extends ComputationEvent {

    private boolean gracefully;

    public ComputationCancelled(boolean gracefully) {
        this.gracefully = gracefully;
    }
}
