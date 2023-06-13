package org.processmining.specpp.datastructures.transitionSystems;

import org.processmining.specpp.datastructures.log.Activity;

/**
 * Represents a transition of the prefix automaton
 */
public class PATransition {

    /**
     * Activity.
     */
    private Activity activity;

    /**
     * Pointer to the state the transition is pointing to.
     */
    private PAState pointer;

    /**
     * Creates a new transition with activity a.
     *
     * @param a Activity.
     */
    public PATransition(Activity a) {
        this.pointer = new PAState();
        this.activity = a;
    }

    /**
     * Returns the activity of the transition.
     *
     * @return Activity.
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Sets the activity of the transition.
     *
     * @param a Activity.
     */
    public void setActivity(Activity a) {
        this.activity = a;
    }

    /**
     * Returns the pointer to the state the transition is pointing to.
     *
     * @return State.
     */
    public PAState getPointer() {
        return pointer;
    }

    /**
     * Sets the pointer to the state the transition is pointing to.
     *
     * @param p Pointer.
     */
    public void setPointer(PAState p) {
        this.pointer = p;
    }

    /**
     * Returns a String describing the transition.
     *
     * @return String.
     */
    public String toString() {
        return "Transition: " + this.activity.toString() + " ) ";
    }
}
