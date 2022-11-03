package org.processmining.specpp.preprocessing;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.log.utils.XUtils;
import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.Variant;
import org.processmining.specpp.datastructures.log.impls.*;
import org.processmining.specpp.datastructures.petri.FinalTransition;
import org.processmining.specpp.datastructures.petri.InitialFinalTransition;
import org.processmining.specpp.datastructures.petri.InitialTransition;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.*;
import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.preprocessing.orderings.ActivityOrderingStrategy;
import org.processmining.specpp.util.Reflection;

import java.io.File;
import java.util.*;

public class XLogBasedInputDataBundle implements DataSource<InputDataBundle> {

    private final XLog xLog;
    private final Class<? extends ActivityOrderingStrategy> transitionEncodingsBuilderClass;
    private final boolean introduceStartEndTransitions;
    private final XEventClassifier eventClassifier;
    private InputDataBundle idb;

    protected XLogBasedInputDataBundle(XLog xLog, PreProcessingParameters parameters) {
        this.xLog = xLog;
        eventClassifier = parameters.getEventClassifier();
        this.transitionEncodingsBuilderClass = parameters.getTransitionEncodingsBuilderClass();
        this.introduceStartEndTransitions = parameters.isAddStartEndTransitions();
    }

    public static XLogBasedInputDataBundle fromPath(String path, PreProcessingParameters parameters) {
        XLog log = readLog(path);
        return new XLogBasedInputDataBundle(log, parameters);
    }

    public static XLogBasedInputDataBundle fromXLog(XLog log, PreProcessingParameters parameters) {
        return new XLogBasedInputDataBundle(log, parameters);
    }

    public static Transition makeTransition(Activity activity, String label) {
        if (Factory.ARTIFICIAL_START.equals(activity)) return new InitialTransition(label);
        else if (Factory.ARTIFICIAL_END.equals(activity)) return new FinalTransition(label);
        else return new Transition(label);
    }

    public static Transition makeTransition(Activity activity, String label, boolean isInitial, boolean isFinal) {
        if (isInitial && isFinal) return new InitialFinalTransition(label);
        else if (isInitial) return new InitialTransition(label);
        else if (isFinal) return new FinalTransition(label);
        else return new Transition(label);
    }

    public static Tuple2<IntEncodings<Transition>, BidiMap<Activity, Transition>> deriveTransitions(Pair<Comparator<Activity>> comparators, Log log, Map<String, Activity> activityMapping) {
        BidiMap<Activity, Transition> transitionMapping = createTransitions(log, activityMapping);
        Set<Activity> presetSet = new HashSet<>(activityMapping.values());
        presetSet.remove(Factory.ARTIFICIAL_END);
        Set<Activity> postsetSet = new HashSet<>(activityMapping.values());
        postsetSet.remove(Factory.ARTIFICIAL_START);
        IntEncodings<Transition> encodings = ActivityOrderingStrategy.createEncodings(new ImmutablePair<>(presetSet, postsetSet), comparators, transitionMapping);
        return new ImmutableTuple2<>(encodings, transitionMapping);
    }

    public static BidiMap<Activity, Transition> createTransitions(Log log, Map<String, Activity> activityMapping) {
        Set<Activity> initialActivities = new HashSet<>(), finalActivities = new HashSet<>();
        for (IndexedVariant indexedVariant : log) {
            Variant v = indexedVariant.getVariant();
            int size = v.size();
            if (size > 0) {
                initialActivities.add(v.getAt(0));
                finalActivities.add(v.getAt(size - 1));
            }
        }
        BidiMap<Activity, Transition> transitionMapping = new DualHashBidiMap<>();
        activityMapping.forEach((label, activity) -> transitionMapping.put(activity, makeTransition(activity, label, initialActivities.contains(activity), finalActivities.contains(activity))));
        return transitionMapping;
    }

    public static Pair<Comparator<Activity>> createOrderings(Log log, Map<String, Activity> activityMapping, Class<? extends ActivityOrderingStrategy> transitionEncodingsBuilderClass) {
        ActivityOrderingStrategy teb = Reflection.instance(transitionEncodingsBuilderClass, log, activityMapping);
        return teb.build();
    }

    public static XLog readLog(String path) {
        try {
            return XUtils.loadLog(new File(path));
        } catch (Exception e) {
            throw new InputLoadingException(e);
        }
    }

    public static Tuple2<Log, Map<String, Activity>> convertLog(XLog input, XEventClassifier eventClassifier, boolean introduceStartEndTransitions) {
        if (input == null) throw new InputLoadingException();

        Factory factory = new Factory(introduceStartEndTransitions);

        Map<String, Activity> activities = new HashMap<>();
        if (introduceStartEndTransitions) activities.putAll(Factory.getStartEndActivities());

        Counter<Variant> c = new Counter<>();
        for (XTrace trace : input) {
            VariantBuilder<VariantImpl> builder = factory.createVariantBuilder();
            for (XEvent event : trace) {
                String s = eventClassifier.getClassIdentity(event);
                if (!activities.containsKey(s)) activities.put(s, factory.createActivity(s));
                Activity activity = activities.get(s);
                builder.append(activity);
            }
            VariantImpl v = builder.build();
            c.inc(v);
        }
        LogBuilder<LogImpl> builder = factory.createLogBuilder();
        for (Map.Entry<Variant, Integer> entry : c.entrySet()) {
            builder.appendVariant(entry.getKey(), entry.getValue());
        }
        return new ImmutableTuple2<>(builder.build(), activities);
    }

    @Override
    public InputDataBundle getData() {
        if (idb == null) {
            Tuple2<Log, Map<String, Activity>> tuple = convertLog(xLog, eventClassifier, introduceStartEndTransitions);
            Pair<Comparator<Activity>> orderings = createOrderings(tuple.getT1(), tuple.getT2(), transitionEncodingsBuilderClass);
            Tuple2<IntEncodings<Transition>, BidiMap<Activity, Transition>> derivedTransitions = deriveTransitions(orderings, tuple.getT1(), tuple.getT2());
            idb = new InputDataBundle(tuple.getT1(), derivedTransitions.getT1(), derivedTransitions.getT2());
        }
        return idb;
    }

    private static class InputLoadingException extends RuntimeException {
        public InputLoadingException() {
        }

        public InputLoadingException(Throwable cause) {
            super(cause);
        }
    }

}
