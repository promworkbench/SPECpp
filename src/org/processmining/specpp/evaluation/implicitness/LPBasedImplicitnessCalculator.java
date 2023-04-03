package org.processmining.specpp.evaluation.implicitness;

import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.optim.linear.*;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.specpp.componenting.traits.IsGlobalProvider;
import org.processmining.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IntEncoding;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.EvaluationParameterTuple2;
import org.processmining.specpp.datastructures.util.ImmutableTuple2;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.util.JavaTypingUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PrimitiveIterator;

public class LPBasedImplicitnessCalculator extends AbstractGlobalComponentSystemUser implements ProvidesEvaluators, IsGlobalProvider {


    public static class Builder extends ComponentSystemAwareBuilder<LPBasedImplicitnessCalculator> {

        protected DelegatingDataSource<IntEncodings<Transition>> transitionEncodingsSource = new DelegatingDataSource<>();

        public Builder() {
            globalComponentSystem().require(DataRequirements.ENC_TRANS, transitionEncodingsSource);
        }

        @Override
        protected LPBasedImplicitnessCalculator buildIfFullySatisfied() {
            return new LPBasedImplicitnessCalculator(transitionEncodingsSource.getData());
        }
    }

    protected final IntEncoding<Transition> combinedEncoding;
    protected final IntEncodings<Transition> transitionEncodings;

