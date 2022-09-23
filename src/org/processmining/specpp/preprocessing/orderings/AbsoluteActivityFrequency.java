package org.processmining.specpp.preprocessing.orderings;

import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.Variant;
import org.processmining.specpp.datastructures.log.impls.IndexedVariant;
import org.processmining.specpp.datastructures.util.Counter;
import org.processmining.specpp.datastructures.util.ImmutablePair;
import org.processmining.specpp.datastructures.util.Pair;

import java.util.Comparator;
import java.util.Map;

public class AbsoluteActivityFrequency extends ActivityOrderingStrategy {
    public AbsoluteActivityFrequency(Log log, Map<String, Activity> activityMapping) {
        super(log, activityMapping);
    }

    @Override
    public Pair<Comparator<Activity>> computeActivityOrderings(Log log, Map<String, Activity> mapping) {

        Counter<Activity> counter = new Counter<>();

        for (IndexedVariant indexedVariant : log) {
            Variant v = indexedVariant.getVariant();
            for (Activity a : v) {
                counter.inc(a, log.getVariantFrequency(indexedVariant.getIndex()));
            }
        }

        Comparator<Activity> comparator = Comparator.<Activity>comparingInt(counter::get).reversed();

        return new ImmutablePair<>(comparator, comparator);
    }
}
