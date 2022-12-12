package org.processmining.specpp.config.parameters;

import java.time.Duration;

public class ExecutionParameters implements Parameters {

    private final ParallelizationTarget parallelizationTarget;
    private final PerformanceFocus performanceFocus;
    private final ExecutionTimeLimits timeLimits;

    public ExecutionParameters(ExecutionTimeLimits timeLimits, ParallelizationTarget parallelizationTarget, PerformanceFocus performanceFocus) {
        this.timeLimits = timeLimits;
        this.parallelizationTarget = parallelizationTarget;
        this.performanceFocus = performanceFocus;
    }

    public static ExecutionParameters timeouts(ExecutionTimeLimits timeLimits) {
        return new ExecutionParameters(timeLimits, ParallelizationTarget.None, PerformanceFocus.Balanced);
    }

    public static ExecutionParameters noTimeouts() {
        return new ExecutionParameters(ExecutionTimeLimits.empty(), ParallelizationTarget.None, PerformanceFocus.Balanced);
    }

    public ExecutionTimeLimits getTimeLimits() {
        return timeLimits;
    }

    public static class ExecutionTimeLimits {
        private final Duration discoveryTimeLimit, postProcessingTimeLimit, totalTimeLimit;

        public ExecutionTimeLimits(Duration discoveryTimeLimit, Duration postProcessingTimeLimit, Duration totalTimeLimit) {
            assert totalTimeLimit == null || discoveryTimeLimit == null || discoveryTimeLimit.compareTo(totalTimeLimit) <= 0;
            assert totalTimeLimit == null || postProcessingTimeLimit == null || postProcessingTimeLimit.compareTo(totalTimeLimit) <= 0;
            this.discoveryTimeLimit = discoveryTimeLimit;
            this.postProcessingTimeLimit = postProcessingTimeLimit;
            this.totalTimeLimit = totalTimeLimit;
        }

        public static ExecutionTimeLimits empty() {
            return new ExecutionTimeLimits(null, null, null);
        }

        public boolean hasDiscoveryTimeLimit() {
            return discoveryTimeLimit != null;
        }

        public boolean hasPostProcessingTimeLimit() {
            return postProcessingTimeLimit != null;
        }

        public boolean hasTotalTimeLimit() {
            return totalTimeLimit != null;
        }

        public Duration getTotalTimeLimit() {
            return totalTimeLimit;
        }

        public Duration getDiscoveryTimeLimit() {
            return discoveryTimeLimit;
        }

        public Duration getPostProcessingTimeLimit() {
            return postProcessingTimeLimit;
        }

        @Override
        public String toString() {
            return "ExecutionTimeLimits{" + "discoveryTimeLimit=" + discoveryTimeLimit + ", postProcessingTimeLimit=" + postProcessingTimeLimit + ", totalTimeLimit=" + totalTimeLimit + '}';
        }
    }


    public static ExecutionParameters getDefault() {
        return new ExecutionParameters(null, ParallelizationTarget.None, PerformanceFocus.Balanced);
    }

    public static ExecutionParameters withDiscoveryTimeLimit(Duration discoveryTimeLimit) {
        return new ExecutionParameters(new ExecutionTimeLimits(discoveryTimeLimit, null, null), ParallelizationTarget.Moderate, PerformanceFocus.Balanced);
    }

    public static ExecutionParameters withTotalTimeLimit(Duration totalTimeLimit) {
        return new ExecutionParameters(new ExecutionTimeLimits(null, null, totalTimeLimit), ParallelizationTarget.Moderate, PerformanceFocus.Balanced);
    }

    public boolean hasTimeLimit() {
        return timeLimits != null;
    }


    public ParallelizationTarget getParallelizationTarget() {
        return parallelizationTarget;
    }

    public PerformanceFocus getPerformanceFocus() {
        return performanceFocus;
    }

    public enum ParallelizationTarget {
        None, Moderate, Maximum
    }

    public enum PerformanceFocus {
        Balanced, Memory, CpuTime
    }

}