    public LPBasedImplicitnessCalculator(IntEncodings<Transition> transitionEncodings) {
        combinedEncoding = transitionEncodings.unionizedEncoding();
        this.transitionEncodings = transitionEncodings;
        globalComponentSystem().provide(EvaluationRequirements.evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), BooleanImplicitness.class, this::compute));
        provideSelf();
    }

    protected void provideSelf() {
        globalComponentSystem().provide(DataRequirements.LP_BASED_IMPLICITNESS_CALCULATOR_DATA_REQUIREMENT.fulfilWithStatic(() -> new LPBasedImplicitnessCalculator(transitionEncodings)));
    }

    private BooleanImplicitness compute(EvaluationParameterTuple2<Place, Collection<Place>> input) {
        return isImplicitAmong(input.getT1(), input.getT2()) ? BooleanImplicitness.IMPLICIT : BooleanImplicitness.NOT_IMPLICIT;
    }

    public boolean isImplicitAmong(Place placeToTest, Collection<Place> existingPlaces) {
        if (existingPlaces.isEmpty()) return false;

        ArrayList<Place> places = new ArrayList<>(1 + existingPlaces.size());
        places.add(placeToTest);
        places.addAll(existingPlaces);

        Tuple2<List<BitMask>, List<int[]>> matrices = computeIncidenceMatrices(places);

        return isImplicitAmong(places.indexOf(placeToTest), places, matrices.getT1(), matrices.getT2());
    }

    public Tuple2<List<BitMask>, List<int[]>> computeIncidenceMatrices(List<Place> places) {
        List<BitMask> preIncidenceMatrix = new ArrayList<>();
        // apparently unneeded
        // List<BitMask> postIncidenceMatrix = new ArrayList<>();
        List<int[]> incidenceMatrix = new ArrayList<>();
        for (int p = 0; p < places.size(); p++) {
            Place place = places.get(p);
            preIncidenceMatrix.add(place.preset().getBitMask().copy());
            // postIncidenceMatrix.add(place.postset().getBitMask().copy());
            int[] incidence = new int[combinedEncoding.size()];
            place.preset().streamItems().map(combinedEncoding::encode).forEach(i -> incidence[i] += 1);
            place.postset().streamItems().map(combinedEncoding::encode).forEach(i -> incidence[i] -= 1);
            incidenceMatrix.add(incidence);
        }
        return new ImmutableTuple2<>(preIncidenceMatrix, incidenceMatrix);
    }


    //use an LPP solver on the given parameters to test the place defined by placePos for implicitness
    //variables: y | z | k | x --> places.size()+places.size()+1+1
    public boolean isImplicitAmong(int currentPlaceIndex, List<Place> places, List<BitMask> preIncidenceMatrix, List<int[]> incidenceMatrix) {
        assert places.size() == preIncidenceMatrix.size();
        assert places.size() == incidenceMatrix.size();
        assert 0 <= currentPlaceIndex;
        assert currentPlaceIndex < places.size();

        //For initial marking 0, variables k, x and reference sets Y, Z the objective functions is
        //0*y1+0*y2+ ... 0*yn + 0*z1+0*z2+ ... 0*zn + 1*k + 0*x + 0
        //This simplifies to 1*k
        int placeCount = places.size();
        int yVariablesCount = placeCount, zVariablesCount = placeCount;
        int totalVariablesCount = yVariablesCount + zVariablesCount + 1 + 1;
        int kVariableIndex = yVariablesCount + zVariablesCount;
        int xVariableIndex = yVariablesCount + zVariablesCount + 1;
        double[] coefficientsLinearObjectiveFunction = new double[totalVariablesCount]; //there are 2*|places| coefficients for Y, Z and 2 coefficient for k, x
        coefficientsLinearObjectiveFunction[kVariableIndex] = 1; //k*1
        LinearObjectiveFunction objectiveFunction = new LinearObjectiveFunction(coefficientsLinearObjectiveFunction, 0);

        //Add the linear constraints w.r.t. the current place currP, using k, x, Y, Z, incMatrix, preIncMatrix
        Collection<LinearConstraint> constraints = new ArrayList<>();
        //Type 0: ensure currP is not in Y, Z, that is currP=0
        double[] forceCurrentPlaceToZeroInY = new double[totalVariablesCount]; //coefficients are all 0, except for currP in Y
        double[] forceCurrentPlaceToZeroInZ = new double[totalVariablesCount]; //coefficients are all 0, except for currP in Z
        forceCurrentPlaceToZeroInY[currentPlaceIndex] = 1; // y_{curr_p}=1
        forceCurrentPlaceToZeroInZ[yVariablesCount + currentPlaceIndex] = 1; // z_{curr_p}=1
        constraints.add(new LinearConstraint(forceCurrentPlaceToZeroInY, Relationship.EQ, 0));
        constraints.add(new LinearConstraint(forceCurrentPlaceToZeroInZ, Relationship.EQ, 0));

        //Type 1: Y>=Z>=0, k>=0, x=0, x<k--> x-k<=-0
        //Y>=Z>=0 --> Z>=0 AND Y-Z>=0
        for (int p = 0; p < placeCount; p++) {
            double[] nonNegZ = new double[totalVariablesCount];//coefficients are all 0, except for the current z=1
            double[] YGegZ = new double[totalVariablesCount];//coefficients are all 0, except for the current y=1 and z=-1
            nonNegZ[yVariablesCount + p] = 1; // z=1
            YGegZ[p] = 1; // y=1
            YGegZ[yVariablesCount + p] = -1; // z=-1
            constraints.add(new LinearConstraint(nonNegZ, Relationship.GEQ, 0));//1*z>=0
            constraints.add(new LinearConstraint(YGegZ, Relationship.GEQ, 0));//1*y-1*z>=0
        }
        //k>=0, x=0, x-k<=-1
        double[] nonNegk = new double[totalVariablesCount];//coefficients are all 0, except for k=1
        double[] zerox = new double[totalVariablesCount];//coefficients are all 0, except for x=1
        double[] xSmallerk = new double[totalVariablesCount];//coefficients are all 0, except for x=1, k=-1
        nonNegk[kVariableIndex] = 1; // k=1
        zerox[xVariableIndex] = 1; // x=1
        xSmallerk[kVariableIndex] = -1; // k=-1
        xSmallerk[xVariableIndex] = 1; // x=1
        constraints.add(new LinearConstraint(xSmallerk, Relationship.LEQ, -1));//1*x-1*k <=-1
        constraints.add(new LinearConstraint(nonNegk, Relationship.GEQ, 0));//1*k>=0
        constraints.add(new LinearConstraint(zerox, Relationship.EQ, 0));//1*x=0

        //Type 2: Y*incMatrix<=k*inc(currP) ---> Y*incMatrix - k*inc(currP) <=0;

        // one constraint per transition
        combinedEncoding.primitiveRange().forEach(t -> {
            double[] coefficients = new double[totalVariablesCount];//coefficients are based on incMatrix
            for (int p = 0; p < placeCount; p++) {
                coefficients[p] = incidenceMatrix.get(p)[t]; //find the coefficient for Y each pair (p,t)
            }
            //coefficient for k is -incMatrix[currP, t]
            coefficients[kVariableIndex] = -incidenceMatrix.get(currentPlaceIndex)[t];
            //coefficient for x is 0;
            coefficients[xVariableIndex] = 0;
            //the sum should be <= inc(currP,t)
            constraints.add(new LinearConstraint(coefficients, Relationship.LEQ, 0));
        });


        //Type 3: forall t with currP in pre(t): Z*pre(q, t) + x >= k *pre(currP, t), for q in P/{currP}
        // --> Z*pre(q, t) + x - k* pre(currP, t) >=0 	// in contrast to paper (imp places in net systems, garcia&colom, proposition 13) seems to work fine for k=1. explain this result theoretically?!

        PrimitiveIterator.OfInt preIncidentTransitions = preIncidenceMatrix.get(currentPlaceIndex).iterator();
        while (preIncidentTransitions.hasNext()) {
            int encodedPresetTransition = preIncidentTransitions.nextInt();
            double[] coefficients = new double[totalVariablesCount];
            for (int p = 0; p < placeCount; p++) {
                if (preIncidenceMatrix.get(p).get(encodedPresetTransition))
                    coefficients[yVariablesCount + p] = 1; //coefficients of Z = pre(p,t)
            }
            //coefficients[places.size()*2] = (-1)*preIncMatrix.get(currP)[t];//coefficient of k=-pre(currP, t)
            coefficients[xVariableIndex] = 1; //coefficient of x=1
            constraints.add(new LinearConstraint(coefficients, Relationship.GEQ, 1));
        }
        LinearOptimizer solver = new SimplexSolver();
        try {
            solver.optimize(objectiveFunction, new LinearConstraintSet(constraints));
        } catch (org.apache.commons.math3.optim.linear.NoFeasibleSolutionException | UnboundedSolutionException |
                 TooManyIterationsException ignored) {
            //				System.out.println("LPP Solver found no feasible solution for reference set of place "+placeToNamedString(places[currP], transitions));
            return false;
        }

        return true;
    }

}
