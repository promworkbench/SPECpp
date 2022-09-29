package org.processmining.specpp.postprocessing;

import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.evaluation.implicitness.LPBasedImplicitnessCalculator;

import java.util.ArrayList;
import java.util.List;

public class LPBasedImplicitnessPostProcessing implements CollectionOfPlacesPostProcessor {

    public static class Builder extends ComponentSystemAwareBuilder<LPBasedImplicitnessPostProcessing> {

        protected DelegatingDataSource<IntEncodings<Transition>> transitionEncodingsSource = new DelegatingDataSource<>();

        public Builder() {
            globalComponentSystem().require(DataRequirements.ENC_TRANS, transitionEncodingsSource);
        }

        @Override
        protected LPBasedImplicitnessPostProcessing buildIfFullySatisfied() {
            return new LPBasedImplicitnessPostProcessing(transitionEncodingsSource.getData());
        }
    }

    private final LPBasedImplicitnessCalculator calculator;

    public LPBasedImplicitnessPostProcessing(IntEncodings<Transition> transitionEncodings) {
        calculator = new LPBasedImplicitnessCalculator(transitionEncodings);
    }

    @Override
    public CollectionOfPlaces postProcess(CollectionOfPlaces collectionOfPlaces) {
        //compute all the stuff needed for the LPP

        List<Place> places = new ArrayList<>(collectionOfPlaces.getPlaces());
        List<Place> survivors = new ArrayList<>();

        Tuple2<List<BitMask>, List<int[]>> matrices = calculator.computeIncidenceMatrices(places);
        List<BitMask> preIncidenceMatrix = matrices.getT1();
        List<int[]> incidenceMatrix = matrices.getT2();

        //do the LPP magic to check implicitness for each place
        //increase speed by removing implicit places for the next iteration

        int placesCount = places.size();
        int i = 0;
        while (i < placesCount) {
            if (calculator.isImplicitAmong(i, places, preIncidenceMatrix, incidenceMatrix)) {
                /*
                for (int j = i; j < places.size() - 1; j++) {
                    places.set(j, places.get(j + 1));
                    preIncidenceMatrix.set(j, preIncidenceMatrix.get(j + 1));
                    incidenceMatrix.set(j, incidenceMatrix.get(j + 1));
                }*/
                places.remove(i);
                preIncidenceMatrix.remove(i);
                incidenceMatrix.remove(i);
                placesCount--;
            } else {
                survivors.add(places.get(i));
                i++;
            }
        }

        return new CollectionOfPlaces(survivors);
    }


}
