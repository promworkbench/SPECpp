package org.processmining.specpp.evaluation.markings;

import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.specpp.componenting.traits.IsGlobalProvider;
import org.processmining.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.specpp.config.parameters.ReplayComputationParameters;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.vectorization.VariantMarkingHistories;

public class LogHistoryMaker extends AbstractGlobalComponentSystemUser implements ProvidesEvaluators, IsGlobalProvider {


    public static class Builder extends ComponentSystemAwareBuilder<LogHistoryMaker> {

        private final DelegatingDataSource<ReplayComputationParameters> replayComputationParametersSource = new DelegatingDataSource<>();

        public Builder() {
            globalComponentSystem().require(ParameterRequirements.REPLAY_COMPUTATION, replayComputationParametersSource);
        }

        @Override
        protected LogHistoryMaker buildIfFullySatisfied() {
            return new LogHistoryMaker();
        }

    }

    private final DelegatingDataSource<MultiEncodedLog> encodedLogSource = new DelegatingDataSource<>();
    private final DelegatingDataSource<BitMask> consideredVariantsSource = new DelegatingDataSource<>();

    private BitMask consideredVariants;

    public LogHistoryMaker() {
        globalComponentSystem().require(DataRequirements.ENC_LOG, encodedLogSource)
                               .require(DataRequirements.CONSIDERED_VARIANTS, consideredVariantsSource)
                               .provide(EvaluationRequirements.PLACE_MARKING_HISTORY.fulfilWith(this::computeVariantMarkingHistories));
    }

    protected void updateConsideredVariants() {
        setConsideredVariants(consideredVariantsSource.getData());
    }

    public VariantMarkingHistories computeVariantMarkingHistories(Place input) {
        updateConsideredVariants();
        return consideredVariantsSource.isSet() ? QuickReplay.makeHistoryOn(consideredVariants, encodedLogSource.getData(), input) : QuickReplay.makeHistory(encodedLogSource.getData(), input);
    }

    public BitMask getConsideredVariants() {
        return consideredVariants;
    }

    public void setConsideredVariants(BitMask consideredVariants) {
        this.consideredVariants = consideredVariants;
    }

    @Override
    public String toString() {
        return "LogHistoryMaker()";
    }
}
