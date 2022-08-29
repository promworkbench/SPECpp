package org.processmining.specpp.componenting.delegators;

import java.util.function.Consumer;

public class ConsumingContainer<T> implements Container<T> {

    protected final Consumer<T> consumer;
    private boolean hasConsumedOnce = false;

    public ConsumingContainer(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void addContent(T content) {
        consumer.accept(content);
        hasConsumedOnce = true;
    }

    @Override
    public boolean isEmpty() {
        return !hasConsumedOnce;
    }

    @Override
    public boolean isFull() {
        return false;
    }
}
