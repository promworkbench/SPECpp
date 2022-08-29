package org.processmining.specpp.composition;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.vectorization.VariantMarkingHistories;
import org.processmining.specpp.evaluation.implicitness.ImplicitnessRating;
import org.processmining.specpp.evaluation.implicitness.ReplayBasedImplicitnessCalculator;

public class TestPlaceCollection extends PlaceCollection {
    @Override
    public ImplicitnessRating rateImplicitness(Place place) {
        timeStopper.start(REPLAY_BASED_CONCURRENT_IMPLICITNESS);
        VariantMarkingHistories h = historyMaker.eval(place);
        BitMask mask = getCurrentlySupportedVariants();
        ImplicitnessRating implicitnessRating = ReplayBasedImplicitnessCalculator.replaySubregionImplicitnessOn(mask, place, h, histories);
        timeStopper.stop(REPLAY_BASED_CONCURRENT_IMPLICITNESS);
        return implicitnessRating;
    }
}
