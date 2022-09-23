package org.processmining.specpp.supervision.observations.performance;

import org.processmining.specpp.datastructures.util.ImmutableTuple2;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.supervision.piping.AbstractAsyncAwareObservable;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ConcurrentTimeStopper extends AbstractAsyncAwareObservable<PerformanceEvent> {

    private final Map<Tuple2<Thread, TaskDescription>, Long> running;

    public ConcurrentTimeStopper() {
        running = new HashMap<>();
    }


    public void start(TaskDescription taskDescription) {
        running.put(new ImmutableTuple2<>(Thread.currentThread(), taskDescription), System.currentTimeMillis());
    }

    public void stop(TaskDescription taskDescription) {
        long stop = System.currentTimeMillis();
        long start = running.remove(new ImmutableTuple2<>(Thread.currentThread(), taskDescription));
        publish(new PerformanceEvent(taskDescription, new PerformanceMeasurement(Duration.ofMillis(stop - start))));
    }

}
