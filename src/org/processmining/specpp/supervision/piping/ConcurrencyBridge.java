package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.traits.RequiresSupportingTask;

import java.util.concurrent.LinkedBlockingQueue;

public class ConcurrencyBridge<O extends Observation> extends AbstractAsyncAwareObservable<O> implements TypeIdentPipe<O>, RequiresSupportingTask {

    private final LinkedBlockingQueue<O> blockingQueue;
    private final Runnable bufferClearingTask;

    public ConcurrencyBridge() {
        blockingQueue = new LinkedBlockingQueue<>();
        bufferClearingTask = this::continuousBufferClearing;
    }


    @Override
    public void observe(O observation) {
        blockingQueue.offer(observation);
    }

    private void continuousBufferClearing() {
        while (true) {
            try {
                O poll = blockingQueue.take();
                publish(poll);
                while (poll != null && !blockingQueue.isEmpty()) {
                    poll = blockingQueue.poll();
                    publish(poll);
                }
                Thread.yield();
            } catch (InterruptedException ignored) {
                break;
            }
        }
    }

    @Override
    public Runnable getSupportingTask() {
        return bufferClearingTask;
    }

    @Override
    public String toString() {
        return "ConcurrencyBridge(" + "|Q|=" + blockingQueue.size() + ")";
    }
}
