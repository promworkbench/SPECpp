package org.processmining.specpp.componenting.delegators;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.Observable;
import org.processmining.specpp.supervision.piping.Observer;

import java.util.function.Consumer;

public class ContainerUtils {

    public static <T> ListContainer<T> listContainer() {
        return new ListContainer<>();
    }

    public static <O extends Observation> Consumer<Observable<O>> observedByConsumer(Observer<O> observer) {
        return obs -> obs.addObserver(observer);
    }

    public static <O extends Observation> ConsumingContainer<Observable<O>> observeResults(Observer<O> observer) {
        return new ConsumingContainer<>(observedByConsumer(observer));
    }

    public static ConsumingContainer<Object> printResults() {
        return new ConsumingContainer<>(System.out::println);
    }
}
