package org.processmining.specpp.supervision;

import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.supervision.piping.AsyncObserver;
import org.processmining.specpp.supervision.piping.Buffer;
import org.processmining.specpp.supervision.piping.Buffering;
import org.processmining.specpp.supervision.piping.ConcurrentBuffer;
import org.processmining.specpp.traits.Stoppable;
import org.processmining.specpp.util.FileUtils;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CSVWriter<O extends Observation> implements AsyncObserver<O>, Buffering, Stoppable {

    private final Function<O, String[]> rowMapper;
    private final Buffer<String[]> buffer;
    private final com.opencsv.CSVWriter csvWriter;


    public CSVWriter(String filePath, String[] columnLabels, Function<O, String[]> rowMapper) {
        this.rowMapper = rowMapper;
        buffer = new ConcurrentBuffer<>();
        csvWriter = new com.opencsv.CSVWriter(FileUtils.createOutputFileWriter(filePath));
        csvWriter.writeNext(columnLabels);
    }

    private void handleObservation(O observation) {
        String[] row = rowMapper.apply(observation);
        buffer.store(row);
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
    public void flushBuffer() {
        csvWriter.writeAll(buffer.drain());
    }

    @Override
    public boolean isBufferNonEmpty() {
        return !buffer.isEmpty();
    }

    @Override
    public void stop() {
        flushBuffer();
        try {
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException ignored) {
        }
    }

}
