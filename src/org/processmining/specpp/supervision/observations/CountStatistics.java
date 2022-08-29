package org.processmining.specpp.supervision.observations;

import java.util.Map;

public class CountStatistics<T extends StatisticKey> extends Statistics<T, Count> {

    public CountStatistics() {
    }

    public CountStatistics(Map<T, Count> input) {
        super(input);
    }

    @Override
    public String toPrettyString() {
        return "Count " + super.toPrettyString();
    }

    @Override
    public String toString() {
        return "CountStatistics:" + super.toString() + "";
    }

}
