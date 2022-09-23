package org.processmining.specpp.preprocessing.orderings;

import com.google.common.collect.Maps;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.Variant;
import org.processmining.specpp.datastructures.log.impls.IndexedVariant;
import org.processmining.specpp.datastructures.util.ImmutablePair;
import org.processmining.specpp.datastructures.util.Pair;
import org.processmining.specpp.datastructures.vectorization.IntVector;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

public class AverageTraceOccurrence extends ActivityOrderingStrategy {
    public AverageTraceOccurrence(Log log, Map<String, Activity> activityMapping) {
        super(log, activityMapping);
    }

    @Override
    public Pair<Comparator<Activity>> computeActivityOrderings(Log log, Map<String, Activity> mapping) {

        Map<Activity, DoubleSummaryStatistics> stats = mapping.entrySet()
                                                              .stream()
                                                              .collect(Collectors.toMap(Map.Entry::getValue, en -> new DoubleSummaryStatistics()));

        IntVector frequencies = log.getVariantFrequencies();

        for (IndexedVariant indexedVariant : log) {
            Variant v = indexedVariant.getVariant();
            for (Activity a : v) {
                stats.get(a).accept(frequencies.getRelative(indexedVariant.getIndex()));
            }
        }

        Map<Activity, Double> averageTraceFrequency = Maps.transformValues(stats, DoubleSummaryStatistics::getSum);
        Comparator<Activity> comparator = Comparator.<Activity>comparingDouble(averageTraceFrequency::get).reversed();

        return new ImmutablePair<>(comparator, comparator);
    }
}
