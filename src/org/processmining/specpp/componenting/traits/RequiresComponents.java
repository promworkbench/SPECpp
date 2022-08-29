package org.processmining.specpp.componenting.traits;

import com.google.common.collect.Table;
import org.processmining.specpp.componenting.delegators.Container;
import org.processmining.specpp.componenting.system.ComponentType;
import org.processmining.specpp.componenting.system.FulfilledRequirement;
import org.processmining.specpp.componenting.system.FulfilledRequirementsCollection;
import org.processmining.specpp.componenting.system.Requirement;

import java.util.Map;

public interface RequiresComponents {

    Table<ComponentType, Requirement<?, ?>, Container<?>> componentRequirements();

    default <R extends Requirement<?, R>> void instantiateFrom(FulfilledRequirementsCollection<R> exposer) {
        for (Map.Entry<Requirement<?, ?>, Container<?>> entry : componentRequirements().row(exposer.componentType())
                                                                                       .entrySet()) {
            for (FulfilledRequirement<?, R> fulfilledRequirement : exposer.fulfilledRequirements()) {
                instantiateWith(entry.getKey(), entry.getValue(), fulfilledRequirement);
                if (entry.getValue().isFull()) break;
            }
        }
    }

    default <R extends Requirement<?, R>, F extends FulfilledRequirement<?, R>> void instantiateFromSequentially(Iterable<F> fr) {
        for (FulfilledRequirement<?, R> f : fr) {
            instantiateFrom(f);
        }
    }

    default <R extends Requirement<?, R>> void instantiateFrom(FulfilledRequirement<?, R> fulfilledRequirement) {
        for (Map.Entry<Requirement<?, ?>, Container<?>> entry : componentRequirements().row(fulfilledRequirement.componentType())
                                                                                       .entrySet()) {
            instantiateWith(entry.getKey(), entry.getValue(), fulfilledRequirement);
        }
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    default <R extends Requirement<?, R>> void instantiateWith(Requirement<?, ?> requirement, Container<?> container, FulfilledRequirement<?, R> fulfilledRequirement) {
        if (container.isFull()) return;
        R r = (R) requirement;
        if (canBeSatisfiedBy(r, fulfilledRequirement)) {
            ((Container) container).addContent(fulfilledRequirement.getContent());
        }
    }

    static <R extends Requirement<?, R>> boolean canBeSatisfiedBy(R requirement, FulfilledRequirementsCollection<R> fulfilledRequirementProvider) {
        return fulfilledRequirementProvider.fulfilledRequirements()
                                           .stream()
                                           .anyMatch(f -> canBeSatisfiedBy(requirement, f));
    }

    static <R extends Requirement<?, R>> boolean canBeSatisfiedBy(R requirement, FulfilledRequirement<?, R> fulfilledRequirement) {
        return fulfilledRequirement.gt(requirement);
    }


}
