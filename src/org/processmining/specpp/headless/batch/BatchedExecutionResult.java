package org.processmining.specpp.headless.batch;

import org.processmining.specpp.supervision.observations.CSVRowEvent;
import org.processmining.specpp.util.PrintingUtils;

import java.util.Objects;

public abstract class BatchedExecutionResult implements CSVRowEvent {


    protected final String runIdentifier, resultTypeIdentifier;

    protected BatchedExecutionResult(String runIdentifier, String resultTypeIdentifier) {
        this.runIdentifier = runIdentifier;
        this.resultTypeIdentifier = resultTypeIdentifier;
    }

    @Override
    public String toString() {
        return resultTypeIdentifier + PrintingUtils.stringifyRow(getColumnNames(), toRow());
    }

    @Override
    public abstract String[] getColumnNames();

    public String getRunIdentifier() {
        return runIdentifier;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BatchedExecutionResult that = (BatchedExecutionResult) o;

        if (!Objects.equals(runIdentifier, that.runIdentifier)) return false;
        return Objects.equals(resultTypeIdentifier, that.resultTypeIdentifier);
    }

    @Override
    public int hashCode() {
        int result = runIdentifier != null ? runIdentifier.hashCode() : 0;
        result = 31 * result + (resultTypeIdentifier != null ? resultTypeIdentifier.hashCode() : 0);
        return result;
    }
}
