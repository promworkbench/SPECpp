package org.processmining.specpp.datastructures.tree.iterators;

import java.util.Iterator;
import java.util.function.Function;

public class GeneratorIterable<K> implements Iterable<K> {

    private final Function<Integer, K> generator;
    private final int startId;
    private final int limit;

    public GeneratorIterable(Function<Integer, K> generator, int startId, int limit) {
        this.generator = generator;
        this.startId = startId;
        this.limit = limit;
    }

    @Override
    public Iterator<K> iterator() {
        return new GeneratorIterator<>(generator, startId, limit);
    }
}
