package org.processmining.specpp.datastructures.util;

import org.processmining.specpp.traits.ProperlyPrintable;

public class TypedItem<T> extends ImmutableTuple2<Class<? extends T>, T> implements ProperlyPrintable {

    public TypedItem(Class<? extends T> tClass, T t) {
        super(tClass, t);
    }

    public Class<? extends T> getType() {
        return t1;
    }

    public T getItem() {
        return t2;
    }

    @Override
    public String toString() {
        return "TypedItem{" + getType().getSimpleName() + ": " + getItem() + '}';
    }

}
