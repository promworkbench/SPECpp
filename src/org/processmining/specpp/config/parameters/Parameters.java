package org.processmining.specpp.config.parameters;

import org.processmining.specpp.traits.PrettyPrintable;
import org.processmining.specpp.traits.ProperlyPrintable;

public interface Parameters extends ProperlyPrintable, PrettyPrintable {

    @Override
    default String toPrettyString() {
        return toString();
    }
}
