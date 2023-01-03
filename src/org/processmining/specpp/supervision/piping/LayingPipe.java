package org.processmining.specpp.supervision.piping;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.processmining.specpp.datastructures.util.ImmutableTuple2;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.supervision.BackgroundTaskRunner;
import org.processmining.specpp.supervision.RegularScheduler;
import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.supervisors.DebuggingSupervisor;
import org.processmining.specpp.supervision.traits.*;
import org.processmining.specpp.traits.Triggerable;

import java.time.Duration;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Consumer;

public class LayingPipe {

    private Object lastAddition;

    private RegularScheduler regularScheduler;
    private BackgroundTaskRunner backgroundTaskRunner;
    private final LinkedList<Observable<?>> observables;
    private final MultiValuedMap<Integer, Observer<?>> observers;
    private final Deque<Tuple2<Runnable, Duration>> prospectiveScheduledRunnables;
    private final Deque<Runnable> prospectiveSubTaskedRunnables;

    private LayingPipe() {
        observables = new LinkedList<>();
        observers = new ArrayListValuedHashMap<>();
        prospectiveScheduledRunnables = new LinkedList<>();
        prospectiveSubTaskedRunnables = new LinkedList<>();
    }


    public static LayingPipe inst() {
        return new LayingPipe();
    }

    public static LayingPipe inst(RegularScheduler regularScheduler, BackgroundTaskRunner backgroundTaskRunner) {
        return inst().setRegularRunner(regularScheduler).setConstantRunner(backgroundTaskRunner);
    }

    public static void link(Observable<?> source, Observer<?> sink) {
        inst().source(source).sink(sink).apply();
    }

    public LayingPipe setRegularRunner(RegularScheduler regularScheduler) {
        this.regularScheduler = regularScheduler;
        return this;
    }

    public LayingPipe setConstantRunner(BackgroundTaskRunner backgroundTaskRunner) {
        this.backgroundTaskRunner = backgroundTaskRunner;
        return this;
    }

    public LayingPipe source(Observable<?> source) {
        if (!observables.isEmpty() || !observers.isEmpty()) throw new DisorderedPipeLaying();
        appendObservable(source);
        return this;
    }


    public LayingPipe pipe(ObservationPipe<?, ?> pipe) {
        appendPipe(pipe);
        return this;
    }


    public LayingPipe split(Consumer<LayingPipe> splitPath) {
        LayingPipe lp = LayingPipe.inst(regularScheduler, backgroundTaskRunner);
        Observable<?> observable = lastObservable();
        lp.source(observable);
        splitPath.accept(lp);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <O extends Observation> LayingPipe export(Consumer<Observable<O>> consumer) {
        Observable<?> observable = lastObservable();
        Observable<O> cast = (Observable<O>) observable;
        if (cast == null) DebuggingSupervisor.debug("pipe laying export", "cast exception");
        consumer.accept(cast);
        return this;
    }

    private Observable<?> lastObservable() {
        if (observables.isEmpty()) throw new DisorderedPipeLaying();
        return observables.getLast();
    }

    private void appendObservable(Observable<?> source) {
        observables.add(source);
        lastAddition = source;
    }

    private void appendPipe(ObservationPipe<?, ?> pipe) {
        ensureFittingDimensionality(lastObservable(), pipe);
        appendObserver(pipe);
        appendObservable(pipe);
    }

    private void appendObserver(Observer<?> observer) {
        ensureFittingDimensionality(lastObservable(), observer);
        if (observables.isEmpty()) throw new DisorderedPipeLaying();
        int i = observables.size() - 1;
        observers.put(i, observer);
        lastAddition = observer;
    }

    private static void ensureFittingDimensionality(Object producer, Object consumer) {
        if (producer == null || consumer == null)
            throw new PipeLayingException("Trying to lay a connection " + (producer == null ? "from" : "into") + " null");
        if ((producer instanceof ToOne && consumer instanceof FromMany && !(consumer instanceof FromOne)))
            throw new IncompatiblePipeLaying(IncompatiblePipeLaying.WrongRelationship.ToOneIntoFromMany, producer, consumer);
        else if (producer instanceof ToMany && consumer instanceof FromOne && !(consumer instanceof FromMany))
            throw new IncompatiblePipeLaying(IncompatiblePipeLaying.WrongRelationship.ToManyIntoFromOne, producer, consumer);
    }

    public LayingPipe sink(Observer<?> sink) {
        ensureFittingDimensionality(lastObservable(), sink);
        appendObserver(sink);
        return this;
    }

    public LayingPipe sinks(Observer<?>... sinks) {
        for (Observer<?> s : sinks) {
            sink(s);
        }
        return this;
    }

    public void terminalSink(Observer<?> sink) {
        sink(sink);
        apply();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void connectAllObservers(int index) {
        if (index >= observables.size()) throw new DisorderedPipeLaying();
        Observable<?> obs = observables.get(index);
        if (observers.containsKey(index)) observers.get(index).forEach(s -> obs.addObserver((Observer) s));
    }

    public void apply() throws IncompletePipeLaying {
        for (int i = 0; i < observables.size(); i++) {
            connectAllObservers(i);
        }

        if (regularScheduler != null && !prospectiveScheduledRunnables.isEmpty()) {
            for (Tuple2<Runnable, Duration> tuple2 : prospectiveScheduledRunnables) {
                regularScheduler.schedule(tuple2.getT1(), tuple2.getT2());
            }
        } else if (!prospectiveScheduledRunnables.isEmpty())
            throw new IncompletePipeLaying("Missing Regular Scheduler");

        if (backgroundTaskRunner != null && !prospectiveSubTaskedRunnables.isEmpty()) {
            for (Runnable r : prospectiveSubTaskedRunnables) {
                backgroundTaskRunner.register(r);
            }
        } else if (!prospectiveSubTaskedRunnables.isEmpty()) throw new IncompletePipeLaying("Missing SubTasker");
    }

    public LayingPipe schedule(Duration interval) {
        if (lastAddition instanceof Triggerable) {
            Triggerable tr = ((Triggerable) lastAddition);
            prospectiveScheduledRunnables.add(new ImmutableTuple2<>(tr::trigger, interval));
        }
        return this;
    }

    public LayingPipe giveBackgroundThread() {
        if (lastAddition instanceof RequiresSupportingTask) {
            RequiresSupportingTask rst = (RequiresSupportingTask) lastAddition;
            prospectiveSubTaskedRunnables.add(rst.getSupportingTask());
        }
        return this;
    }

    public static class PipeLayingException extends RuntimeException {
        public PipeLayingException() {
        }

        public PipeLayingException(String message) {
            super(message);
        }
    }

    public static class IncompletePipeLaying extends PipeLayingException {

        public IncompletePipeLaying() {
        }

        public IncompletePipeLaying(String message) {
            super(message);
        }
    }


    public static class DisorderedPipeLaying extends PipeLayingException {
    }

    public static class IncompatiblePipeLaying extends PipeLayingException {

        public enum WrongRelationship {
            ToOneIntoFromMany, ToManyIntoFromOne
        }

        public IncompatiblePipeLaying(WrongRelationship mistake, Object producer, Object consumer) {
            super(mistake.toString() + " caused by " + producer.getClass()
                                                               .getSimpleName() + " connecting into " + consumer.getClass()
                                                                                                                .getSimpleName());
        }

    }

}
