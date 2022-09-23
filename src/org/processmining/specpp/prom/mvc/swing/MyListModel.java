package org.processmining.specpp.prom.mvc.swing;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class MyListModel<T> extends AbstractListModel<T> implements Iterable<T> {

    private final List<T> internal;

    public MyListModel() {
        internal = new ArrayList<>();
    }

    public MyListModel(List<T> internal) {
        this.internal = new ArrayList<>(internal);
    }

    @Override
    public int getSize() {
        return internal.size();
    }

    @Override
    public T getElementAt(int index) {
        return internal.get(index);
    }

    public void append(T item) {
        internal.add(item);
        int index = internal.size() - 1;
        fireIntervalAdded(this, index, index);
    }

    public void appendAll(Collection<T> items) {
        int oldIndex = internal.size();
        internal.addAll(items);
        int index = internal.size() - 1;
        fireIntervalAdded(this, oldIndex, index);
    }

    public void insert(T item, int index) {
        internal.add(index, item);
        fireIntervalAdded(this, index, index);
    }

    public void clear() {
        if (internal.isEmpty()) return;
        int index = internal.size() - 1;
        internal.clear();
        fireIntervalRemoved(this, 0, index);
    }

    public void remove(int index) {
        if (index < 0 || index >= internal.size()) return;
        internal.remove(index);
        fireIntervalRemoved(this, index, index);
    }

    @Override
    public ListIterator<T> iterator() {
        return internal.listIterator();
    }
}
