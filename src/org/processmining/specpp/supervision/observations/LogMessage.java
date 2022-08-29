package org.processmining.specpp.supervision.observations;


import org.apache.log4j.Level;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogMessage implements Observation {
    private final String source;
    private final String message;
    private final Level logLevel;
    private final LocalDateTime time;


    public LogMessage(String source, String message) {
        this(source, message, Level.DEBUG, LocalDateTime.now());
    }

    public LogMessage(String source, String message, Level logLevel, LocalDateTime time) {
        this.source = source;
        this.message = message;
        this.logLevel = logLevel;
        this.time = time;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    @Override
    public String toString() {
        return source + " @ " + time.format(DateTimeFormatter.ISO_LOCAL_TIME) + ": " + message;
    }


}
