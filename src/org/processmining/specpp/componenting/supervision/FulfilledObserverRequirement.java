package org.processmining.specpp.componenting.supervision;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.Observer;

public class FulfilledObserverRequirement<O extends Observation> extends AbstractFulfilledSupervisionRequirement<Observer<O>> {

    public FulfilledObserverRequirement(ObserverRequirement<?> requirement, Observer<O> delegate) {
        super(requirement, delegate);
    }

}
