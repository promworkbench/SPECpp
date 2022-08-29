package org.processmining.specpp.evaluation.implicitness;

public enum BooleanImplicitness implements ImplicitnessRating {
    IMPLICIT(true), NOT_IMPLICIT(false);

    private final boolean isImplicit;

    BooleanImplicitness(boolean isImplicit) {
        this.isImplicit = isImplicit;
    }

    public boolean bool() {
        return isImplicit;
    }

}
