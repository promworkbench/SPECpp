package org.processmining.specpp.supervision.transformers;

import org.processmining.specpp.datastructures.util.Counter;
import org.processmining.specpp.supervision.observations.ClassKey;
import org.processmining.specpp.supervision.observations.Count;
import org.processmining.specpp.supervision.observations.Event;
import org.processmining.specpp.supervision.observations.EventCountStatistics;
import org.processmining.specpp.supervision.piping.ObservationSummarizer;
import org.processmining.specpp.supervision.piping.Observations;

import java.util.Map;

public class EventCounter implements ObservationSummarizer<Event, EventCountStatistics> {
    @Override
    public EventCountStatistics summarize(Observations<? extends Event> events) {
        Counter<Class<? extends Event>> counts = new Counter<>();
        for (Event e : events) {
            counts.inc(e.getClass());
        }
        EventCountStatistics result = new EventCountStatistics();
        for (Map.Entry<Class<? extends Event>, Integer> entry : counts.entrySet()) {
            ClassKey<Event> key = new ClassKey<>(entry.getKey());
            result.record(key, new Count(entry.getValue()));
        }
        return result;
    }
}
