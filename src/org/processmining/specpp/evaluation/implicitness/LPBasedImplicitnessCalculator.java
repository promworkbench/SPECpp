package org.processmining.specpp.evaluation.implicitness;

import com.google.common.collect.Streams;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.Relationship;
import org.processmining.specpp.datastructures.encoding.HashmapEncoding;
import org.processmining.specpp.datastructures.encoding.IntEncoding;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.Triple;
import org.processmining.specpp.datastructures.vectorization.IntVectorStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LPBasedImplicitnessCalculator {


    public static Set<Place> filterIPIdentifiedImplicitPlaces(Set<Place> places, IntEncodings<Transition> pair) {
        ArrayList<Place> placeList = new ArrayList<>(places);
        List<Transition> transitionList = Streams.concat(pair.pre().domain(), pair.post().domain())
                                                 .distinct()
                                                 .collect(Collectors.toList());
        HashmapEncoding<Transition> allTransitions = HashmapEncoding.ofList(transitionList);

        Triple<IntVectorStorage> matrices = incidenceMatrices(placeList, allTransitions);


        int placeCount = placeList.size(), transitionCount = transitionList.size();


        LinearConstraint lc = new LinearConstraint(new double[0], Relationship.EQ, 0);

        return null;
    }

    public static Triple<IntVectorStorage> incidenceMatrices(List<Place> places, IntEncoding<Transition> allTransitions) {

        int[] lengths = new int[places.size()];
        Arrays.fill(lengths, allTransitions.size());
        IntVectorStorage preIncidence = IntVectorStorage.zeros(lengths);
        IntVectorStorage postIncidence = IntVectorStorage.zeros(lengths);
        IntVectorStorage incidence = IntVectorStorage.zeros(lengths);

        IntStream.range(0, places.size()).forEach(i -> {
            Place p = places.get(i);
            allTransitions.pairs().forEach(tup -> {
                int sum = 0;
                Transition t = tup.getT1();
                Integer j = tup.getT2();
                if (p.preset().contains(t)) {
                    preIncidence.setVectorElement(i, j, 1);
                    ++sum;
                }
                if (p.postset().contains(t)) {
                    postIncidence.setVectorElement(i, j, 1);
                    --sum;
                }
                if (sum != 0) incidence.setVectorElement(i, j, sum);
            });
        });

        return new Triple<>(preIncidence, postIncidence, incidence);
    }


}
