package org.processmining.specpp.supervision.observations.performance;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimedPerformanceMeasurement extends PerformanceMeasurement {
    public LocalDateTime getTime() {
        return time;
    }

    private final LocalDateTime time;

    public TimedPerformanceMeasurement(LocalDateTime time, Duration duration) {
        super(duration);
        this.time = time;
    }

    @Override
    public String toString() {
        return duration.toString() + " @ " + time.toString();
    }
}
