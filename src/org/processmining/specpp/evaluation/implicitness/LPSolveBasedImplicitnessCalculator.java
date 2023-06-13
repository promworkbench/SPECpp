package org.processmining.specpp.evaluation.implicitness;

import org.processmining.lpengines.factories.LPEngineFactory;
import org.processmining.lpengines.interfaces.LPEngine;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;

import java.util.List;
import java.util.PrimitiveIterator;

@Deprecated
public class LPSolveBasedImplicitnessCalculator extends LPBasedImplicitnessCalculator {
    public LPSolveBasedImplicitnessCalculator(IntEncodings<Transition> transitionEncodings) {
        super(transitionEncodings);
    }

    @Override
    protected void provideSelf() {
        globalComponentSystem().provide(DataRequirements.LP_BASED_IMPLICITNESS_CALCULATOR_DATA_REQUIREMENT.fulfilWithStatic(() -> new LPSolveBasedImplicitnessCalculator(transitionEncodings)));
    }

    public static class Builder extends LPBasedImplicitnessCalculator.Builder {
        @Override
        protected LPSolveBasedImplicitnessCalculator buildIfFullySatisfied() {
            return new LPSolveBasedImplicitnessCalculator(transitionEncodingsSource.getData());
        }
    }

    @Override
    public boolean isImplicitAmong(int currentPlaceIndex, List<Place> places, List<BitMask> preIncidenceMatrix, List<int[]> incidenceMatrix) {
        assert places.size() == preIncidenceMatrix.size();
        assert places.size() == incidenceMatrix.size();
        assert 0 <= currentPlaceIndex;
        assert currentPlaceIndex < places.size();
        LPEngine lpEngine = LPEngineFactory.createLPEngine(LPEngine.EngineType.LPSOLVE_DISPOSABLE);

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
        lpEngine.setObjective(coefficientsLinearObjectiveFunction, LPEngine.ObjectiveTargetType.MIN);

        //Add the linear constraints w.r.t. the current place currP, using k, x, Y, Z, incMatrix, preIncMatrix
        //Type 0: ensure currP is not in Y, Z, that is currP=0
        double[] forceCurrentPlaceToZeroInY = new double[totalVariablesCount]; //coefficients are all 0, except for currP in Y
        double[] forceCurrentPlaceToZeroInZ = new double[totalVariablesCount]; //coefficients are all 0, except for currP in Z
        forceCurrentPlaceToZeroInY[currentPlaceIndex] = 1; // y_{curr_p}=1
        forceCurrentPlaceToZeroInZ[yVariablesCount + currentPlaceIndex] = 1; // z_{curr_p}=1
        lpEngine.addConstraint(forceCurrentPlaceToZeroInY, LPEngine.Operator.EQUAL, 0);
        lpEngine.addConstraint(forceCurrentPlaceToZeroInZ, LPEngine.Operator.EQUAL, 0);

        //Type 1: Y>=Z>=0, k>=0, x=0, x<k--> x-k<=-0
        //Y>=Z>=0 --> Z>=0 AND Y-Z>=0
        for (int p = 0; p < placeCount; p++) {
            double[] nonNegZ = new double[totalVariablesCount];//coefficients are all 0, except for the current z=1
            double[] YGegZ = new double[totalVariablesCount];//coefficients are all 0, except for the current y=1 and z=-1
            nonNegZ[yVariablesCount + p] = 1; // z=1
            YGegZ[p] = 1; // y=1
            YGegZ[yVariablesCount + p] = -1; // z=-1
            lpEngine.addConstraint(nonNegZ, LPEngine.Operator.GREATER_EQUAL, 0);
            lpEngine.addConstraint(YGegZ, LPEngine.Operator.GREATER_EQUAL, 0);
        }
        //k>=0, x=0, x-k<=-1
        double[] nonNegk = new double[totalVariablesCount];//coefficients are all 0, except for k=1
        double[] zerox = new double[totalVariablesCount];//coefficients are all 0, except for x=1
        double[] xSmallerk = new double[totalVariablesCount];//coefficients are all 0, except for x=1, k=-1
        nonNegk[kVariableIndex] = 1; // k=1
        zerox[xVariableIndex] = 1; // x=1
        xSmallerk[kVariableIndex] = -1; // k=-1
        xSmallerk[xVariableIndex] = 1; // x=1
        lpEngine.addConstraint(xSmallerk, LPEngine.Operator.LESS_EQUAL, -1);
        lpEngine.addConstraint(nonNegk, LPEngine.Operator.GREATER_EQUAL, 0);
        lpEngine.addConstraint(zerox, LPEngine.Operator.EQUAL, 0);

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
            lpEngine.addConstraint(coefficients, LPEngine.Operator.LESS_EQUAL, 0);
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
            lpEngine.addConstraint(coefficients, LPEngine.Operator.GREATER_EQUAL, 1);
        }

        boolean feasible = lpEngine.isFeasible();
        lpEngine.destroy();
        return feasible;
    }

}
