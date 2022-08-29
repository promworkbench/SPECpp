package org.processmining.specpp.componenting.supervision;

import org.processmining.specpp.datastructures.util.Label;
import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.ObservationPipe;
import org.processmining.specpp.util.JavaTypingUtils;

public class ObservationPipeRequirement<I extends Observation, O extends Observation> extends SupervisionRequirement implements RequiresObserver<I>, RequiresObservable<O> {

    private final Class<I> observedClass;
    private final Class<O> observableClass;

    public ObservationPipeRequirement(String label, Class<I> observedClass, Class<O> observableClass) {
        this(new Label(label), observedClass,
                observableClass);
    }

    public ObservationPipeRequirement(Label label, Class<I> observedClass, Class<O> observableClass) {
        super(label);
        this.observedClass = observedClass;
        this.observableClass = observableClass;
    }

    public Class<I> getObservedClass() {
        return observedClass;
    }

    @Override
    public Class<O> getObservableClass() {
        return observableClass;
    }

    @Override
    public boolean gt(SupervisionRequirement other) {
        boolean b = labelIsGt(other);
        if (other instanceof RequiresObserver) {
            b &= observedClass.isAssignableFrom(((RequiresObserver<?>) other).getObservedClass());
        }
        if (other instanceof RequiresObservable) {
            b &= ((RequiresObservable<?>) other).getObservableClass().isAssignableFrom(observableClass);
        }
        return b;
    }

    @Override
    public boolean lt(SupervisionRequirement other) {
        if (labelIsGt(other) && other instanceof ObservationPipeRequirement) {
            ObservationPipeRequirement<?, ?> r = (ObservationPipeRequirement<?, ?>) other;
            return r.getObservedClass()
                    .isAssignableFrom(observedClass) && observableClass.isAssignableFrom(r.getObservableClass());
        }
        return false;
    }

    @Override
    public Class<ObservationPipe<I, O>> contentClass() {
        return JavaTypingUtils.castClass(ObservationPipe.class);
    }

    @Override
    public String toString() {
        return "ObservationPipeRequirement(" + label + ", " + observedClass.getSimpleName() + ", " + observableClass.getSimpleName() + ")";
    }

    public FulfilledObservationPipeRequirement<I, O> fulfilWith(ObservationPipe<I, O> pipe) {
        return new FulfilledObservationPipeRequirement<>(this, pipe);
    }

}
