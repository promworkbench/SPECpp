package org.processmining.specpp.supervision.observations.performance;

import org.processmining.specpp.supervision.observations.Statistics;
import org.processmining.specpp.traits.PrettyPrintable;

import java.util.Map;

public class PerformanceStatistics extends Statistics<TaskDescription, PerformanceStatistic> implements PrettyPrintable {

    public PerformanceStatistics() {
    }

    public PerformanceStatistics(Map<TaskDescription, PerformanceStatistic> input) {
        super(input);
    }

    @Override
    public String toString() {
        return "PerformanceStatistics:" + super.toString();
    }

    @Override
    public String toPrettyString() {
        return "Performance " + super.toPrettyString();
    }
}
