package org.processmining.specpp.base;

import org.processmining.specpp.datastructures.tree.constraints.Constraint;
import org.processmining.specpp.supervision.observations.Event;

/**
 * Base Interface for constraint events as they are used with the observation system.
 */
public interface ConstraintEvent extends Constraint, Event {
}
