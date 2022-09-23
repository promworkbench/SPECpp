package org.processmining.specpp.prom.mvc.swing;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class TitledBorderPanel extends JPanel {

    private GridBagConstraints c;

    public TitledBorderPanel(String title, LayoutManager layout) {
        super(layout);
        makeBorder(title);
    }

    public TitledBorderPanel(String title) {
        super(new GridBagLayout());
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(1, 1, 1, 1);
        c.gridx = 0;
        c.gridy = 0;
        makeBorder(title);
    }

    private void makeBorder(String title) {
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), title, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, getFont().deriveFont(18f)));
    }

    public GridBagConstraints getGridBagConstraints() {
        return c;
    }

    public void append(JComponent component) {
        add(component, c);
        c.gridy++;
    }

    public void completeWithWhitespace() {
        c.weighty = 1;
        c.weightx = 1;
        add(Box.createVerticalGlue(), c);
        c.gridx++;
        add(Box.createHorizontalGlue(), c);
    }


}
