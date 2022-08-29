package org.processmining.specpp.componenting.supervision;

import org.processmining.specpp.datastructures.util.Label;
import org.processmining.specpp.datastructures.util.RegexLabel;
import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.observations.Visualization;
import org.processmining.specpp.supervision.piping.AdHocObservable;
import org.processmining.specpp.supervision.piping.Observable;
import org.processmining.specpp.supervision.piping.ObservationPipe;
import org.processmining.specpp.supervision.piping.Observer;
import org.processmining.specpp.util.JavaTypingUtils;

import javax.swing.*;

public class SupervisionRequirements {

    public static <I extends Observation, O extends Observation> ObservationPipeRequirement<I, O> pipe(String label, Class<I> observedClass, Class<O> observableClass) {
        return new ObservationPipeRequirement<>(label, observedClass, observableClass);
    }

    public static <I extends Observation, O extends Observation> FulfilledObservationPipeRequirement<I, O> pipe(String label, Class<I> observedClass, Class<O> observableClass, ObservationPipe<I, O> pipe) {
        return pipe(pipe(label, observedClass, observableClass), pipe);
    }

    public static <I extends Observation, O extends Observation> ObservationPipeRequirement<I, O> pipe(Label label, Class<I> observedClass, Class<O> observableClass) {
        return new ObservationPipeRequirement<>(label, observedClass, observableClass);
    }

    public static <I extends Observation, O extends Observation> FulfilledObservationPipeRequirement<I, O> pipe(Label label, Class<I> observedClass, Class<O> observableClass, ObservationPipe<I, O> pipe) {
        return pipe(pipe(label, observedClass, observableClass), pipe);
    }

    public static <I extends Observation, O extends Observation> FulfilledObservationPipeRequirement<I, O> pipe(ObservationPipeRequirement<I, O> requirement, ObservationPipe<I, O> pipe) {
        return requirement.fulfilWith(pipe);
    }

    public static <O extends Observation> ObservableRequirement<O> observable(String label, Class<O> observableClass) {
        return new ObservableRequirement<>(label, observableClass);
    }

    public static <O extends Observation, T extends Observable<O>> FulfilledObservableRequirement<O> observable(String label, Class<O> observableClass, T observable) {
        return observable(observable(label, observableClass), (observable));
    }

    public static <O extends Observation> ObservableRequirement<O> observable(Label label, Class<O> observableClass) {
        return new ObservableRequirement<>(label, observableClass);
    }

    public static <O extends Observation, T extends Observable<O>> FulfilledObservableRequirement<O> observable(Label label, Class<O> observableClass, T observable) {
        return observable(observable(label, observableClass), (observable));
    }

    public static <O extends Observation, T extends Observable<O>> FulfilledObservableRequirement<O> observable(ObservableRequirement<O> requirement, T observable) {
        return requirement.fulfilWith(observable);
    }

    public static <O extends Observation> AdHocObservableRequirement<O> adHocObservable(String label, Class<O> observableClass) {
        return new AdHocObservableRequirement<>(label, observableClass);
    }

    public static <O extends Observation, T extends AdHocObservable<O>> FulfilledAdHocObservableRequirement<O> adHocObservable(String label, Class<O> observableClass, T observable) {
        return adHocObservable(adHocObservable(label, observableClass), observable);
    }

    public static <O extends Observation> AdHocObservableRequirement<O> adHocObservable(Label label, Class<O> observableClass) {
        return new AdHocObservableRequirement<>(label, observableClass);
    }

    public static <O extends Observation, T extends AdHocObservable<O>> FulfilledAdHocObservableRequirement<O> adHocObservable(Label label, Class<O> observableClass, T observable) {
        return adHocObservable(adHocObservable(label, observableClass), observable);
    }

    public static <O extends Observation, T extends AdHocObservable<O>> FulfilledAdHocObservableRequirement<O> adHocObservable(AdHocObservableRequirement<O> requirement, T observable) {
        return requirement.fulfilWith(observable);
    }

    public static <O extends Observation> ObserverRequirement<O> observer(String label, Class<O> observedClass) {
        return new ObserverRequirement<>(label, observedClass);
    }

    public static <O extends Observation, T extends Observer<O>> FulfilledObserverRequirement<O> observer(String label, Class<O> observedClass, T observer) {
        return observer(observer(label, observedClass), observer);
    }

    public static <O extends Observation> ObserverRequirement<O> observer(Label label, Class<O> observedClass) {
        return new ObserverRequirement<>(label, observedClass);
    }

    public static <O extends Observation, T extends Observer<O>> FulfilledObserverRequirement<O> observer(Label label, Class<O> observedClass, T observer) {
        return observer(observer(label, observedClass), observer);
    }

    public static <O extends Observation, T extends Observer<O>> FulfilledObserverRequirement<O> observer(ObserverRequirement<O> requirement, T observer) {
        return requirement.fulfilWith(observer);
    }

    public static <T extends JComponent> AdHocObservableRequirement<Visualization<T>> visualization(String label) {
        return adHocObservable(label, JavaTypingUtils.castClass(Visualization.class));
    }

    public static RegexLabel regex(String pattern) {
        return new RegexLabel(pattern);
    }


}
