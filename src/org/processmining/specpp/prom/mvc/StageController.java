package org.processmining.specpp.prom.mvc;

import javax.swing.*;
import java.util.function.Supplier;

public interface StageController extends Supplier<JPanel> {

    JPanel createPanel();

    @Override
    default JPanel get() {
        return createPanel();
    }

    void startup();
}
