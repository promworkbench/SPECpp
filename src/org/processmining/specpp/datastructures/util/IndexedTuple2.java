package org.processmining.specpp.datastructures.util;

import java.util.function.Consumer;

public class IndexedTuple2<T1, T2> extends MutableTuple2<T1, T2> {

    protected int index;

    public IndexedTuple2() {
    }

    public IndexedTuple2(int index, T1 t1, T2 t2) {
        super(t1, t2);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void map(Consumer<MutableTuple2<T1, T2>> action) {
        action.accept(this);
    }

}
