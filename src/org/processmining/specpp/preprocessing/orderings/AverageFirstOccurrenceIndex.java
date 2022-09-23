package org.processmining.specpp.preprocessing.orderings;

import com.google.common.collect.Maps;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.Variant;
import org.processmining.specpp.datastructures.log.impls.IndexedVariant;
import org.processmining.specpp.datastructures.util.ImmutablePair;
import org.processmining.specpp.datastructures.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class AverageFirstOccurrenceIndex extends ActivityOrderingStrategy {
    public AverageFirstOccurrenceIndex(Log log, Map<String, Activity> activityMapping) {
        super(log, activityMapping);
    }

    @Override
    public Pair<Comparator<Activity>> computeActivityOrderings(Log log, Map<String, Activity> activityMap) {
        Map<Activity, IntSummaryStatistics> stats = activityMap.entrySet()
                                                               .stream()
                                                               .collect(Collectors.toMap(Map.Entry::getValue, en -> new IntSummaryStatistics()));


        for (IndexedVariant indexedVariant : log) {
            Set<Activity> seen = new HashSet<>();
            Variant variant = indexedVariant.getVariant();
            int variantFrequency = log.getVariantFrequency(indexedVariant.getIndex());
            int j = 0;
            for (Activity activity : variant) {
                if (!seen.contains(activity)) stats.get(activity).accept(j * variantFrequency);
                j++;
                seen.add(activity);
            }
        }

        Map<Activity, Double> averageVariantPosition = Maps.transformValues(stats, IntSummaryStatistics::getAverage);
        Comparator<Activity> presetComp = Comparator.comparingDouble(averageVariantPosition::get);
        Comparator<Activity> postsetComp = presetComp.reversed();

        return new ImmutablePair<>(presetComp, postsetComp);
    }
}
