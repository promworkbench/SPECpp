package org.processmining.specpp.prom.util;

import javax.swing.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class DummyWorker<T> extends SwingWorker<T, Void> {

    private final CompletableFuture<T> future;
    private final Consumer<T> onSuccess;
    private final Runnable onCancellation;

    public DummyWorker(CompletableFuture<T> future, Consumer<T> onSuccess, Runnable onCancellation) {
        this.future = future;
        this.onSuccess = onSuccess;
        this.onCancellation = onCancellation;
    }

    @Override
    protected T doInBackground() throws Exception {
        return future.get();
    }

    @Override
    protected void done() {
        if (isCancelled()) onCancellation.run();
        else {
            try {
                T t = get();
                onSuccess.accept(t);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
