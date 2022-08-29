package org.processmining.specpp.componenting.supervision;

import org.processmining.specpp.datastructures.util.Label;
import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.AdHocObservable;
import org.processmining.specpp.supervision.piping.Observable;
import org.processmining.specpp.util.JavaTypingUtils;

public class AdHocObservableRequirement<O extends Observation> extends ObservableRequirement<O> {

    public AdHocObservableRequirement(String label, Class<O> observableClass) {
        this(new Label(label), observableClass);
    }


    public AdHocObservableRequirement(Label label, Class<O> observableClass) {
        super(label, observableClass);
    }

    @Override
    public boolean gt(SupervisionRequirement other) {
        return other instanceof AdHocObservableRequirement && super.gt(other);
    }

    @Override
    public boolean lt(SupervisionRequirement other) {
        return other instanceof AdHocObservableRequirement && super.lt(other);
    }

    @Override
    public Class<Observable<O>> contentClass() {
        return JavaTypingUtils.castClass(AdHocObservable.class);
    }

    @Override
    public String toString() {
        return "AdHocObservableRequirement(" + label + ", " + getObservableClass().getSimpleName() + ")";
    }

    public FulfilledAdHocObservableRequirement<O> fulfilWith(AdHocObservable<O> observable) {
        return new FulfilledAdHocObservableRequirement<>(this, observable);
    }

}
