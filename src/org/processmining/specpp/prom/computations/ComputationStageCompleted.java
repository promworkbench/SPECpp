package org.processmining.specpp.prom.computations;

public class ComputationStageCompleted extends ComputationEvent {
    private final int completedStage;

    public int getCompletedStage() {
        return completedStage;
    }

    public ComputationStageCompleted(int completedStage) {
        this.completedStage = completedStage;
    }
}
