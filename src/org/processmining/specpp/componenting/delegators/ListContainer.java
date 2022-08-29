package org.processmining.specpp.componenting.delegators;

import java.util.LinkedList;
import java.util.List;

public class ListContainer<T> implements Container<T> {

    private final List<T> contents;

    public ListContainer() {
        contents = new LinkedList<>();
    }

    @Override
    public void addContent(T content) {
        contents.add(content);
    }

    @Override
    public boolean isEmpty() {
        return contents.isEmpty();
    }

    @Override
    public boolean isFull() {
        return false;
    }

    public List<T> getContents() {
        return contents;
    }
}
