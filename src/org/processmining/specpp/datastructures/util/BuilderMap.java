package org.processmining.specpp.datastructures.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class BuilderMap<K, B, V> {

    private final Map<K, B> internal;
    private final Supplier<B> builderSupplier;
    private final BiConsumer<B, V> buildUpAction;

    public BuilderMap(Supplier<B> builderSupplier, BiConsumer<B, V> buildUpAction) {
        this.internal = new HashMap<>();
        this.builderSupplier = builderSupplier;
        this.buildUpAction = buildUpAction;
    }

    public void add(K key, V value) {
        if (!internal.containsKey(key)) internal.put(key, builderSupplier.get());
        buildUpAction.accept(internal.get(key), value);
    }

    public B get(K key) {
        return internal.get(key);
    }

    public Set<Map.Entry<K, B>> entrySet() {
        return internal.entrySet();
    }

    public Map<K, B> getMap() {
        return internal;
    }

}
