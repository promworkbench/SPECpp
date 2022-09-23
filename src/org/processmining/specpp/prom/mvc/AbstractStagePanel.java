package org.processmining.specpp.prom.mvc;

import org.processmining.specpp.prom.util.Destructible;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractStagePanel<C extends StageController> extends JPanel implements Destructible {

    protected final C controller;
    private List<Destructible> destructibleChildren = new LinkedList<>();

    public AbstractStagePanel(C controller) {
        this.controller = controller;
    }

    public AbstractStagePanel(C controller, LayoutManager layout) {
        super(layout);
        this.controller = controller;
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        if (comp instanceof Destructible) destructibleChildren.add((Destructible) comp);
        super.addImpl(comp, constraints, index);
    }

    @Override
    public void destroy() {
        destructibleChildren.forEach(Destructible::destroy);
    }
}
