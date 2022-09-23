package org.processmining.specpp.supervision.observations.performance;

import org.processmining.specpp.supervision.piping.AbstractAsyncAwareObservable;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class TimeStopper extends AbstractAsyncAwareObservable<PerformanceEvent> {

    private final Map<TaskDescription, Long> running;
    private ZoneOffset zoneOffset = ZoneOffset.systemDefault().getRules().getStandardOffset(Instant.now()); // incorrect

    public TimeStopper() {
        running = new HashMap<>();
    }


    public void start(TaskDescription taskDescription) {
        running.put(taskDescription, System.currentTimeMillis());
    }

    public void stop(TaskDescription taskDescription) {
        long stop = System.currentTimeMillis();
        long start = running.remove(taskDescription);
        publish(new PerformanceEvent(taskDescription, new PerformanceMeasurement(Duration.ofMillis(stop - start))));
    }

}
