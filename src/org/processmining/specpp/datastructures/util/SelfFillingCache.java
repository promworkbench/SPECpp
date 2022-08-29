package org.processmining.specpp.datastructures.util;

public interface SelfFillingCache<K, V> {

    V get(K key);

}
