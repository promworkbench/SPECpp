package org.processmining.specpp.datastructures.util.caching;

public interface SelfFillingCache<K, V> {

    V get(K key);

}
