package org.processmining.specpp.componenting.supervision;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.AdHocObservable;

public class FulfilledAdHocObservableRequirement<O extends Observation> extends FulfilledObservableRequirement<O> {

    public FulfilledAdHocObservableRequirement(AdHocObservableRequirement<?> adHocObservableRequirement, AdHocObservable<O> observable) {
        super(adHocObservableRequirement, observable);
    }
}
