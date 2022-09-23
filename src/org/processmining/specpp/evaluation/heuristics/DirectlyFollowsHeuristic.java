package org.processmining.specpp.evaluation.heuristics;

import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.specpp.datastructures.encoding.IntEncoding;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.Variant;
import org.processmining.specpp.datastructures.log.impls.IndexedVariant;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.specpp.datastructures.vectorization.IntVector;

import java.util.Comparator;

public class DirectlyFollowsHeuristic implements HeuristicStrategy<Place, CandidateScore> {

    private final int[][] dfCounts;

    public static class Builder extends ComponentSystemAwareBuilder<DirectlyFollowsHeuristic.Provider> {

        private final DelegatingDataSource<Log> rawLog = new DelegatingDataSource<>();
        private final DelegatingDataSource<IntEncodings<Activity>> encAct = new DelegatingDataSource<>();

        public Builder() {
            globalComponentSystem().require(DataRequirements.RAW_LOG, rawLog).require(DataRequirements.ENC_ACT, encAct);
        }

        @Override
        protected DirectlyFollowsHeuristic.Provider buildIfFullySatisfied() {
            Log log = rawLog.getData();
            IntEncodings<Activity> activityIntEncodings = encAct.getData();
            IntEncoding<Activity> presetEncoding = activityIntEncodings.getPresetEncoding();
            IntEncoding<Activity> postsetEncoding = activityIntEncodings.getPostsetEncoding();
            int preSize = presetEncoding.size();
            int postSize = postsetEncoding.size();

            int[][] counts = new int[preSize][postSize];
            for (int i = 0; i < counts.length; i++) {
                counts[i] = new int[postSize];
            }

            IntVector frequencies = log.getVariantFrequencies();
            for (IndexedVariant indexedVariant : log) {
                Variant variant = indexedVariant.getVariant();
                int f = frequencies.get(indexedVariant.getIndex());
                Activity last = null;
                for (Activity activity : variant) {
                    if (last != null) {
                        if (presetEncoding.isInDomain(last) && postsetEncoding.isInDomain(activity)) {
                            Integer i = presetEncoding.encode(last);
                            Integer j = postsetEncoding.encode(activity);
                            counts[i][j] += f;
                        }
                    }
                    last = activity;
                }
            }

            return new DirectlyFollowsHeuristic.Provider(counts);
        }
    }

    public static class Provider extends AbstractGlobalComponentSystemUser implements ProvidesEvaluators {

        public Provider(int[][] counts) {
            HeuristicStrategy<Place, CandidateScore> delegate = new DirectlyFollowsHeuristic(counts);
            globalComponentSystem().provide(EvaluationRequirements.POSTPONED_CANDIDATES_HEURISTIC.fulfilWith(delegate))
                                   .provide(DataRequirements.dataSource("heuristics.place.df", DirectlyFollowsHeuristic.class, () -> new DirectlyFollowsHeuristic(counts)));
        }
    }

    public DirectlyFollowsHeuristic(int[][] dfCounts) {
        this.dfCounts = dfCounts;
    }

    @Override
    public CandidateScore computeHeuristic(Place input) {
        int sum = input.preset()
                       .streamIndices()
                       .flatMap(i -> input.postset().streamIndices().map(j -> dfCounts[i][j]))
                       .sum();

        return new CandidateScore((double) sum / input.size());
    }

    @Override
    public Comparator<CandidateScore> heuristicValuesComparator() {
        return Comparator.reverseOrder();
    }
}
