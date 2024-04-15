package org.processmining.specpp.datastructures.util.caching;

public class StackedCache<K, V> implements SelfFillingCache<K, V> {
    private final BasicCache<K, V> left;
    private final SelfFillingCache<K, V> right;

    public StackedCache(BasicCache<K, V> left, SelfFillingCache<K, V> right) {
        this.left = left;
        this.right = right;
    }

    public V get(K key) {
        return left.getOrElse(key, right::get);
    }

}
