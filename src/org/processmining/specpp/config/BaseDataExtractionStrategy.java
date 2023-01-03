package org.processmining.specpp.config;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.DataSourceCollection;
import org.processmining.specpp.componenting.data.StaticDataSource;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.ParsedLog;
import org.processmining.specpp.datastructures.log.Variant;
import org.processmining.specpp.datastructures.log.impls.Factory;
import org.processmining.specpp.datastructures.log.impls.IndexedVariant;
import org.processmining.specpp.datastructures.log.impls.LogEncoder;
import org.processmining.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.specpp.datastructures.petri.FinalTransition;
import org.processmining.specpp.datastructures.petri.InitialFinalTransition;
import org.processmining.specpp.datastructures.petri.InitialTransition;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.ImmutablePair;
import org.processmining.specpp.datastructures.util.ImmutableTuple2;
import org.processmining.specpp.datastructures.util.Pair;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.preprocessing.orderings.ActivityOrderingStrategy;
import org.processmining.specpp.util.Reflection;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BaseDataExtractionStrategy implements DataExtractionStrategy {

    public static Transition makeTransition(Activity activity, String label, boolean isInitial, boolean isFinal) {
        if (isInitial && isFinal) return new InitialFinalTransition(label);
        else if (isInitial) return new InitialTransition(label);
        else if (isFinal) return new FinalTransition(label);
        else return new Transition(label);
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

    public static Tuple2<IntEncodings<Transition>, BidiMap<Activity, Transition>> deriveTransitions(Pair<Comparator<Activity>> comparators, Log log, Map<String, Activity> activityMapping) {
        BidiMap<Activity, Transition> transitionMapping = createTransitions(log, activityMapping);
        return new ImmutableTuple2<>(orderTransitions(comparators, activityMapping, transitionMapping), transitionMapping);
    }

    public static IntEncodings<Transition> orderTransitions(Pair<Comparator<Activity>> comparators, Map<String, Activity> activityMapping, BidiMap<Activity, Transition> transitionMapping) {
        Set<Activity> presetSet = new HashSet<>(activityMapping.values());
        presetSet.remove(Factory.ARTIFICIAL_END);
        Set<Activity> postsetSet = new HashSet<>(activityMapping.values());
        postsetSet.remove(Factory.ARTIFICIAL_START);
        return ActivityOrderingStrategy.createEncodings(new ImmutablePair<>(presetSet, postsetSet), comparators, transitionMapping);
    }

    public static Pair<Comparator<Activity>> createOrderings(Log log, Map<String, Activity> activityMapping, Class<? extends ActivityOrderingStrategy> transitionEncodingsBuilderClass) {
        ActivityOrderingStrategy teb = Reflection.instance(transitionEncodingsBuilderClass, log, activityMapping);
        return teb.build();
    }


    @Override
    public InputDataBundle extract(ParsedLog parsedLog, DataExtractionParameters parameters) {
        Log log = parsedLog.getLog();
        BidiMap<String, Activity> stringActivityMapping = parsedLog.getStringActivityMapping();
        Pair<Comparator<Activity>> orderings = createOrderings(log, stringActivityMapping, parameters.getActivityOrderingStrategy());
        Tuple2<IntEncodings<Transition>, BidiMap<Activity, Transition>> derivedTransitions = deriveTransitions(orderings, log, stringActivityMapping);
        return new InputDataBundle(log, derivedTransitions.getT1(), derivedTransitions.getT2());
    }

    @Override
    public void registerDataSources(GlobalComponentRepository cr, InputDataBundle bundle) {
        Log log = bundle.getLog();
        IntEncodings<Transition> transitionEncodings = bundle.getTransitionEncodings();
        BidiMap<Activity, Transition> mapping = bundle.getMapping();

        DataSourceCollection dc = cr.dataSources();
        dc.register(DataRequirements.RAW_LOG, StaticDataSource.of(log));
        LogEncoder.LogEncodingParameters lep = LogEncoder.LogEncodingParameters.getDefault();
        MultiEncodedLog multiEncodedLog = LogEncoder.multiEncodeLog(log, transitionEncodings, mapping, lep);
        dc.register(DataRequirements.ENC_LOG, StaticDataSource.of(multiEncodedLog));
        BitMask data = multiEncodedLog.variantIndices();
        dc.register(DataRequirements.CONSIDERED_VARIANTS, StaticDataSource.of(data));
        dc.register(DataRequirements.VARIANT_FREQUENCIES, StaticDataSource.of(multiEncodedLog.variantFrequencies()));
        dc.register(DataRequirements.ENC_ACT, StaticDataSource.of(multiEncodedLog.getEncodings()));
        dc.register(DataRequirements.ENC_TRANS, StaticDataSource.of(transitionEncodings));
        dc.register(DataRequirements.ACT_TRANS_MAPPING, StaticDataSource.of(mapping));
    }

}
