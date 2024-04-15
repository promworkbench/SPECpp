package org.processmining.specpp.composition.composers;

import org.processmining.specpp.base.Result;
import org.processmining.specpp.componenting.system.link.ComposerComponent;
import org.processmining.specpp.componenting.system.link.CompositionComponent;
import org.processmining.specpp.config.parameters.TauFitnessThresholds;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.tree.constraints.ClinicallyOverfedPlace;
import org.processmining.specpp.datastructures.tree.constraints.ClinicallyUnderfedPlace;
import org.processmining.specpp.evaluation.fitness.results.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.results.DetailedFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.FitnessThresholder;

public class AbsoluteFitnessFilter<I extends CompositionComponent<Place>, R extends Result> extends CachingFitnessFilter<I, R> {
    public AbsoluteFitnessFilter(ComposerComponent<Place, I, R> childComposer) {
        super(childComposer);
    }

    @Override
    protected void initSelf() {

    }


    @Override
    public void accept(Place place) {
        DetailedFitnessEvaluation eval = fitnessEvaluator.eval(place);
        BasicFitnessEvaluation fitness = eval.getFractionalEvaluation();
        TauFitnessThresholds thresholds = fitnessThresholds.getData();
        if (FitnessThresholder.isUnderfed(fitness, thresholds)) {
            gotFiltered(place, new ClinicallyUnderfedPlace(place));
        } else if (FitnessThresholder.isOverfed(fitness, thresholds)) {
            gotFiltered(place, new ClinicallyOverfedPlace(place));
        } else if (FitnessThresholder.isTauFitting(fitness, thresholds)) {
            fitnessCache.put(place, eval);
            forward(place);
        }
    }
}
