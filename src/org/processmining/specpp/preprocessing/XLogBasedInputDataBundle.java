package org.processmining.specpp.preprocessing;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
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
import org.processmining.specpp.datastructures.petri.InitialTransition;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.Counter;
import org.processmining.specpp.datastructures.util.ImmutableTuple2;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.util.Reflection;

import java.util.HashMap;
import java.util.Map;

public class XLogBasedInputDataBundle implements DataSource<InputDataBundle> {

    private final XLog xLog;
    private final Class<? extends TransitionEncodingsBuilder> transitionEncodingsBuilderClass;
    private final boolean introduceStartEndTransitions;

    protected XLogBasedInputDataBundle(XLog xLog, PreProcessingParameters parameters) {
        this.xLog = xLog;
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

    public static InputDataBundle getInputBundle(String logPath, boolean introduceStartEndTransitions, Class<? extends TransitionEncodingsBuilder> transitionEncodingsBuilderClass) {
        if (logPath == null) throw new InputLoadingException();
        XLog xLog = readLog(logPath);
        Tuple2<Log, Map<String, Activity>> tuple = convert(xLog, introduceStartEndTransitions);
        Tuple2<IntEncodings<Transition>, BidiMap<Activity, Transition>> derivedTransitions = deriveTransitions(tuple.getT1(), tuple.getT2(), transitionEncodingsBuilderClass);
        return new InputDataBundle(tuple.getT1(), derivedTransitions.getT1(), derivedTransitions.getT2());
    }

    public static Transition makeTransition(Activity activity, String label) {
        if (Factory.ARTIFICIAL_START.equals(activity)) return new InitialTransition(label);
        else if (Factory.ARTIFICIAL_END.equals(activity)) return new FinalTransition(label);
        else return new Transition(label);
    }

    public static Tuple2<IntEncodings<Transition>, BidiMap<Activity, Transition>> deriveTransitions(Log log, Map<String, Activity> activityMapping, Class<? extends TransitionEncodingsBuilder> transitionEncodingsBuilderClass) {
        BidiMap<Activity, Transition> transitionMapping = new DualHashBidiMap<>();
        activityMapping.forEach((label, activity) -> transitionMapping.put(activity, makeTransition(activity, label)));
        IntEncodings<Transition> transitionEncodings;
        try {
            TransitionEncodingsBuilder teb = Reflection.instance(transitionEncodingsBuilderClass, log, activityMapping, transitionMapping);
            transitionEncodings = teb.build();
        } catch (RuntimeException e) {
            throw new InputLoadingException(e);
        }
        return new ImmutableTuple2<>(transitionEncodings, transitionMapping);
    }

    public static XLog readLog(String path) {
        try {
            return XUtils.loadLog(path);
        } catch (Exception e) {
            throw new InputLoadingException(e);
        }
    }

    public static Tuple2<Log, Map<String, Activity>> convert(XLog input, boolean introduceStartEndTransitions) {
        if (input == null) throw new InputLoadingException();

        XEventClassifier xEventClassifier = new XEventNameClassifier();// input.getClassifiers().get(0);
        Factory factory = new Factory(introduceStartEndTransitions);

        Map<String, Activity> activities = new HashMap<>();
        if (introduceStartEndTransitions) activities.putAll(Factory.getStartEndActivities());

        Counter<Variant> c = new Counter<>();
        for (XTrace trace : input) {
            VariantBuilder<VariantImpl> builder = factory.createVariantBuilder();
            for (XEvent event : trace) {
                String s = xEventClassifier.getClassIdentity(event);
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
        Tuple2<Log, Map<String, Activity>> tuple = convert(xLog, introduceStartEndTransitions);
        Tuple2<IntEncodings<Transition>, BidiMap<Activity, Transition>> derivedTransitions = deriveTransitions(tuple.getT1(), tuple.getT2(), transitionEncodingsBuilderClass);
        return new InputDataBundle(tuple.getT1(), derivedTransitions.getT1(), derivedTransitions.getT2());
    }

    private static class InputLoadingException extends RuntimeException {
        public InputLoadingException() {
        }

        public InputLoadingException(Throwable cause) {
            super(cause);
        }
    }
}
