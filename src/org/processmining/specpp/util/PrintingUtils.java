package org.processmining.specpp.util;

import org.processmining.specpp.componenting.data.DataSourceCollection;
import org.processmining.specpp.componenting.data.FulfilledDataRequirement;
import org.processmining.specpp.orchestra.ExecutionEnvironment;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class PrintingUtils {
    public static String quote(Object o) {
        return "\"" + o + "\"";
    }

    public static String parametersToPrettyString(DataSourceCollection parameters) {
        return parameters.fulfilledRequirements()
                         .stream()
                         .map(f -> (FulfilledDataRequirement<?>) f)
                         .map(f -> "\t" + f.getComparable().toString() + " = " + f.getContent().getData().toString())
                         .collect(Collectors.joining("\n", "Configured Parameters:\n", ""));
    }

    public static String stringifyComputationStatuses(ExecutionEnvironment.SPECppExecution<?, ?, ?, ?> execution) {
        return "\t" + "PEC-cycling: " + execution.getDiscoveryComputation() + "\n" + "\t" + "Post Processing: " + execution.getPostProcessingComputation() + "\n\t" + "Overall: " + execution.getMasterComputation();
    }

    public static String stringifyRow(String[] columnLabels, String[] row) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < columnLabels.length; i++) {
            sb.append(columnLabels[i]).append(": ").append(row[i]);
            if (i < columnLabels.length - 1) sb.append(", ");
        }
        return sb.append("}").toString();
    }

    public static String stringifyThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        return String.format("pool size: %d/%d(peak: %d), active threads: %d, queue length: %d", threadPoolExecutor.getPoolSize(), threadPoolExecutor.getMaximumPoolSize(), threadPoolExecutor.getLargestPoolSize(), threadPoolExecutor.getActiveCount(), threadPoolExecutor.getQueue()
                                                                                                                                                                                                                                                                            .size());
    }
}
