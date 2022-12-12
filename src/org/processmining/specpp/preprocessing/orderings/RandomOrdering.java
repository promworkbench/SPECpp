package org.processmining.specpp.preprocessing.orderings;

import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.util.ImmutablePair;
import org.processmining.specpp.datastructures.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class RandomOrdering extends ActivityOrderingStrategy {
    public RandomOrdering(Log log, Map<String, Activity> activityMapping) {
        super(log, activityMapping);
    }

    @Override
    public Pair<Comparator<Activity>> computeActivityOrderings(Log log, Map<String, Activity> mapping) {
        ArrayList<Activity> presetOrder = new ArrayList<>(mapping.values());
        ArrayList<Activity> postsetOrder = new ArrayList<>(mapping.values());
        Collections.shuffle(presetOrder);
        Collections.shuffle(postsetOrder);

        return new ImmutablePair<>(Comparator.comparingInt(presetOrder::indexOf), Comparator.comparingInt(postsetOrder::indexOf));
    }
}
