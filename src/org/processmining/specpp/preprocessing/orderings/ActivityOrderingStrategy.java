package org.processmining.specpp.preprocessing.orderings;

import org.apache.commons.collections4.BidiMap;
import org.processmining.specpp.config.SimpleBuilder;
import org.processmining.specpp.datastructures.encoding.HashmapEncoding;
import org.processmining.specpp.datastructures.encoding.IntEncoding;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public abstract class ActivityOrderingStrategy implements SimpleBuilder<Pair<Comparator<Activity>>> {

    private final Log log;
    private final Map<String, Activity> activityMapping;

    public ActivityOrderingStrategy(Log log, Map<String, Activity> activityMapping) {
        this.log = log;
        this.activityMapping = activityMapping;
    }


    public static IntEncodings<Transition> createEncodings(Pair<Set<Activity>> selectedActivitiesPair, Pair<Comparator<Activity>> comparators, BidiMap<Activity, Transition> mapping) {
        return new IntEncodings<>(createPresetEncoding(selectedActivitiesPair.first(), comparators.first(), mapping), createPostsetEncoding(selectedActivitiesPair.second(), comparators.second(), mapping));
    }

    public static IntEncoding<Transition> createPresetEncoding(Collection<Activity> activities, Comparator<Activity> comparator, BidiMap<Activity, Transition> mapping) {
        return HashmapEncoding.ofList(createOrderedList(activities, comparator, mapping));
    }

    public static IntEncoding<Transition> createPostsetEncoding(Collection<Activity> activities, Comparator<Activity> comparator, BidiMap<Activity, Transition> mapping) {
        return HashmapEncoding.ofList(createOrderedList(activities, comparator, mapping));
    }

    public static List<Transition> createOrderedList(Collection<Activity> activities, Comparator<Activity> comparator, BidiMap<Activity, Transition> mapping) {
        ArrayList<Activity> l = new ArrayList<>(activities);
        l.sort(comparator);
        return l.stream().map(mapping::get).collect(Collectors.toList());
    }


    public abstract Pair<Comparator<Activity>> computeActivityOrderings(Log log, Map<String, Activity> mapping);


    @Override
    public Pair<Comparator<Activity>> build() {
        return computeActivityOrderings(log, activityMapping);
    }
}
