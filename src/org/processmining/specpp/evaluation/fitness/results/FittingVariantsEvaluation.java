package org.processmining.specpp.evaluation.fitness.results;

import org.processmining.specpp.base.CandidateEvaluation;
import org.processmining.specpp.datastructures.encoding.BitMask;

public interface FittingVariantsEvaluation extends CandidateEvaluation {

    BitMask getFittingVariants();

}
