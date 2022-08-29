package org.processmining.specpp.evaluation.markings;

import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.traits.IsGlobalProvider;
import org.processmining.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.vectorization.VariantMarkingHistories;

public class LogHistoryMaker extends AbstractGlobalComponentSystemUser implements ProvidesEvaluators, IsGlobalProvider {

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
