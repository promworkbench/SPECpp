package org.processmining.specpp.datastructures.log.impls;

import com.google.common.collect.ImmutableMap;
import org.processmining.specpp.config.components.InitializingBuilder;
import org.processmining.specpp.datastructures.log.Activity;

import java.util.Map;
import java.util.function.Supplier;

public class Factory {

    private final InitializingBuilder<Activity, String> activityBuilder;
    private final Supplier<VariantBuilder<VariantImpl>> variantBuilderSupplier;
    private final Supplier<LogBuilder<LogImpl>> logBuilderSupplier;


    public static final String UNIQUE_START_LABEL, UNIQUE_END_LABEL;
    public static final Activity ARTIFICIAL_START, ARTIFICIAL_END;

    static {
        UNIQUE_START_LABEL = "\u25B7";
        UNIQUE_END_LABEL = "\u2610";
        ARTIFICIAL_START = new ActivityImpl(UNIQUE_START_LABEL);
        ARTIFICIAL_END = new ActivityImpl(UNIQUE_END_LABEL);
    }

    public Factory(boolean introduceStartEndTransitions) {
        activityBuilder = ActivityImpl::new;
        variantBuilderSupplier = introduceStartEndTransitions ? StartEndInsertingVariantBuilderImpl::new : VariantBuilderImpl::new;
        logBuilderSupplier = LogBuilderImpl::new;
    }

    public static Map<String, Activity> getStartEndActivities() {
        return ImmutableMap.of(UNIQUE_START_LABEL, ARTIFICIAL_START, UNIQUE_END_LABEL, ARTIFICIAL_END);
    }

    public Activity createActivity(String label) {
        return activityBuilder.build(label);
    }

    public VariantBuilder<VariantImpl> createVariantBuilder() {
        return variantBuilderSupplier.get();
    }

    public LogBuilder<LogImpl> createLogBuilder() {
        return logBuilderSupplier.get();
    }

}
