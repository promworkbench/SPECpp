package org.processmining.specpp.traits;

public interface Copyable<T extends Copyable<T>> {

    T copy();

}
