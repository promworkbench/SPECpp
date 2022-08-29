package org.processmining.specpp.supervision.observations.performance;

import org.processmining.specpp.supervision.observations.Event;

public class PerformanceEvent implements Event {

    private final TaskDescription task;
    private final PerformanceMeasurement measurement;

    public PerformanceEvent(TaskDescription task, PerformanceMeasurement measurement) {
        this.task = task;
        this.measurement = measurement;
    }

    public TaskDescription getTask() {
        return task;
    }

    public PerformanceMeasurement getMeasurement() {
        return measurement;
    }

    @Override
    public String toString() {
        return "PerformanceEvent(" + task + ": " + measurement + ")";
    }
}
