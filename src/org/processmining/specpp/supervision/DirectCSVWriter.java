package org.processmining.specpp.supervision;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.AsyncObserver;
import org.processmining.specpp.traits.Stoppable;
import org.processmining.specpp.util.FileUtils;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class DirectCSVWriter<O extends Observation> implements AsyncObserver<O>, Stoppable {

    private final Function<O, String[]> rowMapper;
    private final com.opencsv.CSVWriter csvWriter;


    public DirectCSVWriter(String filePath, String[] columnLabels, Function<O, String[]> rowMapper) {
        this.rowMapper = rowMapper;
        csvWriter = FileUtils.createCSVWriter(filePath);
        csvWriter.writeNext(columnLabels);
    }

    private void handleObservation(O observation) {
        String[] row = rowMapper.apply(observation);
        csvWriter.writeNext(row);
        csvWriter.flushQuietly(); // fuck you opencsv
    }

    @Override
    public void observeAsync(CompletableFuture<O> futureObservation) {
        futureObservation.thenAccept(this::handleObservation);
    }

    @Override
    public void observe(O observation) {
        handleObservation(observation);
    }


    @Override
    public void stop() {
        try {
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    protected void finalize() throws Throwable {
        stop();
        super.finalize();
    }

}
