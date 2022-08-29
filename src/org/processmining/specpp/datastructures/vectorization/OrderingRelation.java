package org.processmining.specpp.datastructures.vectorization;

import java.util.EnumSet;

public enum OrderingRelation {
    lt, gt, ltEq, gtEq, eq, neq;
    public static final EnumSet<OrderingRelation> BASE = EnumSet.of(ltEq, gtEq, eq);
}
