package org.processmining.specpp.supervision;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.SimpleLayout;

public class ConsoleMessageLogger extends MessageLogger {

    static {
        ConsoleAppender consoleAppender = new ConsoleAppender(new SimpleLayout(), ConsoleAppender.SYSTEM_OUT);
        LogManager.getLogger("SPECPP Console Logger").addAppender(consoleAppender);
    }

    public ConsoleMessageLogger() {
        super(LogManager.getLogger("SPECPP Console Logger"));
    }
}
