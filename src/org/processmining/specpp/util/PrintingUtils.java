package org.processmining.specpp.util;

import org.jbpt.petri.querying.IStructuralQuerying;
import org.processmining.specpp.componenting.data.DataSourceCollection;
import org.processmining.specpp.componenting.data.FulfilledDataRequirement;
import org.processmining.specpp.datastructures.encoding.IntEncoding;
import org.processmining.specpp.orchestra.ExecutionEnvironment;

import java.util.stream.Collectors;

public class PrintingUtils {
    public static String quote(Object o) {
        return "\"" + o + "\"";
    }

    public static String parametersToPrettyString(DataSourceCollection parameters) {
        return parameters
                .fulfilledRequirements()
                .stream()
                .map(f -> (FulfilledDataRequirement<?>) f)
                .map(f -> "\t" + f.getComparable().toString() + " = " + f.getContent()
                                                                         .getData()
                                                                         .toString())
                .collect(Collectors.joining("\n", "Configured Parameters:\n", ""));
    }

    public static String stringifyComputationStatuses(ExecutionEnvironment.SPECppExecution<?, ?, ?, ?> execution) {
        return "\t" + "PEC-cycling: " + execution.getDiscoveryComputation() + "\n" + "\t" + "Post Processing: " + execution.getPostProcessingComputation() + "\n\t" + "Overall: " + execution.getMasterComputation();
    }
}
