package org.processmining.specpp.componenting.supervision;

import org.processmining.specpp.datastructures.util.Label;
import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.Observer;
import org.processmining.specpp.util.JavaTypingUtils;

public class ObserverRequirement<O extends Observation> extends SupervisionRequirement implements RequiresObserver<O> {

    private final Class<O> observedClass;

    public ObserverRequirement(String label, Class<O> observedClass) {
        this(new Label(label), observedClass);
    }

    public ObserverRequirement(Label label, Class<O> observedClass) {
        super(label);
        this.observedClass = observedClass;
    }

    @Override
    public Class<O> getObservedClass() {
        return observedClass;
    }

    @Override
    public boolean gt(SupervisionRequirement other) {
        if (labelIsGt(other) && other instanceof ObserverRequirement) {
            ObserverRequirement<?> r = (ObserverRequirement<?>) other;
            return observedClass.isAssignableFrom(r.getObservedClass());
        }
        return false;
    }

    @Override
    public boolean lt(SupervisionRequirement other) {
        if (labelIsGt(other) && other instanceof RequiresObserver) {
            RequiresObserver<?> r = (RequiresObserver<?>) other;
            return r.getObservedClass().isAssignableFrom(observedClass);
        }
        return false;
    }

    @Override
    public Class<Observer<O>> contentClass() {
        return JavaTypingUtils.castClass(Observer.class);
    }

    @Override
    public String toString() {
        return "ObserverRequirement(" + label + ", " + observedClass.getSimpleName() + ")";
    }

    public FulfilledObserverRequirement<O> fulfilWith(Observer<O> observer) {
        return new FulfilledObserverRequirement<>(this, observer);
    }

}
