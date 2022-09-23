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
import org.processmining.specpp.config.Configuration;
import org.processmining.specpp.supervision.Supervisor;
import org.processmining.specpp.supervision.supervisors.DebuggingSupervisor;
import org.processmining.specpp.traits.Joinable;
import org.processmining.specpp.traits.StartStoppable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
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
    private boolean active, computationCancelled;
    private R result;

    private F finalResult;


    public SPECpp(GlobalComponentRepository cr, List<Supervisor> supervisors, ProposerComponent<C> proposer, ComposerComponent<C, I, R> composer, PostProcessingPipeline<R, F> postProcessor) {
        this.cr = cr;
        this.supervisors = supervisors;
        this.proposer = proposer;
        this.composer = composer;
        this.postProcessor = postProcessor;
        cycleCount = 0;
        lastCandidate = null;
        computationCancelled = false;
        configuration = new Configuration(cr);

        globalComponentSystem().provide(DataRequirements.dataSource("cancel_gracefully", Runnable.class, StaticDataSource.of(this::cancelGracefully)));
        localComponentSystem().provide(DataRequirements.dataSource("update_local_component_system", Runnable.class, StaticDataSource.of(this::updateLocalComponentSystem)));
        registerSubComponent(proposer);
        registerSubComponent(composer);
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
        active = true;
        supervisors.forEach(Supervisor::start);
    }


    public boolean executePECCycle() {
        if (composer.isFinished()) return true;
        C c = proposer.proposeCandidate();
        if (computationCancelled || c == null) {
            composer.candidatesAreExhausted();
            return true;
        }
        composer.accept(c);
        lastCandidate = c;
        return false;
    }

    public void cancelGracefully() {
        computationCancelled = true;
    }

    protected void executeAllPECCycles() {
        while (!executePECCycle()) ++cycleCount;
    }

    protected void generateResult() {
        result = composer.generateResult();
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
            DebuggingSupervisor.debug("Post Processing", "Post Processing failed with:");
            e.printStackTrace();
        }
        return finalResult;
    }

    public final F executePostProcessing(Consumer<Result> intermediateResultCallback) {
        try {
            finalResult = postProcessor.postProcess(result, intermediateResultCallback);
        } catch (Exception e) {
            DebuggingSupervisor.debug("Post Processing", "Post Processing failed with:");
            e.printStackTrace();
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

    public int currentStepCount() {
        return cycleCount;
    }

    public C lastCandidate() {
        return lastCandidate;
    }

    @Override
    public void stop() {
        supervisors.forEach(Supervisor::stop);
        for (Supervisor supervisor : supervisors) {
            if (supervisor instanceof Joinable) {
                try {
                    ((Joinable) supervisor).join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public Collection<Supervisor> getSupervisors() {
        return supervisors;
    }


    public PostProcessingPipeline<R, F> getPostProcessor() {
        return postProcessor;
    }
}
