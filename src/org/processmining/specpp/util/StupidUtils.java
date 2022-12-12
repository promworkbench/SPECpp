package org.processmining.specpp.util;

public class StupidUtils {

    public static  <T extends Comparable<T>> T takeMin(T one, T two) {
        if (one == null && two == null) return null;
        else if (one == null) return two;
        else if (two == null) return one;
        else return one.compareTo(two) <= 0 ? one : two;
    }

}
