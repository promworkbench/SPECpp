package org.processmining.specpp.datastructures.util.caching;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

public class BasicCache<K, V> {

    public static final int MAX_CACHE_SIZE = 1000;

    protected final Map<K, V> internal;
    private final Deque<K> keys;
    private final int capacity;

    public BasicCache() {
        this(MAX_CACHE_SIZE);
    }

    public BasicCache(int capacity) {
        this.capacity = capacity;
        internal = new HashMap<>();
        keys = new LinkedList<>();
    }

    public void put(K key, V value) {
        if (keys.size() >= capacity) {
            K oldestKey = keys.removeFirst();
            internal.remove(oldestKey);
        }
        internal.put(key, value);
        keys.addLast(key);
    }

    public V getOrElse(K key, Function<K, V> computer) {
        if (!internal.containsKey(key)) return computer.apply(key);
        else return internal.get(key);
    }


    public V getOrCompute(K key, Function<K, V> computer) {
        if (!internal.containsKey(key)) {
            put(key, computer.apply(key));
        }
        return internal.get(key);
    }

}
