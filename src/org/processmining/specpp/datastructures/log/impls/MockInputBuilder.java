package org.processmining.specpp.datastructures.log.impls;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.processmining.specpp.config.BaseDataExtractionStrategy;
import org.processmining.specpp.datastructures.encoding.FixedOrdering;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.Variant;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.ImmutablePair;
import org.processmining.specpp.datastructures.util.Pair;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.preprocessing.orderings.ActivityOrderingStrategy;

import java.util.Comparator;

public class MockInputBuilder {

    private final BidiMap<String, Activity> activityMap;
    private final Factory factory;
    private final LogBuilder<LogImpl> logBuilder;
    private Log log;
    private BidiMap<Activity, Transition> activityTransitionMap;
    private IntEncodings<Transition> transitionEncodings;
    private FixedOrdering<Activity> fixedPresetOrdering, fixedPostsetOrdering;

    public MockInputBuilder(boolean addArtificialStartEnd) {
        factory = new Factory(addArtificialStartEnd);
        activityMap = new DualHashBidiMap<>();
        if (addArtificialStartEnd) activityMap.putAll(Factory.getStartEndActivities());
        logBuilder = factory.createLogBuilder();
    }

    public String artificialStartLabel() {
        return Factory.UNIQUE_START_LABEL;
    }

    public String artificialEndLabel() {
        return Factory.UNIQUE_END_LABEL;
    }

    public Activity artificialStart() {
        return Factory.ARTIFICIAL_START;
    }

    public Activity artificialEnd() {
        return Factory.ARTIFICIAL_END;
    }

    public Activity[] addActivities(String... labels) {
        Activity[] as = new Activity[labels.length];
        for (int i = 0; i < labels.length; i++) {
            String s = labels[i];
            as[i] = addActivity(s);
        }
        return as;
    }

    public Activity addActivity(String label) {
        return activityMap.put(label, factory.createActivity(label));
    }

    public Variant addVariant(Activity... activities) {
        Variant v = makeVariant(activities);
        logBuilder.appendVariant(v);
        return v;
    }

    public Variant addVariant(int frequency, Activity... activities) {
        Variant v = makeVariant(activities);
        logBuilder.appendVariant(v, frequency);
        return v;
    }

    public Variant addVariant(String... labels) {
        Variant v = makeVariant(labels);
        logBuilder.appendVariant(v);
        return v;
    }

    public Variant addVariant(int frequency, String... labels) {
        Variant variant = makeVariant(labels);
        logBuilder.appendVariant(variant, frequency);
        return variant;
    }

    private Variant makeVariant(Activity[] activities) {
        VariantBuilder<VariantImpl> vb = factory.createVariantBuilder();
        for (Activity a : activities) {
            vb.append(a);
        }
        return vb.build();
    }

    private Variant makeVariant(String[] labels) {
        VariantBuilder<VariantImpl> vb = factory.createVariantBuilder();
        for (String s : labels) {
            vb.append(activityMap.get(s));
        }
        return vb.build();
    }

    public Log createLog() {
        log = logBuilder.build();
        return log;
    }

    public BidiMap<Activity, Transition> createTransitions() {
        activityTransitionMap = BaseDataExtractionStrategy.createTransitions(log, activityMap);
        return activityTransitionMap;
    }

    public IntEncodings<Transition> createTransitionEncodings() {
        transitionEncodings = ActivityOrderingStrategy.createEncodings(new ImmutablePair<>(fixedPresetOrdering.elements(), fixedPostsetOrdering.elements()), new ImmutablePair<>(fixedPresetOrdering, fixedPostsetOrdering), activityTransitionMap);
        return transitionEncodings;
    }

    public IntEncodings<Transition> createTransitionEncodings(Class<ActivityOrderingStrategy> type) {
        Pair<Comparator<Activity>> pair = BaseDataExtractionStrategy.createOrderings(log, activityMap, type);
        transitionEncodings = BaseDataExtractionStrategy.orderTransitions(pair, activityMap, activityTransitionMap);
        return transitionEncodings;
    }

    public FixedOrdering<Activity> addPresetOrdering(String... labels) {
        fixedPresetOrdering = makeOrdering(labels);
        return fixedPresetOrdering;
    }

    public FixedOrdering<Activity> addPresetOrdering(Activity... labels) {
        fixedPresetOrdering = makeOrdering(labels);
        return fixedPresetOrdering;
    }

    public FixedOrdering<Activity> addPostsetOrdering(String... labels) {
        fixedPostsetOrdering = makeOrdering(labels);
        return fixedPostsetOrdering;
    }

    public FixedOrdering<Activity> addPostsetOrdering(Activity... labels) {
        fixedPostsetOrdering = makeOrdering(labels);
        return fixedPostsetOrdering;
    }

    public FixedOrdering<Activity> makeOrdering(String... labels) {
        Activity[] as = new Activity[labels.length];
        for (int i = 0; i < labels.length; i++) {
            as[i] = activityMap.get(labels[i]);
        }
        return new FixedOrdering<>(as);
    }

    public FixedOrdering<Activity> makeOrdering(Activity... activities) {
        return new FixedOrdering<>(activities);
    }

    public InputDataBundle createInputDataBundle() {
        if (log == null) createLog();
        if (activityTransitionMap == null) createTransitions();
        if (transitionEncodings == null) createTransitionEncodings();
        return new InputDataBundle(log, transitionEncodings, activityTransitionMap);
    }

}
