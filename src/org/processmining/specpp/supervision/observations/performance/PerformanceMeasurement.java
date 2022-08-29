package org.processmining.specpp.supervision.observations.performance;

import org.processmining.specpp.supervision.observations.Statistic;

import java.time.Duration;

public class PerformanceMeasurement implements Statistic {
    protected final Duration duration;

    public PerformanceMeasurement(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return duration.toString();
    }
}
