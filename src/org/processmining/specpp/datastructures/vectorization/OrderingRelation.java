package org.processmining.specpp.datastructures.vectorization;

import java.util.EnumSet;

public enum OrderingRelation {
    lt('<'), gt('>'), ltEq('\u2264'), gtEq('\u2265'), eq('='), neq('\u2260');
    public static final EnumSet<OrderingRelation> BASE = EnumSet.of(ltEq, gtEq, eq);

    private final Character symbol;

    OrderingRelation(Character symbol) {
        this.symbol = symbol;
    }


    @Override
    public String toString() {
        return symbol.toString();
    }

}
