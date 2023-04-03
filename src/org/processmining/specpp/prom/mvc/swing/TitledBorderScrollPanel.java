package org.processmining.specpp.prom.mvc.swing;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class TitledBorderScrollPanel extends JScrollPane {

    private final JPanel content;
    private GridBagConstraints c;

    public TitledBorderScrollPanel(String title, LayoutManager layout) {
        content = new JPanel(layout);
        createBorder(title);
        setViewportView(content);
    }

    public TitledBorderScrollPanel(String title) {
        this(title, new GridBagLayout());
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(1, 1, 1, 1);
        c.gridx = 0;
        c.gridy = 0;
    }

    private void createBorder(String title) {
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), title, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, getFont().deriveFont(18f)));
    }

    public GridBagConstraints getGridBagConstraints() {
        return c;
    }

    public void append(JComponent component) {
        content.add(component, c);
        c.gridy++;
    }

    public void completeWithWhitespace() {
        c.weighty = 1;
        c.weightx = 1;
        content.add(Box.createVerticalGlue(), c);
        c.gridx++;
        content.add(Box.createHorizontalGlue(), c);
    }

}
