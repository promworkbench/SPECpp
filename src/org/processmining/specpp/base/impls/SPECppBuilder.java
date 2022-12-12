package org.processmining.specpp.base.impls;

import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.componenting.system.link.ComposerComponent;
import org.processmining.specpp.componenting.system.link.CompositionComponent;
import org.processmining.specpp.componenting.system.link.ProposerComponent;
import org.processmining.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.specpp.config.components.InitializingBuilder;
import org.processmining.specpp.config.components.PostProcessingConfiguration;
import org.processmining.specpp.config.components.ProposerComposerConfiguration;
import org.processmining.specpp.config.components.SupervisionConfiguration;
import org.processmining.specpp.config.parameters.SupervisionParameters;
import org.processmining.specpp.supervision.Supervisor;
import org.processmining.specpp.supervision.instrumentators.InstrumentedSPECpp;

import java.util.List;

public class SPECppBuilder<C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> extends AbstractGlobalComponentSystemUser implements InitializingBuilder<SPECpp<C, I, R, F>, GlobalComponentRepository> {

    private final DelegatingDataSource<ProposerComposerConfiguration<C, I, R>> pcConfigDelegator = new DelegatingDataSource<>();
    private final DelegatingDataSource<PostProcessingConfiguration<R, F>> ppConfigDelegator = new DelegatingDataSource<>();
    private final DelegatingDataSource<SupervisionConfiguration> svConfigDelegator = new DelegatingDataSource<>();
    private final DelegatingDataSource<EvaluatorConfiguration> evConfigDelegator = new DelegatingDataSource<>();
    private final DelegatingDataSource<SupervisionParameters> svParametersDelegator = new DelegatingDataSource<>();


    public SPECppBuilder() {
        globalComponentSystem().require(DataRequirements.proposerComposerConfiguration(), pcConfigDelegator)
                               .require(DataRequirements.postprocessingConfiguration(), ppConfigDelegator)
                               .require(DataRequirements.EVALUATOR_CONFIG, evConfigDelegator)
                               .require(DataRequirements.SUPERVISOR_CONFIG, svConfigDelegator)
                               .require(ParameterRequirements.SUPERVISION_PARAMETERS, svParametersDelegator);
    }

    @Override
    public SPECpp<C, I, R, F> build(GlobalComponentRepository gcr) {
        SupervisionConfiguration svConfig = svConfigDelegator.getData();
        List<Supervisor> supervisorList = svConfig.createSupervisors();
        for (Supervisor supervisor : supervisorList) {
            gcr.consumeEntirely(supervisor.globalComponentSystem());
        }
        ProposerComposerConfiguration<C, I, R> pcConfig = pcConfigDelegator.getData();
        PostProcessingConfiguration<R, F> ppConfig = ppConfigDelegator.getData();
        EvaluatorConfiguration evConfig = evConfigDelegator.getData();
        List<ProvidesEvaluators> evaluatorsList = evConfig.createPossiblyInstrumentedEvaluators();
        ProposerComponent<C> proposer = pcConfig.createPossiblyInstrumentedProposer();
        ComposerComponent<C, I, R> composer = pcConfig.createPossiblyInstrumentedComposer();
        PostProcessingPipeline<R, F> processor = ppConfig.createPostProcessorPipeline();
        evConfig.reCheckoutEvaluators(evaluatorsList);
        SupervisionParameters svParams = svParametersDelegator.getData();
        if (svParams.shouldClassBeInstrumented(SPECpp.class))
            return new InstrumentedSPECpp<>(gcr, supervisorList, proposer, composer, processor);
        else return new SPECpp<>(gcr, supervisorList, proposer, composer, processor);
    }
}
