package org.processmining.specpp.preprocessing.orderings;

import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.util.ImmutablePair;
import org.processmining.specpp.datastructures.util.Pair;

import java.util.Comparator;
import java.util.Map;

public class Lexicographic extends ActivityOrderingStrategy {
    public Lexicographic(Log log, Map<String, Activity> activityMapping) {
        super(log, activityMapping);
    }

    @Override
    public Pair<Comparator<Activity>> computeActivityOrderings(Log log, Map<String, Activity> mapping) {
        Comparator<Activity> presetComparator = Comparator.comparing(Activity::toString);
        return new ImmutablePair<>(presetComparator, presetComparator);
    }
}
