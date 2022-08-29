package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.traits.ThreadsafeBuffer;

public abstract class AbstractBufferingPipe<I extends Observation, O extends Observation> extends AbstractAsyncAwareObservable<O> implements ObservationPipe<I, O>, Buffering {
    protected final Buffer<I> buffer;
    public final boolean hasThreadsafeBuffer;

    public AbstractBufferingPipe(boolean useThreadsafeBuffer) {
        this.buffer = useThreadsafeBuffer ? new ConcurrentBuffer<>() : new BasicBuffer<>();
        hasThreadsafeBuffer = useThreadsafeBuffer;
    }

    public AbstractBufferingPipe(Buffer<I> buffer) {
        this.buffer = buffer;
        hasThreadsafeBuffer = buffer instanceof ThreadsafeBuffer;
    }

    public boolean hasThreadsafeBuffer() {
        return hasThreadsafeBuffer;
    }

    protected void buffer(I observation) {
        buffer.store(observation);
    }

    protected abstract O collect(Observations<I> bufferedObservations);

    protected ObservationCollection<I> drainBuffer() {
        return new ObservationCollection<>(buffer.drain());
    }

    @Override
    public void flushBuffer() {
        publish(collect(drainBuffer()));
    }

    @Override
    public boolean isBufferNonEmpty() {
        return !buffer.isEmpty();
    }

    @Override
    public void observe(I observation) {
        buffer(observation);
    }
}
