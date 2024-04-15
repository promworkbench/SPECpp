package org.processmining.specpp.composition.composers;

import org.processmining.specpp.base.Result;
import org.processmining.specpp.componenting.system.link.ComposerComponent;
import org.processmining.specpp.componenting.system.link.CompositionComponent;
import org.processmining.specpp.config.parameters.TauFitnessThresholds;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.tree.constraints.ClinicallyOverfedPlace;
import org.processmining.specpp.datastructures.tree.constraints.ClinicallyUnderfedPlace;
import org.processmining.specpp.evaluation.fitness.FitnessThresholder;
import org.processmining.specpp.evaluation.fitness.base.ReplayOutcome;
import org.processmining.specpp.evaluation.fitness.results.ComprehensiveFitnessEvaluation;

public class AggregatedFitnessFilter<I extends CompositionComponent<Place>, R extends Result> extends AbstractActivityActivationAwareFitnessFilter<I, R> {
    public AggregatedFitnessFilter(ComposerComponent<Place, I, R> childComposer) {
        super(childComposer);
    }

    @Override
    public void accept(Place place) {
        ComprehensiveFitnessEvaluation eval = fitnessEvaluator.eval(place);
        TauFitnessThresholds thresholds = fitnessThresholds.getData();

        // max > x corresponds to exists s.t. > x
        double aggUnderfed = conditionalOutcomesStream(place, eval.getReplayOutcomes(), ReplayOutcome.UNDERFED).max().orElse(0.0);
        if (FitnessThresholder.isUnderfed(aggUnderfed, thresholds)) {
            gotFiltered(place, new ClinicallyUnderfedPlace(place));
            return;
        }
        double aggOverfed = conditionalOutcomesStream(place, eval.getReplayOutcomes(), ReplayOutcome.OVERFED).max().orElse(0.0);
        if (FitnessThresholder.isOverfed(aggOverfed, thresholds)) {
            gotFiltered(place, new ClinicallyOverfedPlace(place));
            return;
        }
        double aggFitness = conditionalOutcomesStream(place, eval.getReplayOutcomes(), ReplayOutcome.FITTING).min().orElse(0.0);
        if (FitnessThresholder.isTauFitting(aggFitness, thresholds)) {
            fitnessCache.put(place, eval);
            forward(place);
        }
    }
}
