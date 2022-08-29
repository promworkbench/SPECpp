package org.processmining.specpp.supervision.observations;

import org.processmining.specpp.traits.Mergeable;
import org.processmining.specpp.traits.PrettyPrintable;
import org.processmining.specpp.traits.ProperlyPrintable;

import java.util.*;

public class Statistics<K extends StatisticKey, S extends Statistic> implements Observation, Mergeable<Statistics<? extends K, ? extends S>>, ProperlyPrintable, PrettyPrintable {

    private final Map<K, S> internal;

    public Statistics() {
        internal = new HashMap<>();
    }

    public Statistics(Map<K, S> input) {
        internal = new HashMap<>(input);
    }

    public void record(K key, S statistic) {
        internal.put(key, statistic);
    }

    public Set<Map.Entry<K, S>> getRecords() {
        return internal.entrySet();
    }

    @Override
    public String toString() {
        return internal.toString();
    }


    @Override
    public String toPrettyString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Statistics:").append(" {").append("\n");
        ArrayList<Map.Entry<K, S>> entries = new ArrayList<>(getRecords());
        entries.sort(Comparator.comparing(e -> e.getKey().toString()));
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<K, S> record = entries.get(i);
            sb.append("\t").append(record);
            if (i < entries.size() - 1) sb.append("\n");
        }
        return sb.append("}").toString();
    }

    @Override
    public void merge(Statistics<? extends K, ? extends S> other) {
        for (Map.Entry<? extends K, ? extends S> entry : other.internal.entrySet()) {
            K key = entry.getKey();
            S value = entry.getValue();
            if (!internal.containsKey(key)) record(key, value);
            else {
                S statistic = internal.get(key);
                if (statistic.getClass()
                             .isAssignableFrom(value.getClass()) && statistic instanceof Mergeable && value instanceof Mergeable) {
                    Mergeable<S> mergeable = (Mergeable<S>) statistic;
                    mergeable.merge(value);
                }
            }
        }
    }
}
