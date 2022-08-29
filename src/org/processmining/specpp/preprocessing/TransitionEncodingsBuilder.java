package org.processmining.specpp.preprocessing;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.SetUtils;
import org.processmining.specpp.config.SimpleBuilder;
import org.processmining.specpp.datastructures.encoding.HashmapEncoding;
import org.processmining.specpp.datastructures.encoding.IntEncoding;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.impls.Factory;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public abstract class TransitionEncodingsBuilder implements SimpleBuilder<IntEncodings<Transition>> {

    private final Log log;
    private final Map<String, Activity> activityMapping;
    private final BidiMap<Activity, Transition> transitionMapping;

    public TransitionEncodingsBuilder(Log log, Map<String, Activity> activityMapping, BidiMap<Activity, Transition> transitionMapping) {
        this.log = log;
        this.activityMapping = activityMapping;
        this.transitionMapping = transitionMapping;
    }

    protected static IntEncoding<Transition> createPresetEncoding(Collection<Activity> activities, Comparator<Activity> comparator, BidiMap<Activity, Transition> mapping) {
        return createEncoding(activities, SetUtils.unmodifiableSet(Factory.ARTIFICIAL_END), comparator, mapping);
    }

    protected static IntEncoding<Transition> createPostsetEncoding(Collection<Activity> activities, Comparator<Activity> comparator, BidiMap<Activity, Transition> mapping) {
        return createEncoding(activities, SetUtils.unmodifiableSet(Factory.ARTIFICIAL_START), comparator, mapping);
    }

    protected static IntEncoding<Transition> createEncoding(Collection<Activity> activities, Set<Activity> toIgnore, Comparator<Activity> comparator, BidiMap<Activity, Transition> mapping) {
        TreeSet<Activity> set = new TreeSet<>(comparator);
        set.addAll(activities);
        set.removeAll(toIgnore);
        List<Transition> list = set.stream().map(mapping::get).collect(Collectors.toList());
        return HashmapEncoding.ofList(list);
    }


    protected abstract Pair<Comparator<Activity>> computeActivityOrderings(Log log, Map<String, Activity> mapping);


    @Override
    public IntEncodings<Transition> build() {
        Pair<Comparator<Activity>> comparators = computeActivityOrderings(log, activityMapping);

        IntEncoding<Transition> presetEncoding = TransitionEncodingsBuilder.createPresetEncoding(activityMapping.values(), comparators.first(), transitionMapping);
        IntEncoding<Transition> postsetEncoding = TransitionEncodingsBuilder.createPostsetEncoding(activityMapping.values(), comparators.second(), transitionMapping);

        return new IntEncodings<>(presetEncoding, postsetEncoding);
    }
}
