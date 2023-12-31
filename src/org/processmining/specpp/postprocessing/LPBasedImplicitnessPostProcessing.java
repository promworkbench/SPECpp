package org.processmining.specpp.postprocessing;

import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.specpp.config.components.SimpleBuilder;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.evaluation.implicitness.LPBasedImplicitnessCalculator;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class LPBasedImplicitnessPostProcessing implements CollectionOfPlacesPostProcessor {

    public static class Builder extends ComponentSystemAwareBuilder<LPBasedImplicitnessPostProcessing> {

        protected DelegatingDataSource<IntEncodings<Transition>> transitionEncodingsSource = new DelegatingDataSource<>();
        protected DelegatingDataSource<SimpleBuilder<LPBasedImplicitnessCalculator>> implicitnessCalculatorBuilder = new DelegatingDataSource<>();

        public Builder() {
            globalComponentSystem().require(DataRequirements.LP_BASED_IMPLICITNESS_CALCULATOR_DATA_REQUIREMENT, implicitnessCalculatorBuilder);
        }

        @Override
        protected LPBasedImplicitnessPostProcessing buildIfFullySatisfied() {
            return new LPBasedImplicitnessPostProcessing(implicitnessCalculatorBuilder.getData().build());
        }

        @Override
        public LPBasedImplicitnessPostProcessing build() {
            if (implicitnessCalculatorBuilder.isSet()) return buildIfFullySatisfied();
            else if (transitionEncodingsSource.isSet())
                return new LPBasedImplicitnessPostProcessing(transitionEncodingsSource.getData());
            else return insufficientRequirements();
        }
    }

    protected final LPBasedImplicitnessCalculator calculator;

    public LPBasedImplicitnessPostProcessing(IntEncodings<Transition> transitionEncodings) {
        calculator = new LPBasedImplicitnessCalculator(transitionEncodings);
    }

    public LPBasedImplicitnessPostProcessing(LPBasedImplicitnessCalculator calculator) {
        this.calculator = calculator;
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

    public static class Interruptible extends LPBasedImplicitnessPostProcessing {


        public static class Builder extends LPBasedImplicitnessPostProcessing.Builder {
            @Override
            protected LPBasedImplicitnessPostProcessing buildIfFullySatisfied() {
                return new Interruptible(implicitnessCalculatorBuilder.getData().build());
            }
        }

        public Interruptible(IntEncodings<Transition> transitionEncodings) {
            super(transitionEncodings);
        }

        public Interruptible(LPBasedImplicitnessCalculator calculator) {
            super(calculator);
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

                if (Thread.currentThread().isInterrupted()) return null; // purposefully not clearing interrupt flag

                // reuse the f ing LP
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

}
