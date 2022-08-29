package org.processmining.specpp.util;

import java.math.BigInteger;

@Deprecated
public class NumberOfSons {

    public BigInteger calc(int activityCount) {
        BigInteger p_total = BigInteger.valueOf(4).pow(activityCount);
        System.out.println("(2^|A|)^2=" + p_total);
        BigInteger cutoff = BigInteger.valueOf(2).pow(activityCount - 2 + activityCount);
        BigInteger p_remaining = p_total.subtract(cutoff);
        System.out.println("cutoff=" + cutoff);
        System.out.println("remaining=" + p_remaining);
        return p_total;
    }

}
