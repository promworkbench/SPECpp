package org.processmining.specpp.util;

import org.processmining.specpp.componenting.data.DataSourceCollection;
import org.processmining.specpp.componenting.data.FulfilledDataRequirement;

import java.util.stream.Collectors;

public class PrintingUtils {
    public static String quote(Object o) {
        return "\"" + o + "\"";
    }


    public static String printParameters(DataSourceCollection parameters) {
        return parameters
                .fulfilledRequirements()
                .stream()
                .map(f -> (FulfilledDataRequirement<?>) f)
                .map(f -> "\t" + f.getComparable().toString() + " = " + f.getContent()
                                                                         .getData()
                                                                         .toString())
                .collect(Collectors.joining("\n", "Configured Parameters:\n", ""));
    }
}
