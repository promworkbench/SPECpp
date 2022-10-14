package org.processmining.specpp.composition;

import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.evaluation.implicitness.ImplicitnessRating;

public interface OffersImplicitness {

    ImplicitnessRating rateImplicitness(Place place);


}
