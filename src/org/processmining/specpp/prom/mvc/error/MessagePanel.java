package org.processmining.specpp.prom.mvc.error;

import org.processmining.framework.util.ui.widgets.ProMTextArea;

import javax.swing.*;
import java.awt.*;

public class MessagePanel extends JPanel {

    public MessagePanel(String message) {
        super(new BorderLayout());
        ProMTextArea area = new ProMTextArea(false);
        area.setText(message);
        add(area, BorderLayout.CENTER);
    }

}
