package org.processmining.specpp.datastructures.log.impls;

import org.apache.commons.collections4.IteratorUtils;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Variant;
import org.processmining.specpp.datastructures.util.NoRehashing;
import org.processmining.specpp.traits.ProperlyPrintable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

public class VariantImpl extends NoRehashing<Activity[]> implements Variant, ProperlyPrintable {

    private final Activity[] activities;

    protected VariantImpl(Activity[] activityArray) {
        super(activityArray);
        activities = activityArray;
    }

    public static VariantImpl of(Activity... as) {
        return new VariantImpl(as);
    }


    @Override
    public Iterator<Activity> iterator() {
        return IteratorUtils.arrayIterator(activities);
    }

    @Override
    public Stream<Activity> stream() {
        return Arrays.stream(activities);
    }

    @Override
    public int getLength() {
        return activities.length;
    }

    @Override
    public String toString() {
        return Arrays.toString(activities);
    }

    @Override
    public Activity getAt(int index) {
        assert 0 <= index && index < activities.length;
        return activities[index];
    }
}
