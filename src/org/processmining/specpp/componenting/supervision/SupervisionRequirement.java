package org.processmining.specpp.componenting.supervision;

import org.processmining.specpp.componenting.system.ComponentType;
import org.processmining.specpp.componenting.system.Requirement;
import org.processmining.specpp.datastructures.util.Label;

public abstract class SupervisionRequirement implements Requirement<Object, SupervisionRequirement> {

    protected final Label label;

    protected SupervisionRequirement(Label label) {
        this.label = label;
    }

    public Label getLabel() {
        return label;
    }

    protected boolean labelIsGt(SupervisionRequirement other) {
        return label.gt(other.label);
    }

    protected boolean labelIsLt(SupervisionRequirement other) {
        return label.lt(other.label);
    }

    @Override
    public ComponentType componentType() {
        return ComponentType.Supervision;
    }

}
