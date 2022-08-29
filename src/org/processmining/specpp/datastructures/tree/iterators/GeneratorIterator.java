package org.processmining.specpp.datastructures.tree.iterators;

import java.util.Iterator;
import java.util.function.Function;

public class GeneratorIterator<K> implements Iterator<K> {

    private int nextElementId, count;
    private final int limit;
    private K nextElement;
    private final Function<Integer, K> generator;

    public GeneratorIterator(Function<Integer, K> generator, int startId, int limit) {
        this.generator = generator;
        this.nextElementId = startId;
        this.limit = limit;
        count = 0;
        nextElement = null;
        advanceGenerator();
    }

    private void advanceGenerator() {
        nextElement = generator.apply(nextElementId++);
    }

    @Override
    public boolean hasNext() {
        return count < limit && nextElement != null;
    }

    @Override
    public K next() {
        K temp = nextElement;
        advanceGenerator();
        count++;
        return temp;
    }
}
