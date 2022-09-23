package org.processmining.specpp.datastructures.log;

import org.processmining.specpp.traits.*;

public interface Variant extends Iterable<Activity>, Streamable<Activity>, IndexAccessible<Activity>, ProperlyPrintable, ProperlyHashable, Immutable {

    int getLength();

    default boolean isEmpty() {
        return getLength() == 0;
    }

    default int size() {
        return getLength();
    }

}
