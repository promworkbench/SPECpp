package org.processmining.specpp.supervision.piping;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PipeSystemFlusher {

    public static void flush(Collection<Observable<?>> observables) {
        Set<Observable<?>> seen = new HashSet<>();
        for (Observable<?> observable : observables) {
            handleObservable(observable, seen);
        }
    }


    private static void handleObservable(Observable<?> observable, Set<Observable<?>> seen) {
        seen.add(observable);
        if (observable instanceof Buffering) {
            Buffering buffering = (Buffering) observable;
            if (buffering.isBufferNonEmpty()) buffering.flushBuffer();
        }
        for (Observer<?> observer : observable.getObservers()) {
            if (observer instanceof Observable && !seen.contains(observer)) {
                Observable<?> child = (Observable<?>) observer;
                handleObservable(child, seen);
            }
        }
    }


}
