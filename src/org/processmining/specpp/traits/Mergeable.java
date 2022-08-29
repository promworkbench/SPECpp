package org.processmining.specpp.traits;

public interface Mergeable<T> extends Mutable {

    void merge(T other);

}
