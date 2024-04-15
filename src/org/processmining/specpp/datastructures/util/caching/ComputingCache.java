package org.processmining.specpp.datastructures.util.caching;

import java.util.function.Function;

public class ComputingCache<K, V> extends BasicCache<K, V> implements SelfFillingCache<K, V> {
    private final Function<K, V> computationFunction;

    public ComputingCache(int capacity, Function<K, V> computationFunction) {
        super(capacity);
        this.computationFunction = computationFunction;
    }

    public ComputingCache(Function<K, V> computationFunction) {
        this.computationFunction = computationFunction;
    }

    public V get(K key) {
        return getOrCompute(key, computationFunction);
    }

    public Function<K, V> readOnlyGet() {
        return internal::get;
    }

}
