package org.processmining.specpp.supervision;

import org.processmining.specpp.supervision.observations.Event;
import org.processmining.specpp.supervision.piping.IdentityPipe;
import org.processmining.specpp.supervision.traits.OneToOne;

public class EventSupervision<E extends Event> extends IdentityPipe<E> implements OneToOne<E, E> {
}
