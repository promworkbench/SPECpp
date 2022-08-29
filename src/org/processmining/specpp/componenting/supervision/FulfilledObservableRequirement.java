package org.processmining.specpp.componenting.supervision;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.Observable;

public class FulfilledObservableRequirement<O extends Observation> extends AbstractFulfilledSupervisionRequirement<Observable<O>> {

    public FulfilledObservableRequirement(ObservableRequirement<?> requirement, Observable<O> delegate) {
        super(requirement, delegate);
    }

}
