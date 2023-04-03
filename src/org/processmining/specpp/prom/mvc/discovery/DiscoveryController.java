package org.processmining.specpp.prom.mvc.discovery;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.base.impls.SPECppBuilder;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.config.SPECppConfigBundle;
import org.processmining.specpp.config.components.Configuration;
import org.processmining.specpp.config.parameters.ExecutionParameters;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.orchestra.ExternalInitializer;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.prom.computations.OngoingComputation;
import org.processmining.specpp.prom.computations.OngoingStagedComputation;
import org.processmining.specpp.prom.mvc.AbstractStageController;
import org.processmining.specpp.prom.mvc.SPECppController;
import org.processmining.specpp.prom.util.Destructible;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

public class DiscoveryController extends AbstractStageController implements Destructible {

    private final SPECpp<Place, AdvancedComposition<Place>, CollectionOfPlaces, ProMPetrinetWrapper> specpp;
    private final DelegatingDataSource<Runnable> gracefulCancellationDelegate;
    private final OngoingComputation ongoingDiscoveryComputation;
    private final OngoingStagedComputation ongoingPostProcessingComputation;
    private final ExecutionParameters.ExecutionTimeLimits timeLimits;
    private final List<Timer> startedTimers = new LinkedList<>();
    private final ListeningExecutorService executorService;
    private final GlobalComponentRepository gcr;
    private final ExternalInitializer externalInitializer;
    private List<Result> intermediateResults;

    public DiscoveryController(SPECppController parentController) {
        super(parentController);
        executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        InputDataBundle dataBundle = parentController.getDataBundle();
        SPECppConfigBundle configBundle = parentController.getConfigBundle();

        gcr = new GlobalComponentRepository();
        gracefulCancellationDelegate = new DelegatingDataSource<>();
        gcr.require(DataRequirements.dataSource("cancel_gracefully", Runnable.class), gracefulCancellationDelegate);

        configBundle.instantiate(gcr, dataBundle);
        Configuration configuration = new Configuration(gcr);

        ExecutionParameters executionParameters = gcr.parameters()
                                                     .askForData(ParameterRequirements.EXECUTION_PARAMETERS);
        timeLimits = executionParameters.getTimeLimits();

        externalInitializer = configuration.createFrom(ExternalInitializer::new);
        specpp = configuration.createFrom(new SPECppBuilder<>(), gcr);
        specpp.init();

        ongoingDiscoveryComputation = new OngoingComputation();
        ongoingDiscoveryComputation.setCancellationCallback(this::cancelDiscoveryComputation);
        ongoingPostProcessingComputation = new OngoingStagedComputation(specpp.getPostProcessor().getPipelineLength());
        ongoingPostProcessingComputation.setCancellationCallback(this::cancelPostProcessingComputation);
    }

    @Override
    public void startup() {
        startDiscovery();
    }

    public SPECpp<Place, AdvancedComposition<Place>, CollectionOfPlaces, ProMPetrinetWrapper> getSpecpp() {
        return specpp;
    }

    public void startDiscovery() {
        if (ongoingDiscoveryComputation.isCancelled()) return;

        specpp.start();

        externalInitializer.init();

        LocalDateTime startTime = LocalDateTime.now();
        if (timeLimits.hasDiscoveryTimeLimit())
            ongoingDiscoveryComputation.setTimeLimit(timeLimits.getDiscoveryTimeLimit());
        else if (timeLimits.hasTotalTimeLimit())
            ongoingDiscoveryComputation.setTimeLimit(timeLimits.getTotalTimeLimit());
        ongoingDiscoveryComputation.setStart(startTime);

        ListenableFuture<CollectionOfPlaces> future = getExecutor().submit(specpp::executeDiscovery);
        ongoingDiscoveryComputation.setComputationFuture(future);
        future.addListener(this::discoveryFinished, MoreExecutors.sameThreadExecutor());

        if (timeLimits.hasDiscoveryTimeLimit()) {
            Timer cancellationTimer = new Timer((int) timeLimits.getDiscoveryTimeLimit()
                                                                .toMillis(), e -> cancelDiscoveryComputation());
            cancellationTimer.setRepeats(false);
            startedTimers.add(cancellationTimer);
            cancellationTimer.start();
        }
        if (timeLimits.hasTotalTimeLimit()) {
            Timer cancellationTimer = new Timer((int) timeLimits.getTotalTimeLimit()
                                                                .toMillis(), e -> cancelEverything());
            cancellationTimer.setRepeats(false);
            startedTimers.add(cancellationTimer);
            cancellationTimer.start();
        }
    }

    private void discoveryFinished() {
        ongoingDiscoveryComputation.setEnd(LocalDateTime.now());
        if (specpp.getInitialResult() != null)
            startPostProcessing();
        else cancelEverything();
    }

    public OngoingComputation getOngoingDiscoveryComputation() {
        return ongoingDiscoveryComputation;
    }

    public OngoingStagedComputation getOngoingPostProcessingComputation() {
        return ongoingPostProcessingComputation;
    }

    private void startPostProcessing() {
        if (ongoingPostProcessingComputation.isCancelled()) return;
        ongoingPostProcessingComputation.setStart(LocalDateTime.now());
        intermediateResults = new LinkedList<>();
        ListenableFuture<ProMPetrinetWrapper> postProcessingFuture = getExecutor().submit(() -> specpp.executePostProcessing(e -> {
            intermediateResults.add(e);
            ongoingPostProcessingComputation.incStage();
        }));
        ongoingPostProcessingComputation.setComputationFuture(postProcessingFuture);
        postProcessingFuture.addListener(this::postProcessingFinished, MoreExecutors.sameThreadExecutor());

        if (timeLimits.hasPostProcessingTimeLimit()) {
            Timer cancellationTimer = new Timer(((int) timeLimits.getPostProcessingTimeLimit()
                                                                 .toMillis()), e -> cancelPostProcessingComputation());
            cancellationTimer.setRepeats(false);
            startedTimers.add(cancellationTimer);
            cancellationTimer.start();
        }
    }

    private ListeningExecutorService getExecutor() {
        return executorService;
    }

    private void postProcessingFinished() {
        ongoingPostProcessingComputation.setEnd(LocalDateTime.now());
        if (specpp.isActive()) specpp.stop();
        if (!ongoingPostProcessingComputation.isCancelled()) {
            parentController.discoveryCompleted(specpp.getPostProcessedResult(), intermediateResults);
        }
    }

    public void cancelDiscoveryComputation() {
        gracefulCancellationDelegate.getData().run();
        ongoingDiscoveryComputation.markGracefullyCancelled();
    }

    public void cancelPostProcessingComputation() {
        ListenableFuture<?> future = ongoingPostProcessingComputation.getComputationFuture();
        if (future != null && !future.isDone()) future.cancel(true);
        ongoingPostProcessingComputation.markForciblyCancelled();
        if (specpp.isActive()) specpp.stop();
    }

    private void cancelEverything() {
        cancelDiscoveryComputation();
        cancelPostProcessingComputation();
        if (specpp.isActive()) specpp.stop();
    }

    @Override
    public JPanel createPanel() {
        return new DiscoveryPanel(this);
    }


    @Override
    public void destroy() {
        cancelEverything();
        for (Timer timer : startedTimers) {
            timer.stop();
        }
    }

    public void continueToResults() {
        if (ongoingPostProcessingComputation.hasTerminatedSuccessfully()) parentController.tryAdvancingToResults();
    }
}
