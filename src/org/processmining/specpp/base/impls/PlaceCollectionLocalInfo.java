package org.processmining.specpp.base.impls;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.evaluation.implicitness.ImplicitnessRating;

public interface PlaceCollectionLocalInfo {

    ImplicitnessRating rateImplicitness(Place place);

    BitMask getCurrentlySupportedVariants();

}
