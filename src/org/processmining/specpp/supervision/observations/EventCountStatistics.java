package org.processmining.specpp.supervision.observations;

import java.util.Map;

public class EventCountStatistics extends CountStatistics<ClassKey<Event>> {
    public EventCountStatistics() {
    }

    public EventCountStatistics(Map<ClassKey<Event>, Count> input) {
        super(input);
    }

    @Override
    public String toPrettyString() {
        return "Event" + super.toPrettyString();
    }
}
