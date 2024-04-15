package org.processmining.specpp.headless.batch;

import org.processmining.specpp.util.EvalUtils;

public class SPECppEvaluationInfo extends BatchedExecutionResult {

    public static final String[] COLUMN_NAMES = new String[]{"run identifier", "perfectly fitting traces", "alignment based fitness", "etc precision", "f1", "duration"};


    private final double fittingTraces, alignmentFitness, etcPrecision;
    private final long duration;

    public SPECppEvaluationInfo(String runIdentifier, double fittingTraces, double alignmentFitness, double etcPrecision, long duration) {
        super(runIdentifier, "SPECppEvaluated");
        this.fittingTraces = fittingTraces;
        this.alignmentFitness = alignmentFitness;
        this.etcPrecision = etcPrecision;
        this.duration = duration;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String[] toRow() {
        return new String[]{runIdentifier, Double.toString(fittingTraces), Double.toString(alignmentFitness), Double.toString(etcPrecision), Double.toString(EvalUtils.computeF1(alignmentFitness, etcPrecision)), Long.toString(duration)};
    }

}
