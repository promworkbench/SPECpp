package org.processmining.specpp.base.impls;

import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.evaluation.implicitness.ImplicitnessRating;

public interface OffersImplicitness {

    ImplicitnessRating rateImplicitness(Place place);


}
