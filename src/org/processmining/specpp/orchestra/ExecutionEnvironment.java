package org.processmining.specpp.orchestra;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.componenting.system.link.CompositionComponent;
import org.processmining.specpp.config.parameters.ExecutionParameters;
import org.processmining.specpp.prom.computations.OngoingComputation;
import org.processmining.specpp.prom.computations.OngoingStagedComputation;
import org.processmining.specpp.traits.Joinable;
import org.processmining.specpp.util.StupidUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class ExecutionEnvironment implements Joinable, AutoCloseable {

    private final ScheduledExecutorService timeoutExecutorService;
    private final ExecutorService managerExecutorService, workerExecutorService, callbackExecutorService;
    private final List<ListenableFuture<?>> monitoredFutures;
    private final List<ListenableFuture<?>> monitoredCallbackFutures;
    private final int MAX_TERMINATION_WAIT = 5;
    private static final int CALLBACK_TIMEOUT = 5;


    @Override
    public void join() throws InterruptedException {
        managerExecutorService.shutdown();
        for (ListenableFuture<?> f : monitoredFutures) {
            try {
                f.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        workerExecutorService.shutdownNow();
        timeoutExecutorService.shutdownNow();
        for (ListenableFuture<?> f : monitoredCallbackFutures) {
            try {
                f.get(CALLBACK_TIMEOUT, TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                System.out.println("callback execution timed out");
            }
        }
        callbackExecutorService.shutdownNow();
        timeoutExecutorService.awaitTermination(MAX_TERMINATION_WAIT, TimeUnit.SECONDS);
        workerExecutorService.awaitTermination(MAX_TERMINATION_WAIT, TimeUnit.SECONDS);
        managerExecutorService.awaitTermination(MAX_TERMINATION_WAIT, TimeUnit.SECONDS);
        callbackExecutorService.awaitTermination(MAX_TERMINATION_WAIT, TimeUnit.SECONDS);
    }

    @Override
    public void close() throws InterruptedException {
        join();
    }

    public static class SPECppExecution<C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> {

        private final SPECpp<C, I, R, F> specpp;
        private final OngoingComputation masterComputation;
        private final OngoingComputation discoveryComputation;
        private final OngoingStagedComputation postProcessingComputation;
        private final List<ScheduledFuture<?>> timeKeepingFutures;

        public SPECppExecution(SPECpp<C, I, R, F> specpp, OngoingComputation masterComputation, OngoingComputation discoveryComputation, OngoingStagedComputation postProcessingComputation, List<ScheduledFuture<?>> timeKeepingFutures) {
            this.specpp = specpp;
            this.masterComputation = masterComputation;
            this.discoveryComputation = discoveryComputation;
            this.postProcessingComputation = postProcessingComputation;
            this.timeKeepingFutures = timeKeepingFutures;
        }

        public SPECpp<C, I, R, F> getSPECpp() {
            return specpp;
        }

        public OngoingComputation getDiscoveryComputation() {
            return discoveryComputation;
        }

        public OngoingStagedComputation getPostProcessingComputation() {
            return postProcessingComputation;
        }

        public List<ScheduledFuture<?>> getTimeKeepingFutures() {
            return timeKeepingFutures;
        }

        public boolean hasTerminatedSuccessfully() {
            return discoveryComputation.hasTerminatedSuccessfully() && postProcessingComputation.hasTerminatedSuccessfully() && masterComputation.hasTerminatedSuccessfully();
        }

        public OngoingComputation getMasterComputation() {
            return masterComputation;
        }

    }


    public ExecutionEnvironment() {
        this(3);
    }


    public ExecutionEnvironment(int num_threads) {
        int core_pool = Math.max(num_threads - 2, 1);

        managerExecutorService = Executors.newFixedThreadPool(core_pool, new ThreadFactoryBuilder().setNameFormat("manager-pool-thread-%d")
                                                                                                   .build());

        ThreadFactory workerThreadFactory = new ThreadFactoryBuilder().setNameFormat("worker-pool-thread-%d").build();
        ThreadPoolExecutor workerExecutor = new ThreadPoolExecutor(core_pool, core_pool, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), workerThreadFactory);
        workerExecutor.prestartAllCoreThreads();
        workerExecutorService = workerExecutor;

        callbackExecutorService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("callback-pool-thread-%d")
                                                                                              .build());

        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, new ThreadFactoryBuilder().setNameFormat("timekeeper-pool-thread-%d")
                                                                                                                               .build());
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        scheduledThreadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        scheduledThreadPoolExecutor.prestartCoreThread();
        scheduledThreadPoolExecutor.setMaximumPoolSize(1);
        timeoutExecutorService = scheduledThreadPoolExecutor;

        monitoredCallbackFutures = new ArrayList<>();
        monitoredFutures = new ArrayList<>();
    }

    public static <C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> SPECppExecution<C, I, R, F> oneshotExecution(SPECpp<C, I, R, F> specpp, ExecutionParameters executionParameters) {
        SPECppExecution<C, I, R, F> execution;
        try (ExecutionEnvironment ee = new ExecutionEnvironment(3)) {
            execution = ee.execute(specpp, executionParameters);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return execution;
    }

    public <C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> ListenableFuture<?> addCompletionCallback(SPECppExecution<C, I, R, F> execution, Consumer<SPECppExecution<C, I, R, F>> callback) {
        ListenableFutureTask<?> futureTask = ListenableFutureTask.create(() -> callback.accept(execution), null);
        execution.getMasterComputation().getComputationFuture().addListener(futureTask, callbackExecutorService);
        monitoredCallbackFutures.add(futureTask);
        return futureTask;
    }


    public <C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> SPECppExecution<C, I, R, F> execute(SPECpp<C, I, R, F> specpp, ExecutionParameters executionParameters) {

        OngoingComputation masterComputation = new OngoingComputation();
        OngoingComputation discoveryComputation = new OngoingComputation();
        OngoingStagedComputation postProcessingComputation = new OngoingStagedComputation(specpp.getPostProcessor()
                                                                                                .getPipelineLength());
        List<ScheduledFuture<?>> timeKeepingFutures = new LinkedList<>();
        SPECppExecution<C, I, R, F> execution = new SPECppExecution<>(specpp, masterComputation, discoveryComputation, postProcessingComputation, timeKeepingFutures);


        Runnable discoveryCanceller = () -> {
            specpp.cancelGracefully();
            discoveryComputation.markGracefullyCancelled();
        };
        Runnable postProcessingCanceller = () -> {
            postProcessingComputation.getComputationFuture().cancel(true);
        };
        Runnable totalCanceller = () -> {
            discoveryComputation.getComputationFuture().cancel(true);
            postProcessingComputation.getComputationFuture().cancel(true);
        };
        discoveryComputation.setCancellationCallback(discoveryCanceller);
        postProcessingComputation.setCancellationCallback(postProcessingCanceller);
        masterComputation.setCancellationCallback(totalCanceller);

        ExecutionParameters.ExecutionTimeLimits timeLimits = executionParameters.getTimeLimits();
        discoveryComputation.setTimeLimit(timeLimits.getDiscoveryTimeLimit());
        postProcessingComputation.setTimeLimit(timeLimits.getPostProcessingTimeLimit());
        masterComputation.setTimeLimit(timeLimits.getTotalTimeLimit());

        ListenableFutureTask<R> discoveryFuture = ListenableFutureTask.create(specpp::executeDiscovery);
        discoveryComputation.setComputationFuture(discoveryFuture);
        ListenableFutureTask<F> postProcessingFuture = ListenableFutureTask.create(specpp::executePostProcessing);
        postProcessingComputation.setComputationFuture(postProcessingFuture);

        ListenableFutureTask<Boolean> task = ListenableFutureTask.create(() -> {

            masterComputation.markStarted();
            discoveryComputation.markStarted();
            specpp.start();

            ScheduledFuture<?> discoveryCancellationFuture = null;
            try {
                workerExecutorService.submit(discoveryFuture);

                if (discoveryComputation.getTimeLimit() != null)
                    discoveryCancellationFuture = timeoutExecutorService.schedule(discoveryCanceller, discoveryComputation.getTimeLimit()
                                                                                                                          .toMillis(), TimeUnit.MILLISECONDS);

                if (masterComputation.getTimeLimit() != null)
                    discoveryFuture.get(masterComputation.getTimeLimit().toMillis(), TimeUnit.MILLISECONDS);
                else discoveryFuture.get();

                discoveryComputation.markEnded();
            } catch (InterruptedException | ExecutionException | TimeoutException | CancellationException e) {
                specpp.stop();
                discoveryComputation.markForciblyCancelled();
                discoveryComputation.markEnded();
                postProcessingFuture.cancel(false);
                e.fillInStackTrace();
                System.out.println("exception during discovery on " + specpp + ":\n\t" + e);
            } finally {
                if (discoveryCancellationFuture != null) discoveryCancellationFuture.cancel(true);
            }

            postProcessingComputation.markStarted();

            try {
                workerExecutorService.submit(postProcessingFuture);

                Duration ppLimit = postProcessingComputation.getTimeLimit();
                Duration totalRemaining = masterComputation.calculateRemainingTime();
                Duration duration = StupidUtils.takeMin(ppLimit, totalRemaining);
                if (duration != null) postProcessingFuture.get(duration.toMillis(), TimeUnit.MILLISECONDS);
                else postProcessingFuture.get();

                postProcessingComputation.markEnded();
                specpp.stop();

            } catch (ExecutionException | InterruptedException | TimeoutException | CancellationException e) {
                postProcessingComputation.markForciblyCancelled();
                postProcessingComputation.markEnded();
                specpp.stop();
                masterComputation.markForciblyCancelled();
                e.fillInStackTrace();
                System.out.println("exception during post processing on " + specpp + ":\n\t" + e);
            }

            masterComputation.markEnded();

            return true;
        });
        masterComputation.setComputationFuture(task);

        managerExecutorService.execute(task);
        monitoredFutures.add(task);

        return execution;
    }


}
