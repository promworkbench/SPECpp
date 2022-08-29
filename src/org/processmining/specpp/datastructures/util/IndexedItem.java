package org.processmining.specpp.datastructures.util;

import org.processmining.specpp.traits.ProperlyPrintable;

import java.util.function.Function;
import java.util.function.IntUnaryOperator;

public class IndexedItem<T> implements ProperlyPrintable {

    private final int index;
    private final T item;

    public IndexedItem(int index, T item) {
        this.index = index;
        this.item = item;
    }

    public int getIndex() {
        return index;
    }

    public T getItem() {
        return item;
    }

    public <R> IndexedItem<R> map(Function<T, R> mapper) {
        return new IndexedItem<>(index, mapper.apply(item));
    }

    public IndexedItem<T> mapIndex(IntUnaryOperator indexMapper) {
        return new IndexedItem<>(indexMapper.applyAsInt(index), item);
    }

    @Override
    public String toString() {
        return "IndexedItem{" + "index=" + index + ", item=" + item + '}';
    }
}
