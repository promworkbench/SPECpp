package org.processmining.specpp.traits;

public interface DeepCopyable<T extends DeepCopyable<T>> {

    T deepCopy();

}
