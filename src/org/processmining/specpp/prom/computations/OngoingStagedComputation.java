package org.processmining.specpp.prom.computations;

import java.util.concurrent.atomic.AtomicInteger;

public class OngoingStagedComputation extends OngoingComputation {
    private final int stages;
    private final AtomicInteger lastCompletedStage;

    public OngoingStagedComputation(int stages) {
        this.stages = stages;
        lastCompletedStage = new AtomicInteger(-1);
    }

    public int getStageCount() {
        return stages;
    }

    public void incStage() {
        int i = lastCompletedStage.incrementAndGet();
        publish(new ComputationStageCompleted(i));
    }


}
