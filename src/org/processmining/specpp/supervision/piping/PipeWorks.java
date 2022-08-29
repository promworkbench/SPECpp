package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.ConsoleMessageLogger;
import org.processmining.specpp.supervision.EventSupervision;
import org.processmining.specpp.supervision.FileMessageLogger;
import org.processmining.specpp.supervision.MessageLogger;
import org.processmining.specpp.supervision.observations.Event;
import org.processmining.specpp.supervision.observations.LogMessage;
import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.observations.TimedObservation;
import org.processmining.specpp.supervision.transformers.Transformers;
import org.processmining.specpp.traits.Mergeable;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PipeWorks {

    private PipeWorks() {
    }

    public static <O extends Observation> Observer<O> loggingSink(String source, Observer<? super LogMessage> ml) {
        return loggingSink(source, Object::toString, ml);
    }

    public static <O extends Observation> Observer<O> loggingSink(String source, Function<O, String> mapper, Observer<? super LogMessage> ml) {
        return o -> ml.observe(new LogMessage(source, mapper.apply(o)));
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <O extends Observation> Observer<O>[] loggingSinks(String source, Observer<? super LogMessage>... mls) {
        return Arrays.stream(mls).map(ml -> loggingSink(source, ml)).toArray(Observer[]::new);
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <O extends Observation> Observer<O>[] loggingSinks(String source, Function<O, String> mapper, Observer<? super LogMessage>... mls) {
        return Arrays.stream(mls).map(ml -> loggingSink(source, mapper, ml)).toArray(Observer[]::new);
    }

    public static <O extends Observation> IdentityPipe<O> identityPipe() {
        return new IdentityPipe<>();
    }


    public static <O extends Observation> AsyncIdentityPipe<O> asynchronizer() {
        return asyncIdentityPipe();
    }


    public static <O extends Observation> AsyncIdentityPipe<O> asyncIdentityPipe() {
        return new AsyncIdentityPipe<>();
    }

    public static <O extends Observation> PredicatePipe<O> predicatePipe(Predicate<? super O> predicate) {
        return new PredicatePipe<>(predicate);
    }

    public static <O extends Observation> FilterPipe<O> filterPipe(Predicate<? super O> predicate) {
        return new FilterPipe<>(predicate);
    }


    public static <O extends Observation & Mergeable<? super O>> MergingSummarizingPipe<O> mergingSummarizingPipe() {
        return new MergingSummarizingPipe<>();
    }

    public static <O extends Observation & Mergeable<? super O>> TypeIdentTransformingPipe<O> accumulatingPipe(Supplier<O> initial) {
        return new AccumulatingPipe<>(initial);
    }

    public static <E extends Event> EventSupervision<E> eventSupervision() {
        return new EventSupervision<>();
    }


    public static MessageLogger fileLogger(String loggerName, String filePath) {
        return FileMessageLogger.create(loggerName, filePath);
    }

    public static MessageLogger consoleLogger() {
        return new ConsoleMessageLogger();
    }


    public static <O extends Observation> BufferPipe<O> buffer() {
        return new BufferPipe<>();
    }

    public static <O extends Observation> ConcurrencyBridge<O> concurrencyBridge() {
        return new ConcurrencyBridge<>();
    }


    public static <O extends Observation> BufferPipe<O> countingBuffer(int threshold) {
        return new CountingBufferPipe<>(threshold);
    }

    public static <O extends Observation> UnpackingPipe<O> unpackingPipe() {
        return new UnpackingPipe<>();
    }

    public static <O extends Observation> PackingPipe<O> packingPipe() {
        return new PackingPipe<>();
    }

    public static <O extends Observation> AsyncBufferPipe<O> asyncBuffer() {
        return new AsyncBufferPipe<>();
    }

    public static <I extends Observation, O extends Observation> SummarizingBufferPipe<I, O> summarizingBuffer(ObservationSummarizer<I, O> summarizer) {
        return new SummarizingBufferPipe<>(summarizer);
    }

    public static <I extends Observation, O extends Observation> SummarizingBufferPipe<I, O> selfEmptyingSummarizingBuffer(ObservationSummarizer<I, O> summarizer, int capacity) {
        return new SelfEmptyingSummarizingBufferPipe<>(summarizer, capacity);
    }

    public static <I extends Observation, O extends Observation> AsyncSummarizingBufferPipe<I, O> asyncSummarizingBuffer(ObservationSummarizer<I, O> summarizer) {
        return new AsyncSummarizingBufferPipe<>(summarizer);
    }

    public static <I extends Observation, O extends Observation> TransformingPipe<I, O> transformingPipe(ObservationTransformer<? super I, ? extends O> transformer) {
        return new TransformingPipe<>(transformer);
    }

    public static <I extends Observation, O extends Observation> AsyncTransformingPipe<I, O> asyncTransformingPipe(ObservationTransformer<? super I, ? extends O> transformer) {
        return new AsyncTransformingPipe<>(transformer);
    }

    public static <O extends Observation> ActionPipe<O> actionPipe(Consumer<O> action) {
        return new ActionPipe<>(action);
    }

    public static <O extends Observation> SkippingPipe<O> skipper(int interval) {
        return new SkippingPipe<>(interval);
    }

    public static <O extends Observation> TransformingPipe<O, TimedObservation<O>> timer() {
        return PipeWorks.transformingPipe(Transformers.addTime());
    }
}