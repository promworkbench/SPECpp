package org.processmining.specpp.datastructures.log.impls;

import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.util.NoRehashing;

public class ActivityImpl extends NoRehashing<String> implements Activity {

    private final String label;

    public ActivityImpl(String label) {
        super(label);
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

}
