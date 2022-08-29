package org.processmining.specpp.componenting.system;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class FulfilledRequirementsCollection<R extends Requirement<?, R>> {

    private final ArrayList<FulfilledRequirement<?, R>> list;

    protected FulfilledRequirementsCollection() {
        this.list = new ArrayList<>();
    }

    public List<? extends FulfilledRequirement<?, R>> fulfilledRequirements() {
        return list;
    }

    protected void add(FulfilledRequirement<?, R> fulfilledRequirement) {
        R r = fulfilledRequirement.getComparable();
        if (list.stream().map(FulfilledRequirement::getComparable).anyMatch(c -> c.equivalent(r))) {
            list.removeIf(f -> f.getComparable().equivalent(r));
        }
        list.add(fulfilledRequirement);
    }


    public abstract ComponentType componentType();

    public boolean hasCorrectComponentType(R requirement) {
        return componentType().equals(requirement.componentType());
    }

    public boolean canSatisfyRequirement(R requirement) {
        return hasCorrectComponentType(requirement) && fulfilledRequirements().stream()
                                                                              .filter(f -> requirement.contentClass()
                                                                                                      .isAssignableFrom(f.contentClass()))
                                                                              .anyMatch(f -> f.gt(requirement));
    }

    @SuppressWarnings("unchecked")
    public <D> FulfilledRequirement<D, R> satisfyRequirement(R requirement) throws RequirementNotSatisfiableException {
        if (!hasCorrectComponentType(requirement)) throw new RequirementNotSatisfiableException();
        return fulfilledRequirements().stream()
                                      .filter(f -> f.gt(requirement))
                                      .filter(f -> requirement.contentClass().isAssignableFrom(f.contentClass()))
                                      .findFirst()
                                      .map(f -> (FulfilledRequirement<D, R>) f)
                                      .orElseThrow(RequirementNotSatisfiableException::new);
    }

    public List<FulfilledRequirement<?, R>> multiSatisfyRequirement(R requirement) throws RequirementNotSatisfiableException {
        if (!hasCorrectComponentType(requirement)) throw new RequirementNotSatisfiableException();
        return fulfilledRequirements().stream()
                                      .filter(f -> f.gt(requirement))
                                      .filter(f -> requirement.contentClass().isAssignableFrom(f.contentClass()))
                                      .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return list.stream()
                   .map(f -> "\t" + f)
                   .collect(Collectors.joining("\n", "Fulfilled Requirements: " + "{" + "\n", "}"));
    }
}
