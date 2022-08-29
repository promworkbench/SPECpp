package org.processmining.specpp.supervision.piping;

import org.processmining.specpp.supervision.traits.ThreadsafeBuffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentBuffer<E> implements Buffer<E>, ThreadsafeBuffer {

    private final ConcurrentLinkedQueue<E> internal;

    public ConcurrentBuffer() {
        internal = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void store(E element) {
        internal.offer(element);
    }

    @Override
    public void storeAll(Collection<E> elements) {
        for (E element : elements) {
            store(element);
        }
    }

    public List<E> flush(int limit) {
        List<E> result = new ArrayList<>();
        int i = 0;
        while (i++ < limit && !internal.isEmpty()) {
            E element = internal.poll();
            result.add(element);
        }
        return result;
    }

    @Override
    public List<E> drain() {
        List<E> result = new ArrayList<>();
        while (!internal.isEmpty()) {
            E poll = internal.poll();
            if (poll != null) result.add(poll);
        }
        return result;
    }

    @Override
    public int size() {
        return internal.size();
    }

    @Override
    public boolean isEmpty() {
        return internal.isEmpty();
    }

}
