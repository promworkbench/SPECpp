package org.processmining.specpp.datastructures.log.impls;

import org.processmining.specpp.datastructures.log.Activity;

import java.util.ArrayList;

public class VariantBuilderImpl implements VariantBuilder<VariantImpl> {

    protected final ArrayList<Activity> activities;

    public VariantBuilderImpl() {
        activities = new ArrayList<>();
    }

    public VariantBuilder<VariantImpl> append(Activity activity) {
        activities.add(activity);
        return this;
    }

    public VariantImpl build() {
        return new VariantImpl(activities.toArray(new Activity[0]));
    }

}
