package org.processmining.specpp.traits;

/**
 * Marks an object as semantic-aware hashable. That is, semantic equality coincides with a hash collision.
 */
public interface ProperlyHashable {

    int hashCode();

}
