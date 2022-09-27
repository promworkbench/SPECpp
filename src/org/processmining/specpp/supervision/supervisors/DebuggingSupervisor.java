package org.processmining.specpp.supervision.supervisors;

import org.apache.log4j.Level;
import org.processmining.specpp.supervision.MessageLogger;
import org.processmining.specpp.supervision.observations.DebugEvent;
import org.processmining.specpp.supervision.observations.LogMessage;
import org.processmining.specpp.supervision.piping.Observer;
import org.processmining.specpp.supervision.piping.PipeWorks;
import org.processmining.specpp.supervision.transformers.Transformers;

import java.util.Objects;

public class DebuggingSupervisor extends SubSupervisor {


    private static MessageLogger staticDebuggingLogger;

    public DebuggingSupervisor() {

    }

    @Override
    protected void instantiateObservationHandlingFullySatisfied() {

    }

    public static void debug(DebugEvent e) {
        getDebugLogger().observe(Transformers.toLogMessage(Level.DEBUG).apply(e));
    }

    private static Observer<LogMessage> getDebugLogger() {
        if (staticDebuggingLogger == null) staticDebuggingLogger = PipeWorks.consoleLogger();
        return staticDebuggingLogger;
    }

    public static void debug(Object o) {
        debug(new DebugEvent(Objects.toString(o)));
    }

    public static void debug(String desc, Object o) {
        debug(new DebugEvent(desc + ": " + o));
    }


}
