package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;

import java.util.LinkedList;
import java.util.List;

public class AbstractAsyncAwareObservable<O extends Observation> extends AbstractObservable<O> implements AsyncAwareObservable<O> {

    private final List<AsyncObserver<O>> asyncObservers;
    private final List<Observer<O>> nonAsyncObservers;

    public AbstractAsyncAwareObservable() {
        asyncObservers = new LinkedList<>();
        nonAsyncObservers = new LinkedList<>();
    }

    @Override
    public void addObserver(Observer<O> observer) {
        super.addObserver(observer);
        if (observer instanceof AsyncObserver && !asyncObservers.contains(observer))
            asyncObservers.add(((AsyncObserver<O>) observer));
        else if (!nonAsyncObservers.contains(observer)) nonAsyncObservers.add(observer);
    }

    @Override
    public List<AsyncObserver<O>> getAsyncObservers() {
        return asyncObservers;
    }

    @Override
    public List<Observer<O>> getNonAsyncObservers() {
        return nonAsyncObservers;
    }

}
