package org.processmining.specpp.supervision.traits;

import org.processmining.specpp.datastructures.util.TypedItem;

import java.util.Collection;

public interface ProvidesResults {

    Collection<TypedItem<?>> getResults();

}
