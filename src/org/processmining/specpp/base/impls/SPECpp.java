package org.processmining.specpp.base.impls;

import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.base.Composer;
import org.processmining.specpp.base.Proposer;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.StaticDataSource;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.componenting.system.LocalComponentRepository;
import org.processmining.specpp.componenting.system.link.AbstractBaseClass;
import org.processmining.specpp.componenting.system.link.ComposerComponent;
import org.processmining.specpp.componenting.system.link.CompositionComponent;
import org.processmining.specpp.componenting.system.link.ProposerComponent;
import org.processmining.specpp.config.SPECppConfigBundle;
import org.processmining.specpp.config.components.Configuration;
import org.processmining.specpp.orchestra.ExternalInitializer;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.supervision.Supervisor;
import org.processmining.specpp.supervision.supervisors.DebuggingSupervisor;
import org.processmining.specpp.traits.Joinable;
import org.processmining.specpp.traits.StartStoppable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class SPECpp<C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> extends AbstractBaseClass implements StartStoppable {


    private final GlobalComponentRepository cr;

    private final List<Supervisor> supervisors;
    private final ProposerComponent<C> proposer;
    private final ComposerComponent<C, I, R> composer;
    private final PostProcessingPipeline<R, F> postProcessor;

    private final Configuration configuration;
    private int cycleCount;
    private C lastCandidate;
    private boolean pecCyclingCancelledPrematurely;

    private final AtomicBoolean active;
    private R result;

    private F finalResult;


    public SPECpp(GlobalComponentRepository cr, List<Supervisor> supervisors, ProposerComponent<C> proposer, ComposerComponent<C, I, R> composer, PostProcessingPipeline<R, F> postProcessor) {
        this.cr = cr;
        this.supervisors = supervisors;
        this.proposer = proposer;
        this.composer = composer;
        this.postProcessor = postProcessor;
        cycleCount = 0;
        active = new AtomicBoolean(false);
        lastCandidate = null;
        pecCyclingCancelledPrematurely = false;
        configuration = new Configuration(cr);

        globalComponentSystem().provide(DataRequirements.dataSource("cancel_gracefully", Runnable.class, StaticDataSource.of(this::cancelPECCyclingGracefully)));
        localComponentSystem().provide(DataRequirements.dataSource("update_local_component_system", Runnable.class, StaticDataSource.of(this::updateLocalComponentSystem)));
        registerSubComponent(proposer);
        registerSubComponent(composer);
    }

    /**
     * Connects the transitive local component systems of composer subcomponents and proposer subcomponents.
     * That is, first {@see connectLocalComponentSystem} collects and requirements from the leafs to the respective proposer/composer root and continuously fulfills them on the way.
     * At the end of this step, all requirements and provisions are collected in {@code proposerLcr} and {@code composerLcr}.
     * These are then fulfilled by each other and finally fulfilled by this component.
     * The tree structure will typically look something like this, if all implementations in this chain properly register their subcomponents:
     * place proposer -> enumerating tree -> child generation logic
     * -> tree expansion strategy
     * rec. composer 1 -> .. -> rec. composer n -> terminal composer -> rec. composition 1 -> .. -> rec. composition n -> terminal composition
     */
    public static <C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> SPECpp<C, I, R, F> build(SPECppConfigBundle configBundle, InputDataBundle dataBundle) {
        GlobalComponentRepository cr = new GlobalComponentRepository();

        configBundle.instantiate(cr, dataBundle);

        Configuration configuration = new Configuration(cr);
        ExternalInitializer externalInitializer = configuration.createFrom(ExternalInitializer::new);
        SPECpp<C, I, R, F> specpp = configuration.createFrom(new SPECppBuilder<>(), cr);

        specpp.init();
        externalInitializer.init();

        return specpp;
    }

    public GlobalComponentRepository getGlobalComponentRepository() {
        return cr;
    }

    public R getInitialResult() {
        return result;
    }

    public F getPostProcessedResult() {
        return finalResult;
    }

    private void updateLocalComponentSystem() {
        LocalComponentRepository proposerLcr = new LocalComponentRepository();
        LocalComponentRepository composerLcr = new LocalComponentRepository();
        proposer.connectLocalComponentSystem(proposerLcr);
        composer.connectLocalComponentSystem(composerLcr);
        proposerLcr.fulfil(composerLcr);
        composerLcr.fulfil(proposerLcr);
        localComponentSystem().fulfil(proposerLcr);
        localComponentSystem().fulfil(composerLcr);
    }

    @Override
    protected void preSubComponentInit() {
        for (Supervisor supervisor : supervisors) {
            configuration.checkout(supervisor);
            supervisor.init();
            configuration.absorbProvisions(supervisor);
        }
        updateLocalComponentSystem();
    }

    @Override
    public void initSelf() {

    }

    @Override
    public void start() {
        if (active.compareAndSet(false, true)) {
            supervisors.forEach(Supervisor::start);
        }
    }


    public boolean executePECCycle() {
        if (composer.isFinished()) return true;
        C c = proposer.proposeCandidate();
        if (pecCyclingCancelledPrematurely || c == null) {
            composer.candidatesAreExhausted();
            return true;
        }
        composer.accept(c);
        lastCandidate = c;
        return false;
    }

    public void cancelPECCyclingGracefully() {
        pecCyclingCancelledPrematurely = true;
    }

    protected void executeAllPECCycles() {
        while (!executePECCycle()) ++cycleCount;
    }

    protected void executeAllPECCyclesInterruptibly() throws InterruptedException {
        while (!executePECCycle()) {
            if (cycleCount % 1000 == 0 && Thread.interrupted()) throw new InterruptedException();
            ++cycleCount;
        }
    }

    protected void generateResult() {
        result = composer.generateResult();
    }

    public final R executeDiscoveryInterruptibly() throws InterruptedException {
        executeAllPECCyclesInterruptibly();
        generateResult();
        return result;
    }

    public final R executeDiscovery() {
        executeAllPECCycles();
        generateResult();
        return result;
    }

    public final F executePostProcessing() {
        try {
            finalResult = postProcessor.postProcess(result);
        } catch (Exception e) {
            e.fillInStackTrace();
            String message = "Post Processing on " + Thread.currentThread() + " failed with:\n" + e;
            DebuggingSupervisor.debug("Post Processing", message);
            System.out.println(message);
        }
        return finalResult;
    }

    public final F executePostProcessingInterruptibly() throws InterruptedException {
        try {
            finalResult = postProcessor.postProcessInterruptibly(result);
        } catch (Exception e) {
            if (e instanceof InterruptedException) throw e;
            e.fillInStackTrace();
            DebuggingSupervisor.debug("Post Processing", "Post Processing on " + Thread.currentThread() + " failed with:\n" + e);
        }
        return finalResult;
    }

    public final F executePostProcessing(Consumer<Result> intermediateResultCallback) {
        try {
            finalResult = postProcessor.postProcess(result, intermediateResultCallback);
        } catch (Exception e) {
            e.fillInStackTrace();
            DebuggingSupervisor.debug("Post Processing", "Post Processing on " + Thread.currentThread() + " failed with:\n" + e);
        }
        return finalResult;
    }

    public F executeAll() {
        executeDiscovery();
        return executePostProcessing();
    }

    public CompletableFuture<F> future(Executor executor) {
        return CompletableFuture.supplyAsync(this::executeAll, executor);
    }

    public Proposer<C> getProposer() {
        return proposer;
    }

    public Composer<C, I, R> getComposer() {
        return composer;
    }

    public int currentCycleCount() {
        return cycleCount;
    }

    public C lastCandidate() {
        return lastCandidate;
    }

    @Override
    public void stop() {
        if (active.compareAndSet(true, false)) {
            supervisors.forEach(Supervisor::stop);
            for (Supervisor supervisor : supervisors) {
                if (supervisor instanceof Joinable) {
                    try {
                        ((Joinable) supervisor).join();
                    } catch (InterruptedException e) {
                        // TODO swallowing interrupted exceptions
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public boolean isActive() {
        return active.get();
    }

    public Collection<Supervisor> getSupervisors() {
        return supervisors;
    }


    public PostProcessingPipeline<R, F> getPostProcessor() {
        return postProcessor;
    }
}
