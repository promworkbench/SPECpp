package org.processmining.specpp.datastructures.tree.nodegen;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.tree.base.NodeState;
import org.processmining.specpp.traits.ProperlyPrintable;

import java.util.Objects;

/**
 * This class represents the local state of a {@code PlaceNode}.
 * It is mutable and used by the {@code PlaceGenerator} to determine the next unseen child node to generate. The state object will be modified in the aforementioned process.
 * While the {@code actualPresetExpansions} and {@code actualPostsetExpansions} are kept updated at all times (excl. multi-threaded use), their potential counterparts, may overestimate the real potential expansions when constraints change.
 *
 * @see #actualPresetExpansions
 * @see #actualPostsetExpansions
 * @see #potentialPresetExpansions
 * @see #potentialPostsetExpansions
 * @see NodeState
 */
public class PlaceState implements NodeState, ProperlyPrintable {

    /**
     * A bitmask of the currently known potential preset expansions.
     * It may be updated by the {@code PlaceGenerator} to serve as information for heuristics.
     */
    private final BitMask potentialPresetExpansions;
    /**
     * A bitmask of the currently known potential postset expansions.
     * It may be updated by the {@code PlaceGenerator} to serve as information for heuristics.
     */
    private final BitMask potentialPostsetExpansions;
    /**
     * A bitmask of the already created preset expansions.
     * It is updated by the {@code PlaceGenerator} when children of the corresponding tree node are generated.
     */
    private final BitMask actualPresetExpansions;
    /**
     * A bitmask of the already created postset expansions.
     * It is updated by the {@code PlaceGenerator} when children of the corresponding tree node are generated.
     */
    private final BitMask actualPostsetExpansions;

    protected PlaceState(BitMask actualPresetExpansions, BitMask actualPostsetExpansions, BitMask potentialPresetExpansions, BitMask potentialPostsetExpansions) {
        this.potentialPresetExpansions = potentialPresetExpansions;
        this.potentialPostsetExpansions = potentialPostsetExpansions;
        this.actualPresetExpansions = actualPresetExpansions;
        this.actualPostsetExpansions = actualPostsetExpansions;
    }

    public static PlaceState withPotentialExpansions(BitMask potentialPresetExpansions, BitMask potentialPostsetExpansions) {
        return new PlaceState(new BitMask(), new BitMask(), potentialPresetExpansions, potentialPostsetExpansions);
    }

    /**
     * @return whether the potential expansions indicate that a future expansion is at all possible
     */
    public boolean canNeverExpand() {
        return potentialPresetExpansions.isEmpty() && potentialPostsetExpansions.isEmpty();
    }

    /**
     * @return whether state describes a node with currently no children
     */
    public boolean isCurrentlyALeaf() {
        return actualPresetExpansions.isEmpty() && actualPostsetExpansions.isEmpty();
    }

    public BitMask getPotentialPresetExpansions() {
        return potentialPresetExpansions;
    }

    public BitMask getPotentialPostsetExpansions() {
        return potentialPostsetExpansions;
    }

    public BitMask getActualPresetExpansions() {
        return actualPresetExpansions;
    }

    public BitMask getActualPostsetExpansions() {
        return actualPostsetExpansions;
    }

    public int computeActualChildrenCount() {
        return actualPresetExpansions.cardinality() + actualPostsetExpansions.cardinality();
    }

    public BitMask getActualExpansions(MonotonousPlaceGenerationLogic.ExpansionType expansionType) {
        return expansionType == MonotonousPlaceGenerationLogic.ExpansionType.Preset ? actualPresetExpansions : actualPostsetExpansions;
    }

    public BitMask getPotentialExpansions(MonotonousPlaceGenerationLogic.ExpansionType expansionType) {
        return expansionType == MonotonousPlaceGenerationLogic.ExpansionType.Preset ? potentialPresetExpansions : potentialPostsetExpansions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlaceState that = (PlaceState) o;

        if (!Objects.equals(potentialPresetExpansions, that.potentialPresetExpansions)) return false;
        if (!Objects.equals(potentialPostsetExpansions, that.potentialPostsetExpansions)) return false;
        if (!Objects.equals(actualPresetExpansions, that.actualPresetExpansions)) return false;
        return Objects.equals(actualPostsetExpansions, that.actualPostsetExpansions);
    }

    @Override
    public int hashCode() {
        int result = potentialPresetExpansions != null ? potentialPresetExpansions.hashCode() : 0;
        result = 31 * result + (potentialPostsetExpansions != null ? potentialPostsetExpansions.hashCode() : 0);
        result = 31 * result + (actualPresetExpansions != null ? actualPresetExpansions.hashCode() : 0);
        result = 31 * result + (actualPostsetExpansions != null ? actualPostsetExpansions.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "PlaceState{" + "actualPresetExpansions=" + actualPresetExpansions + ", actualPostsetExpansions=" + actualPostsetExpansions + ";" + "potentialFuturePresetExpansions=" + potentialPresetExpansions + ", potentialFuturePostsetExpansions=" + potentialPostsetExpansions + "}";
    }
}
