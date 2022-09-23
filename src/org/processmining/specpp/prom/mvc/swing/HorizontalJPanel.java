package org.processmining.specpp.prom.mvc.swing;

import javax.swing.*;

public class HorizontalJPanel extends JPanel {
    public void addSpaced(JComponent component) {
        add(Box.createHorizontalStrut(5));
        add(component);
    }
}
