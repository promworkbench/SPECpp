package org.processmining.specpp.componenting.system;

import org.processmining.specpp.traits.PartiallyOrdered;
import org.processmining.specpp.traits.ProperlyHashable;
import org.processmining.specpp.traits.ProperlyPrintable;

public interface Requirement<D, T> extends PartiallyOrdered<T>, ProperlyPrintable, ProperlyHashable {

    ComponentType componentType();

    Class<? extends D> contentClass();

}
