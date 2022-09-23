package org.processmining.specpp.prom.computations;

import java.time.LocalDateTime;

public class ComputationStarted extends ComputationEvent {
    private final LocalDateTime start;

    public ComputationStarted(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getStart() {
        return start;
    }
}
