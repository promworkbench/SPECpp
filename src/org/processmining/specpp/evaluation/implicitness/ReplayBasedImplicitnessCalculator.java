package org.processmining.specpp.evaluation.implicitness;

import org.processmining.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.NonMutatingSetOperations;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.Pair;
import org.processmining.specpp.datastructures.vectorization.VariantMarkingHistories;

import java.util.Map;

public class ReplayBasedImplicitnessCalculator {


    private static Place computeP3(Place p1, Place p2) {
        Pair<BitEncodedSet<Transition>> preset_diffs = NonMutatingSetOperations.dualSetminus(p1.preset(), p2.preset());
        Pair<BitEncodedSet<Transition>> postset_diffs = NonMutatingSetOperations.dualSetminus(p1.postset(), p2.postset());

        BitEncodedSet<Transition> iip = preset_diffs.first();
        BitEncodedSet<Transition> opo = postset_diffs.second();
        BitEncodedSet<Transition> oop = postset_diffs.first();
        BitEncodedSet<Transition> ipi = preset_diffs.second();

        boolean isFeasible = !iip.intersects(opo) && !oop.intersects(ipi);

        if (isFeasible) {
            iip.union(opo);
            oop.union(ipi);
            return new Place(iip, oop);
        } else return null;
    }

    public static ImplicitnessRating replaySubregionImplicitnessOn(BitMask on, Place place, VariantMarkingHistories placeHistory, Map<Place, VariantMarkingHistories> histories) {
        for (Map.Entry<Place, VariantMarkingHistories> entry : histories.entrySet()) {
            VariantMarkingHistories h = entry.getValue();
            if (placeHistory.gtOn(on, h)) {
                // the examined place is a log replay subregion of an existing place
                Place p3 = computeP3(place, entry.getKey());
                if (p3 == null) return new ReplacementPlaceInfeasible();
                else return new ReplaceExaminedPlace(place, entry.getKey(), p3);
            } else if (placeHistory.ltOn(on, h)) {
                // an existing place is a log replay subregion of the examined place
                Place p3 = computeP3(entry.getKey(), place);
                if (p3 == null) return new ReplacementPlaceInfeasible();
                else return new ReplaceExistingPlace(place, entry.getKey(), p3);
            }
        }
        return BooleanImplicitness.NOT_IMPLICIT;
    }

    public static ImplicitnessRating replaySubregionImplicitness(Place place, VariantMarkingHistories placeHistory, Map<Place, VariantMarkingHistories> histories) {
        for (Map.Entry<Place, VariantMarkingHistories> entry : histories.entrySet()) {
            VariantMarkingHistories h = entry.getValue();
            if (placeHistory.gt(h)) {
                // the examined place is a log replay subregion of an existing place
                Place p3 = computeP3(place, entry.getKey());
                if (p3 == null) return new ReplacementPlaceInfeasible();
                else return new ReplaceExaminedPlace(place, entry.getKey(), p3);
            } else if (placeHistory.lt(h)) {
                // an existing place is a log replay subregion of the examined place
                Place p3 = computeP3(entry.getKey(), place);
                if (p3 == null) return new ReplacementPlaceInfeasible();
                else return new ReplaceExistingPlace(place, entry.getKey(), p3);
            }
        }
        return BooleanImplicitness.NOT_IMPLICIT;
    }

    public static ImplicitnessRating replaySubregionImplicitnessLocally(Place place, VariantMarkingHistories placeHistory, Map<Place, VariantMarkingHistories> histories) {
        BitMask perfectlyFitting = placeHistory.getPerfectlyFittingVariants();
        for (Map.Entry<Place, VariantMarkingHistories> entry : histories.entrySet()) {
            VariantMarkingHistories h = entry.getValue();
            BitMask on = NonMutatingSetOperations.intersection(perfectlyFitting, h.getPerfectlyFittingVariants());
            if (placeHistory.gtOn(on, h)) {
                // the examined place is a log replay subregion of an existing place
                Place p3 = computeP3(place, entry.getKey());
                if (p3 == null) return new ReplacementPlaceInfeasible();
                else return new ReplaceExaminedPlace(place, entry.getKey(), p3);
            } else if (placeHistory.ltOn(on, h)) {
                // an existing place is a log replay subregion of the examined place
                Place p3 = computeP3(entry.getKey(), place);
                if (p3 == null) return new ReplacementPlaceInfeasible();
                else return new ReplaceExistingPlace(place, entry.getKey(), p3);
            }
        }
        return BooleanImplicitness.NOT_IMPLICIT;
    }
}
