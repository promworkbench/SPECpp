package org.processmining.specpp.prom.computations;

import java.time.LocalDateTime;

public class ComputationEnded extends ComputationEvent {
    private final LocalDateTime end;

    public ComputationEnded(LocalDateTime end) {
        this.end = end;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
