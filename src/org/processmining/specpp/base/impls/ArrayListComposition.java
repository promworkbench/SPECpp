package org.processmining.specpp.base.impls;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.Candidate;

import java.util.ArrayList;

public class ArrayListComposition<C extends Candidate> extends AbstractComposition<C, ArrayList<C>> implements AdvancedComposition<C> {

    public static final int ABSOLUTE_SIZE_LIMIT = Integer.MAX_VALUE;

    public ArrayListComposition() {
        super(ArrayList::new);
    }

    @Override
    protected void initSelf() {

    }

    @Override
    public int maxSize() {
        return ABSOLUTE_SIZE_LIMIT;
    }

    public boolean hasCapacityLeft() {
        return size() < ABSOLUTE_SIZE_LIMIT;
    }

    @Override
    public void remove(C item) {
        candidates.remove(item);
    }

    @Override
    public C removeLast() {
        C last = getLastAcceptedCandidate();
        remove(last);
        setLastAcceptedCandidate(candidates.get(candidates.size() - 1));
        return last;
    }

}
