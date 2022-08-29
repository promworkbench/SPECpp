package org.processmining.specpp.preprocessing;

import com.google.common.collect.Maps;
import org.apache.commons.collections4.BidiMap;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.Variant;
import org.processmining.specpp.datastructures.log.impls.IndexedVariant;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.ImmutablePair;
import org.processmining.specpp.datastructures.util.Pair;

import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

public class AverageTracePositionOrdering extends TransitionEncodingsBuilder {
    public AverageTracePositionOrdering(Log log, Map<String, Activity> activityMapping, BidiMap<Activity, Transition> transitionMapping) {
        super(log, activityMapping, transitionMapping);
    }

    @Override
    protected Pair<Comparator<Activity>> computeActivityOrderings(Log log, Map<String, Activity> activityMap) {
        Map<Activity, IntSummaryStatistics> stats = activityMap.entrySet()
                                                               .stream()
                                                               .collect(Collectors.toMap(Map.Entry::getValue, en -> new IntSummaryStatistics()));


        for (IndexedVariant indexedVariant : log) {
            Variant variant = indexedVariant.getVariant();
            int variantFrequency = log.getVariantFrequency(indexedVariant.getIndex());
            int j = 0;
            for (Activity activity : variant) {
                stats.get(activity).accept(j * variantFrequency);
                j++;
            }
        }

        Map<Activity, Double> averageVariantPosition = Maps.transformValues(stats, IntSummaryStatistics::getAverage);
        Comparator<Activity> presetComp = Comparator.comparingDouble(averageVariantPosition::get);
        Comparator<Activity> postsetComp = presetComp.reversed();

        return new ImmutablePair<>(presetComp, postsetComp);
    }
}
