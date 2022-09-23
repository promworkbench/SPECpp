package org.processmining.specpp.evaluation.heuristics;

import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.specpp.datastructures.encoding.IntEncoding;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.Variant;
import org.processmining.specpp.datastructures.log.impls.IndexedVariant;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.specpp.datastructures.tree.heuristic.SubtreeMonotonicity;
import org.processmining.specpp.datastructures.tree.heuristic.TreeNodeScore;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.vectorization.IntVector;
import org.processmining.specpp.traits.ZeroOneBounded;

import java.util.Comparator;
import java.util.PrimitiveIterator;

public class EventuallyFollowsTreeHeuristic implements HeuristicStrategy<PlaceNode, TreeNodeScore>, ZeroOneBounded, SubtreeMonotonicity.Decreasing {

    public EventuallyFollowsTreeHeuristic(double[][] eventuallyFollows) {
        this.eventuallyFollows = eventuallyFollows;
    }

    public static class Builder extends ComponentSystemAwareBuilder<EventuallyFollowsTreeHeuristic> {


        private final DelegatingDataSource<Log> rawLog = new DelegatingDataSource<>();
        private final DelegatingDataSource<IntEncodings<Activity>> encAct = new DelegatingDataSource<>();

        public Builder() {
            globalComponentSystem().require(DataRequirements.RAW_LOG, rawLog).require(DataRequirements.ENC_ACT, encAct);
        }

        @Override
        protected EventuallyFollowsTreeHeuristic buildIfFullySatisfied() {
            Log log = rawLog.getData();
            IntEncodings<Activity> activityIntEncodings = encAct.getData();
            IntEncoding<Activity> presetEncoding = activityIntEncodings.getPresetEncoding();
            IntEncoding<Activity> postsetEncoding = activityIntEncodings.getPostsetEncoding();
            IntVector frequencies = log.getVariantFrequencies();
            int preSize = presetEncoding.size();
            int postSize = postsetEncoding.size();

            double[][] ef = new double[preSize][postSize];
            double[][] coocc = new double[preSize][postSize];
            for (int i = 0; i < ef.length; i++) {
                ef[i] = new double[postSize];
                coocc[i] = new double[postSize];
            }

            for (IndexedVariant indexedVariant : log) {
                Variant variant = indexedVariant.getVariant();
                double f = frequencies.get(indexedVariant.getIndex());
                for (int i = 0; i < variant.getLength(); i++) {
                    Activity a = variant.getAt(i);
                    if (presetEncoding.isInDomain(a)) {
                        Integer m = presetEncoding.encode(a);
                        for (int j = i + 1; j < variant.getLength(); j++) {
                            Activity b = variant.getAt(j);
                            if (postsetEncoding.isInDomain(b)) {
                                Integer n = postsetEncoding.encode(b);
                                ef[m][n] += f;
                                coocc[m][n] += f;
                            }
                        }
                    }
                    if (postsetEncoding.isInDomain(a)) {
                        Integer m = postsetEncoding.encode(a);
                        for (int j = i + 1; j < variant.getLength(); j++) {
                            Activity b = variant.getAt(j);
                            if (presetEncoding.isInDomain(b)) {
                                Integer n = presetEncoding.encode(b);
                                coocc[m][n] += f;
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < ef.length; i++) {
                for (int j = 0; j < ef[i].length; j++) {
                    ef[i][j] /= Math.max(1, coocc[i][j]);
                }
            }

            return new EventuallyFollowsTreeHeuristic(ef);
        }
    }

    protected double[][] eventuallyFollows;

    @Override
    public TreeNodeScore computeHeuristic(PlaceNode node) {
        Place p = node.getPlace();
        if (p.isHalfEmpty()) return new TreeNodeScore(1);

        PrimitiveIterator.OfInt preIt = p.preset().getBitMask().iterator();
        double min = Integer.MAX_VALUE;
        while (preIt.hasNext()) {
            int i = preIt.nextInt();
            PrimitiveIterator.OfInt postIt = p.postset().getBitMask().iterator();
            while (postIt.hasNext()) {
                int j = postIt.nextInt();
                double v = eventuallyFollows[i][j];
                if (v < min) min = v;
            }
        }
        assert 0 <= min;
        assert min < Integer.MAX_VALUE;
        return new TreeNodeScore(min);
    }

    @Override
    public Comparator<TreeNodeScore> heuristicValuesComparator() {
        return Comparator.reverseOrder();
    }

}
