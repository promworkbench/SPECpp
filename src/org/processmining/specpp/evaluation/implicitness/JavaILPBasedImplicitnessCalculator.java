package org.processmining.specpp.evaluation.implicitness;

import net.sf.javailp.*;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.Transition;

import java.util.List;
import java.util.PrimitiveIterator;

public class JavaILPBasedImplicitnessCalculator extends LPBasedImplicitnessCalculator {
    public JavaILPBasedImplicitnessCalculator(IntEncodings<Transition> transitionEncodings) {
        super(transitionEncodings);
    }

    public static class Builder extends LPBasedImplicitnessCalculator.Builder {
        @Override
        protected JavaILPBasedImplicitnessCalculator buildIfFullySatisfied() {
            return new JavaILPBasedImplicitnessCalculator(transitionEncodingsSource.getData());
        }
    }

    @Override
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

        //Add the linear constraints w.r.t. the current place currP, using k, x, Y, Z, incMatrix, preIncMatrix
        Problem problem = new Problem();
        Linear lin = new Linear();
        lin.add(1, kVariableIndex); //k*1
        problem.setObjective(lin, OptType.MIN);

        //Type 0: ensure currP is not in Y, Z, that is currP=0
        double[] forceCurrentPlaceToZeroInY = new double[totalVariablesCount]; //coefficients are all 0, except for currP in Y
        double[] forceCurrentPlaceToZeroInZ = new double[totalVariablesCount]; //coefficients are all 0, except for currP in Z
        forceCurrentPlaceToZeroInY[currentPlaceIndex] = 1; // y_{curr_p}=1
        forceCurrentPlaceToZeroInZ[yVariablesCount + currentPlaceIndex] = 1; // z_{curr_p}=1

        lin.add(1, currentPlaceIndex);
        problem.add(lin, Operator.EQ, 0);
        lin = new Linear();
        lin.add(1, yVariablesCount + currentPlaceIndex);
        problem.add(lin, Operator.EQ, 0);

        //Type 1: Y>=Z>=0, k>=0, x=0, x<k--> x-k<=-0
        //Y>=Z>=0 --> Z>=0 AND Y-Z>=0
        for (int p = 0; p < placeCount; p++) {
            double[] nonNegZ = new double[totalVariablesCount];//coefficients are all 0, except for the current z=1
            double[] YGegZ = new double[totalVariablesCount];//coefficients are all 0, except for the current y=1 and z=-1
            nonNegZ[yVariablesCount + p] = 1; // z=1
            YGegZ[p] = 1; // y=1
            YGegZ[yVariablesCount + p] = -1; // z=-1
            lin = new Linear();
            lin.add(1, yVariablesCount + p);
            problem.add(lin, Operator.GE, 0);
            lin = new Linear();
            lin.add(1, p);
            lin.add(-1, yVariablesCount + p);
            problem.add(lin, Operator.GE, 0);
        }
        //k>=0, x=0, x-k<=-1
        double[] nonNegk = new double[totalVariablesCount];//coefficients are all 0, except for k=1
        double[] zerox = new double[totalVariablesCount];//coefficients are all 0, except for x=1
        double[] xSmallerk = new double[totalVariablesCount];//coefficients are all 0, except for x=1, k=-1
        nonNegk[kVariableIndex] = 1; // k=1
        zerox[xVariableIndex] = 1; // x=1
        xSmallerk[kVariableIndex] = -1; // k=-1
        xSmallerk[xVariableIndex] = 1; // x=1
        lin = new Linear();
        lin.add(-1, kVariableIndex);
        lin.add(1, xVariableIndex);
        problem.add(lin, Operator.LE, -1);
        lin = new Linear();
        lin.add(1, kVariableIndex);
        problem.add(lin, Operator.GE, 0);
        lin = new Linear();
        lin.add(1, xVariableIndex);
        problem.add(lin, Operator.EQ, 0);
        //Type 2: Y*incMatrix<=k*inc(currP) ---> Y*incMatrix - k*inc(currP) <=0;

        // one constraint per transition
        combinedEncoding.primitiveRange().forEach(t -> {
            Linear linear = new Linear();
            for (int p = 0; p < placeCount; p++) {
                if (p == xVariableIndex) linear.add(0, xVariableIndex); //coefficient for x is 0;
                else if (p == kVariableIndex)
                    linear.add(-incidenceMatrix.get(currentPlaceIndex)[t], kVariableIndex); //coefficient for k is -incMatrix[currP, t]
                else //find the coefficient for Y each pair (p,t)
                    linear.add(incidenceMatrix.get(p)[t], p);
            }
            //the sum should be <= inc(currP,t)
            problem.add(linear, Operator.LE, 0);
        });


        //Type 3: forall t with currP in pre(t): Z*pre(q, t) + x >= k *pre(currP, t), for q in P/{currP}
        // --> Z*pre(q, t) + x - k* pre(currP, t) >=0 	// in contrast to paper (imp places in net systems, garcia&colom, proposition 13) seems to work fine for k=1. explain this result theoretically?!

        PrimitiveIterator.OfInt preIncidentTransitions = preIncidenceMatrix.get(currentPlaceIndex).iterator();
        while (preIncidentTransitions.hasNext()) {
            int encodedPresetTransition = preIncidentTransitions.nextInt();
            lin = new Linear();
            for (int p = 0; p < placeCount; p++) {
                if (p == xVariableIndex) lin.add(1, xVariableIndex);
                else if (preIncidenceMatrix.get(p).get(encodedPresetTransition)) //coefficients of Z = pre(p,t)
                    lin.add(1, yVariablesCount + p);
            }
            problem.add(lin, Operator.GE, 1);
        }

        Solver solver = new SolverFactoryLpSolve().get();
        Result result = solver.solve(problem);
        return result != null;
    }

}
