package org.processmining.specpp.supervision.observations;

import org.processmining.specpp.traits.ProperlyHashable;
import org.processmining.specpp.traits.ProperlyPrintable;

/**
 * Marker Interface for Event types.
 * Event types are inherently observations, properly hashable and pretty printable.
 */
public interface Event extends Observation, ProperlyHashable, ProperlyPrintable {

}
