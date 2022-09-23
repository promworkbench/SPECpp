package org.processmining.specpp.datastructures.petri;

import org.processmining.specpp.datastructures.util.NoRehashing;
import org.processmining.specpp.traits.ProperlyHashable;
import org.processmining.specpp.traits.ProperlyPrintable;

public class Transition extends NoRehashing<String> implements ProperlyPrintable, ProperlyHashable {


    public Transition(String label) {
        super(label);
    }

    @Override
    public String toString() {
        return internal;
    }

}
