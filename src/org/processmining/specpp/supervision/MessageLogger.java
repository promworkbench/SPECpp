package org.processmining.specpp.supervision;

import org.apache.log4j.Logger;
import org.processmining.specpp.supervision.observations.LogMessage;
import org.processmining.specpp.supervision.piping.AsyncObserver;

import java.util.concurrent.CompletableFuture;

public abstract class MessageLogger implements AsyncObserver<LogMessage> {
    protected final Logger loggerInstance;

    public MessageLogger(Logger loggerInstance) {
        this.loggerInstance = loggerInstance;
    }

    private void log(LogMessage message) {
        loggerInstance.log(message.getLogLevel(), message.toString());
    }

    @Override
    public void observeAsync(CompletableFuture<LogMessage> futureObservation) {
        futureObservation.thenAcceptAsync(this::log);
    }

    @Override
    public void observe(LogMessage message) {
        log(message);
    }
}
