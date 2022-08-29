package org.processmining.specpp.preprocessing;

import org.apache.commons.collections4.BidiMap;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.impls.IndexedVariant;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.Counter;
import org.processmining.specpp.datastructures.util.ImmutablePair;
import org.processmining.specpp.datastructures.util.Pair;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TraceFrequencyOrdering extends TransitionEncodingsBuilder {
    public TraceFrequencyOrdering(Log log, Map<String, Activity> activityMapping, BidiMap<Activity, Transition> transitionMapping) {
        super(log, activityMapping, transitionMapping);
    }

    @Override
    protected Pair<Comparator<Activity>> computeActivityOrderings(Log log, Map<String, Activity> mapping) {


        Counter<Activity> counter = new Counter<>();

        for (IndexedVariant indexedVariant : log) {
            Set<Activity> seen = new HashSet<>();
            for (Activity a : indexedVariant.getVariant()) {
                if (!seen.contains(a)) counter.inc(a);
                seen.add(a);
            }
        }

        Comparator<Activity> reversed = Comparator.<Activity>comparingInt(counter::get).reversed();

        return new ImmutablePair<>(reversed, reversed);
    }
}
