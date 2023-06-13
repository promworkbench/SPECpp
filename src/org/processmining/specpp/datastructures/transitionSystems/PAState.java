package org.processmining.specpp.datastructures.transitionSystems;

import org.processmining.specpp.datastructures.log.Activity;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Represents a state of the Prefix Automaton.
 */
public class PAState {

    /**
     * List of outgoing transitions.
     */
    private final LinkedList<PATransition> outgoingTrans;

    /**
     * Create a new prefix automaton state (empty List of Transitions).
     */
    public PAState() {
        outgoingTrans = new LinkedList<>();
    }

    /**
     * Create a new prefix automaton state.
     *
     * @param outgoingTrans List of outgoing transitions.
     */
    public PAState(LinkedList<PATransition> outgoingTrans) {
        this.outgoingTrans = outgoingTrans;
    }


    /**
     * Check if state is final.
     *
     * @return True if state has no outgoing transitions, i.e., is final. Otherwise false.
     */
    public boolean isFinal() {
        return this.outgoingTrans.isEmpty();
    }

    /**
     * Adds an outgoing transition to the state.
     *
     * @param t Outgoing transition.
     */
    public void addOutgoingTrans(PATransition t) {
        this.outgoingTrans.add(t);
    }

    /**
     * Checks if the state has a given transition.
     *
     * @param a Activity.
     * @return True, if state has an (outgoing) transition a. Otherwise, false.
     */
    public boolean checkForOutgoingAct(Activity a) {
        ListIterator<PATransition> it = outgoingTrans.listIterator();
        boolean contains = false;

        while (it.hasNext()) {
            PATransition next = it.next();
            if (next.getActivity().equals(a)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns list of outgoing Transitions.
     *
     * @return List of outgoing Transitions.
     */
    public LinkedList<PATransition> getOutgoingTrans() {
        return outgoingTrans;
    }

    /**
     * Returns transition with activity a.
     *
     * @param a Activity.
     * @return Transition.
     */
    public PATransition getTrans(Activity a) {
        for (PATransition t : outgoingTrans) {
            if (a.equals(t.getActivity())) {
                return t;
            }
        }
        return null;
    }


}
