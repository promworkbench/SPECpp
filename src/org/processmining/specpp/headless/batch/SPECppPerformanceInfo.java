package org.processmining.specpp.headless.batch;

import org.processmining.specpp.composition.BasePlaceComposition;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.orchestra.ExecutionEnvironment;
import org.processmining.specpp.prom.computations.OngoingComputation;
import org.processmining.specpp.prom.computations.OngoingStagedComputation;

import java.util.Objects;

public class SPECppPerformanceInfo extends BatchedExecutionResult {

    public static final String[] COLUMN_NAMES = new String[]{"run identifier", "started", "completed", "pec cycling [ms]", "post processing [ms]", "total [ms]", "pec cycles", "pec cycling cancelled?", "terminated successfully?"};

    private final ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> execution;

    public SPECppPerformanceInfo(String runIdentifier, ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> execution) {
        super(runIdentifier, "SPECppPerformanceInfo");
        this.execution = execution;
    }

    public ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> getExecution() {
        return execution;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String[] toRow() {
        OngoingComputation mc = execution.getMasterComputation();
        OngoingComputation dc = execution.getDiscoveryComputation();
        OngoingStagedComputation ppc = execution.getPostProcessingComputation();

        return new String[]{runIdentifier, Objects.toString(mc.getStart()), Objects.toString(mc.getEnd()), dc.hasTerminated() ? Long.toString(dc.calculateRuntime()
                                                                                                                                                .toMillis()) : "dnf", ppc.hasTerminated() ? Long.toString(ppc.calculateRuntime()
                                                                                                                                                                                                             .toMillis()) : "dnf", mc.hasTerminated() ? Long.toString(mc.calculateRuntime()
                                                                                                                                                                                                                                                                        .toMillis()) : "dnf", Integer.toString(execution.getSPECpp()
                                                                                                                                                                                                                                                                                                                        .currentCycleCount()), Boolean.toString(execution.wasDiscoveryCancelledGracefully()), Boolean.toString(execution.hasTerminatedSuccessfully())};

    }
}
