package org.processmining.specpp.orchestra;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.componenting.system.link.CompositionComponent;
import org.processmining.specpp.config.parameters.ExecutionParameters;
import org.processmining.specpp.headless.batch.MyThreadPoolExecutor;
import org.processmining.specpp.prom.computations.OngoingComputation;
import org.processmining.specpp.prom.computations.OngoingStagedComputation;
import org.processmining.specpp.traits.Joinable;
import org.processmining.specpp.util.PrintingUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class ExecutionEnvironment implements Joinable, AutoCloseable {

    private final ScheduledThreadPoolExecutor timeoutExecutorService;
    private final MyThreadPoolExecutor managerExecutorService, workerExecutorService, callbackExecutorService;
    private final List<ListenableFuture<?>> monitoredFutures;
    private final List<ListenableFuture<?>> monitoredCallbackFutures;
    private final Map<Consumer<?>, ListenableFuture<?>> callbackFutures;

    private final EnvironmentSettings environmentSettings;
    private MyThreadPoolExecutor miscExecutorService;


    public static class EnvironmentSettings {
        private int workerThreadCount, callbackThreadCount, totalThreadCount;
        private final int MAX_TERMINATION_WAIT = 5;

        public EnvironmentSettings(int workerThreadCount, int callbackThreadCount) {
            this.workerThreadCount = workerThreadCount;
            this.callbackThreadCount = callbackThreadCount;
            totalThreadCount = workerThreadCount + callbackThreadCount;
        }

        public static EnvironmentSettings targetParallelism(int threadCount) {
            return new EnvironmentSettings(Math.max(1, threadCount - 2), 1);
        }

        public static EnvironmentSettings targetParallelism(int workerThreadCount, int callbackThreadCount) {
            assert workerThreadCount >= 1 && callbackThreadCount >= 1;
            return new EnvironmentSettings(workerThreadCount, callbackThreadCount);
        }

    }

    @Override
    public void join() throws InterruptedException {
        managerExecutorService.shutdown();
        for (ListenableFuture<?> f : monitoredFutures) {
            try {
                f.get();
            } catch (ExecutionException e) {
                System.out.println("manager task execution failed");
                e.printStackTrace();
            } catch (CancellationException ignored) {
            }
        }
        workerExecutorService.shutdownNow();
        callbackExecutorService.setNewFixedSize(environmentSettings.totalThreadCount);
        for (ListenableFuture<?> f : monitoredCallbackFutures) {
            try {
                f.get();
            } catch (ExecutionException e) {
                System.out.println("callback execution failed");
                e.printStackTrace();
            } catch (CancellationException ignored) {
            }
        }
        timeoutExecutorService.shutdownNow();
        callbackExecutorService.shutdownNow();
        miscExecutorService.shutdown();
        timeoutExecutorService.awaitTermination(environmentSettings.MAX_TERMINATION_WAIT, TimeUnit.SECONDS);
        workerExecutorService.awaitTermination(environmentSettings.MAX_TERMINATION_WAIT, TimeUnit.SECONDS);
        managerExecutorService.awaitTermination(environmentSettings.MAX_TERMINATION_WAIT, TimeUnit.SECONDS);
        callbackExecutorService.awaitTermination(environmentSettings.MAX_TERMINATION_WAIT, TimeUnit.SECONDS);
        miscExecutorService.awaitTermination(environmentSettings.MAX_TERMINATION_WAIT, TimeUnit.SECONDS);
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

        public boolean wasDiscoveryCancelledGracefully() {
            return discoveryComputation.isGracefullyCancelled();
        }

        public boolean hasTerminatedSuccessfully() {
            return discoveryComputation.hasTerminatedSuccessfully() && postProcessingComputation.hasTerminatedSuccessfully() && masterComputation.hasTerminatedSuccessfully();
        }

        public OngoingComputation getMasterComputation() {
            return masterComputation;
        }

    }

    public static ExecutionEvironmentThread wrap(Consumer<ExecutionEnvironment> user, Runnable finallyClause) {
        return wrap(EnvironmentSettings.targetParallelism(3), user, finallyClause);
    }

    public static ExecutionEvironmentThread wrap(EnvironmentSettings envs, Consumer<ExecutionEnvironment> user, Runnable finallyClause) {
        ExecutionEvironmentThread thread = new ExecutionEvironmentThread(envs, user, finallyClause);
        thread.setPriority(Thread.MAX_PRIORITY);
        return thread;
    }

    public static class ExecutionEvironmentThread extends Thread {
        private final EnvironmentSettings envs;
        private final Consumer<ExecutionEnvironment> user;
        private final Runnable finallyClause;
        private ExecutionEnvironment ee;

        public ExecutionEvironmentThread(EnvironmentSettings envs, Consumer<ExecutionEnvironment> user, Runnable finallyClause) {
            super("Execution Environment");
            this.envs = envs;
            this.user = user;
            this.finallyClause = finallyClause;
            setDaemon(true);
        }

        public ExecutionEnvironment getExecutionEnvironment() {
            return ee;
        }

        @Override
        public void run() {
            try (ExecutionEnvironment ee = new ExecutionEnvironment(envs)) {
                this.ee = ee;
                user.accept(ee);
            } catch (InterruptedException e) {
                System.out.println("Execution Environment was interrupted.");
                e.printStackTrace();
            } finally {
                finallyClause.run();
            }
        }
    }

    public ExecutionEnvironment() {
        this(3);
    }

    public ExecutionEnvironment(int threadCount) {
        this(EnvironmentSettings.targetParallelism(threadCount));
    }

    public ExecutionEnvironment(EnvironmentSettings envs) {
        environmentSettings = envs;
        managerExecutorService = createFixedThreadPoolExecutor(envs.workerThreadCount, "manager-pool-thread-%d");

        MyThreadPoolExecutor workerExecutor = createFixedThreadPoolExecutor(envs.workerThreadCount, "worker-pool-thread-%d");
        workerExecutor.prestartAllCoreThreads();
        workerExecutorService = workerExecutor;

        MyThreadPoolExecutor callbackExecutor = createFixedThreadPoolExecutor(envs.callbackThreadCount, "callback-pool-thread-%d");
        callbackExecutor.prestartAllCoreThreads();
        callbackExecutorService = callbackExecutor;

        miscExecutorService = createFixedThreadPoolExecutor(1, "misc-pool-thread-%d");
        miscExecutorService.prestartCoreThread();

        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, new ThreadFactoryBuilder().setNameFormat("timekeeper-pool-thread-%d")
                                                                                                                               .build());
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        scheduledThreadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        scheduledThreadPoolExecutor.prestartCoreThread();
        scheduledThreadPoolExecutor.setMaximumPoolSize(1);
        timeoutExecutorService = scheduledThreadPoolExecutor;

        monitoredCallbackFutures = new ArrayList<>();
        monitoredFutures = new ArrayList<>();
        callbackFutures = new HashMap<>();
    }

    public String threadPoolInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(managerExecutorService).append("\n");
        sb.append(workerExecutorService).append("\n");
        sb.append(callbackExecutorService).append("\n");
        sb.append("timekeeper-pool{")
          .append(PrintingUtils.stringifyThreadPoolExecutor(timeoutExecutorService))
          .append("}")
          .append("\n");
        sb.append(miscExecutorService);
        return sb.toString();
    }


    private static ThreadPoolExecutor createCachedThreadPoolExecutor(int size, String threadNamingPattern) {
        return new ThreadPoolExecutor(size, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(true), new ThreadFactoryBuilder().setNameFormat(threadNamingPattern)
                                                                                                                                              .build());
    }

    private static MyThreadPoolExecutor createSynchronousFixedThreadPoolExecutor(int size, String threadNamingPattern) {
        return new MyThreadPoolExecutor(threadNamingPattern.substring(0, threadNamingPattern.indexOf("-thread")), size, new SynchronousQueue<>(), new ThreadFactoryBuilder().setNameFormat(threadNamingPattern)
                                                                                                                                                                            .build());
    }

    private static MyThreadPoolExecutor createFixedThreadPoolExecutor(int size, String threadNamingPattern) {
        return new MyThreadPoolExecutor(threadNamingPattern.substring(0, threadNamingPattern.indexOf("-thread")), size, new LinkedBlockingDeque<>(), new ThreadFactoryBuilder().setNameFormat(threadNamingPattern)
                                                                                                                                                                               .build());
    }

    public static <C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> SPECppExecution<C, I, R, F> oneshotExecution(SPECpp<C, I, R, F> specpp, ExecutionParameters executionParameters) {
        SPECppExecution<C, I, R, F> execution;
        try (ExecutionEnvironment ee = new ExecutionEnvironment()) {
            execution = ee.execute(specpp, executionParameters);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return execution;
    }

    public <C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> ListenableFuture<?> addTimeLimitedCompletionCallback(SPECppExecution<C, I, R, F> execution, Consumer<SPECppExecution<C, I, R, F>> callback, Duration timeLimit) {
        Consumer<SPECppExecution<C, I, R, F>> wrap = ex -> {
            ScheduledFuture<?> schedule = timeoutExecutorService.schedule(() -> {
                if (callbackFutures.containsKey(callback)) callbackFutures.get(callback).cancel(true);
            }, timeLimit.toMillis(), TimeUnit.MILLISECONDS);
            callback.accept(ex);
            schedule.cancel(false);
        };
        ListenableFuture<?> future = addCompletionCallback(execution, wrap);
        callbackFutures.put(callback, future);
        return future;
    }

    public <C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> ListenableFuture<?> addCompletionCallback(SPECppExecution<C, I, R, F> execution, Consumer<SPECppExecution<C, I, R, F>> callback) {
        ListenableFutureTask<?> futureTask = ListenableFutureTask.create(() -> callback.accept(execution), null);
        execution.getMasterComputation().getComputationFuture().addListener(futureTask, callbackExecutorService);
        monitoredCallbackFutures.add(futureTask);
        return futureTask;
    }

    public <C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> void addLightweightCompletionCallback(SPECppExecution<C, I, R, F> execution, Consumer<SPECppExecution<C, I, R, F>> callback) {
        execution.getMasterComputation()
                 .getComputationFuture()
                 .addListener(() -> callback.accept(execution), miscExecutorService);
    }


    public <C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> SPECppExecution<C, I, R, F> execute(SPECpp<C, I, R, F> specpp, ExecutionParameters executionParameters) {

        OngoingComputation masterComputation = new OngoingComputation();
        OngoingComputation discoveryComputation = new OngoingComputation();
        OngoingStagedComputation postProcessingComputation = new OngoingStagedComputation(specpp.getPostProcessor()
                                                                                                .getPipelineLength());
        List<ScheduledFuture<?>> timeKeepingFutures = new LinkedList<>();
        SPECppExecution<C, I, R, F> execution = new SPECppExecution<>(specpp, masterComputation, discoveryComputation, postProcessingComputation, timeKeepingFutures);


        Runnable discoveryCanceller = () -> {
            specpp.cancelPECCyclingGracefully();
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

        ListenableFutureTask<R> discoveryFuture = ListenableFutureTask.create(specpp::executeDiscoveryInterruptibly);
        discoveryComputation.setComputationFuture(discoveryFuture);
        ListenableFutureTask<F> postProcessingFuture = ListenableFutureTask.create(specpp::executePostProcessingInterruptibly);
        postProcessingComputation.setComputationFuture(postProcessingFuture);

        ListenableFutureTask<Boolean> task = ListenableFutureTask.create(() -> {
            specpp.start();

            ScheduledFuture<?> discoveryCancellationFuture = null, totalCancellationFuture = null;
            try {
                masterComputation.markStarted();
                discoveryComputation.markStarted();
                workerExecutorService.execute(discoveryFuture);

                if (discoveryComputation.getTimeLimit() != null)
                    discoveryCancellationFuture = timeoutExecutorService.schedule(discoveryCanceller, discoveryComputation.getTimeLimit()
                                                                                                                          .toMillis(), TimeUnit.MILLISECONDS);

                if (masterComputation.getTimeLimit() != null)
                    totalCancellationFuture = timeoutExecutorService.schedule(totalCanceller, masterComputation.getTimeLimit()
                                                                                                               .toMillis(), TimeUnit.MILLISECONDS);
                discoveryFuture.get();

                discoveryComputation.markEnded();
            } catch (InterruptedException | ExecutionException | CancellationException e) {
                if (totalCancellationFuture != null) totalCancellationFuture.cancel(true);
                specpp.stop();
                discoveryComputation.markForciblyCancelled();
                discoveryComputation.markEnded();
                postProcessingFuture.cancel(false);
                e.fillInStackTrace();
                System.out.println("exception during discovery in " + specpp.hashCode() + " on " + Thread.currentThread()
                                                                                                         .getName() + ":\n\t" + e);
            } finally {
                if (discoveryCancellationFuture != null) discoveryCancellationFuture.cancel(true);
            }

            ScheduledFuture<?> postProcessingCancellationFuture = null;
            try {
                postProcessingComputation.markStarted();
                if (!postProcessingFuture.isCancelled()) {
                    workerExecutorService.execute(postProcessingFuture);
                    if (postProcessingComputation.getTimeLimit() != null)
                        postProcessingCancellationFuture = timeoutExecutorService.schedule(postProcessingCanceller, postProcessingComputation.getTimeLimit()
                                                                                                                                             .toMillis(), TimeUnit.MILLISECONDS);
                }

                postProcessingFuture.get();

                postProcessingComputation.markEnded();
                specpp.stop();

            } catch (ExecutionException | InterruptedException | CancellationException e) {
                postProcessingComputation.markForciblyCancelled();
                postProcessingComputation.markEnded();
                specpp.stop();
                masterComputation.markForciblyCancelled();
                workerExecutorService.purge();
                e.fillInStackTrace();
                System.out.println("exception during post processing in " + specpp.hashCode() + " on " + Thread.currentThread()
                                                                                                               .getName() + ":\n\t" + e);
            } finally {
                if (postProcessingCancellationFuture != null) postProcessingCancellationFuture.cancel(true);
                if (totalCancellationFuture != null) totalCancellationFuture.cancel(true);
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
