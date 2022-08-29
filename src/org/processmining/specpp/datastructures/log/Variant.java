package org.processmining.specpp.datastructures.log;

import org.processmining.specpp.traits.Immutable;
import org.processmining.specpp.traits.ProperlyHashable;
import org.processmining.specpp.traits.ProperlyPrintable;
import org.processmining.specpp.traits.Streamable;

public interface Variant extends Iterable<Activity>, Streamable<Activity>, ProperlyPrintable, ProperlyHashable, Immutable {

    int getLength();

    default int size() {
        return getLength();
    }

}
