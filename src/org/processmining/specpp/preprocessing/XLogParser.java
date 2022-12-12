package org.processmining.specpp.preprocessing;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.log.utils.XUtils;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.ParsedLog;
import org.processmining.specpp.datastructures.log.Variant;
import org.processmining.specpp.datastructures.log.impls.*;
import org.processmining.specpp.datastructures.util.Counter;

import java.io.File;
import java.util.Map;

public class XLogParser {

    public static XLog readLog(String path) {
        try {
            return XUtils.loadLog(new File(path));
        } catch (Exception e) {
            throw new InputLoadingException(e);
        }
    }

    public static ParsedLog convertLog(XLog input, XEventClassifier eventClassifier, boolean introduceStartEndTransitions) {
        if (input == null) throw new InputLoadingException();

        Factory factory = new Factory(introduceStartEndTransitions);

        BidiMap<String, Activity> activities = new DualHashBidiMap<>();
        if (introduceStartEndTransitions) activities.putAll(Factory.getStartEndActivities());

        Counter<Variant> c = new Counter<>();
        for (XTrace trace : input) {
            VariantBuilder<VariantImpl> builder = factory.createVariantBuilder();
            for (XEvent event : trace) {
                String s = eventClassifier.getClassIdentity(event);
                if (!activities.containsKey(s)) activities.put(s, factory.createActivity(s));
                Activity activity = activities.get(s);
                builder.append(activity);
            }
            VariantImpl v = builder.build();
            c.inc(v);
        }
        LogBuilder<LogImpl> builder = factory.createLogBuilder();
        for (Map.Entry<Variant, Integer> entry : c.entrySet()) {
            builder.appendVariant(entry.getKey(), entry.getValue());
        }
        return new ParsedLog(builder.build(), activities);
    }

    public static class InputLoadingException extends RuntimeException {
        public InputLoadingException() {
        }

        public InputLoadingException(Throwable cause) {
            super(cause);
        }
    }

}
