package org.processmining.specpp.util;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.processmining.specpp.datastructures.encoding.HashmapEncoding;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.impls.*;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.ImmutableTuple2;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.preprocessing.InputDataBundle;

import java.util.*;

public class HardcodedTestInput {


    public static Tuple2<IntEncodings<Transition>, Map<String, Transition>> setupTransitions(String... transitions) {
        //Transition start = new Transition("\u25B7");
        //Transition end = new Transition("\u2610");
        //ActivityImpl uniqueStart = new ActivityImpl("\u25B7");
        //ActivityImpl uniqueEnd = new ActivityImpl("\u2610");

        Map<Transition, Integer> ordering = new HashMap<>();
        int i = 0;
        //ordering.put(start, i++);

        BidiMap<String, Transition> trans = new DualHashBidiMap<>();
        for (String s : transitions) {
            Transition t = new Transition(s);
            trans.put(s, t);
            ordering.put(t, i++);
        }
        //ordering.put(end, i);
        Set<Transition> preset = new HashSet<>(trans.values());
        //preset.add(start);
        HashmapEncoding<Transition> presetEncoding = HashmapEncoding.ofComparableSet(preset, Comparator.comparingInt(ordering::get));

        Set<Transition> postset = new HashSet<>(trans.values());
        //postset.add(end);
        HashmapEncoding<Transition> postsetEncoding = HashmapEncoding.ofComparableSet(postset, Comparator.comparingInt(ordering::get));

        //trans.put(uniqueStart, start);
        //trans.put(uniqueEnd, end);

        return new ImmutableTuple2<>(new IntEncodings<>(presetEncoding, postsetEncoding), trans);
    }

    public static Map<String, Activity> setupActivities(String... activities) {
        HashMap<String, Activity> result = new HashMap<>();
        for (String s : activities) {
            result.put(s, new ActivityImpl(s));
        }
        return result;
    }

    public static Log setupLog(Map<String, Activity> activityMap) {
        Activity[] ac = activityMap.values().toArray(new Activity[0]);
        LogBuilder<LogImpl> builder = new LogBuilderImpl();

        switch (ac.length) {
            case 3:
            case 4:
            case 5:
                Activity a = ac[0];
                Activity b = ac[1];
                Activity c = ac[2];
                builder.appendVariant(VariantImpl.of(a, a, b, c, b, c))
                       .appendVariant(VariantImpl.of(a, b, c, b, a, c))
                       .appendVariant(VariantImpl.of())
                       .appendVariant(VariantImpl.of(a, c))
                       .appendVariant(VariantImpl.of(b, b));
                break;
        }

        return builder.build();
    }

    public static BidiMap<Activity, Transition> setupMapping(Map<String, Activity> activities, Map<String, Transition> transitions) {
        BidiMap<Activity, Transition> activityTransitionMapping = new DualHashBidiMap<>();
        for (Map.Entry<String, Activity> entry : activities.entrySet()) {
            activityTransitionMapping.put(entry.getValue(), transitions.get(entry.getKey()));
        }
        return activityTransitionMapping;
    }

    public static InputDataBundle getDummyInputBundle(String... labels) {
        Tuple2<IntEncodings<Transition>, Map<String, Transition>> tTuple = setupTransitions(labels);
        Map<String, Activity> activities = setupActivities(labels);
        BidiMap<Activity, Transition> mapping = setupMapping(activities, tTuple.getT2());
        Log log = setupLog(activities);
        IntEncodings<Transition> transitionEncodings = tTuple.getT1();
        return new InputDataBundle(log, transitionEncodings, mapping);
    }
}
