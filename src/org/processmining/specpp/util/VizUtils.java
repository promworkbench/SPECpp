package org.processmining.specpp.util;

import org.processmining.specpp.supervision.observations.Visualization;

import javax.swing.*;

public class VizUtils {
    public static JFrame showVisualization(Visualization<?> vis) {
        return showJComponent("Visualization: " + vis.getTitle(), vis.getComponent(), false);
    }

    public static JFrame showJComponent(String title, JComponent jComponent, boolean requestFocus) {
        JFrame jFrame = new JFrame(title);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setAutoRequestFocus(requestFocus);
        jFrame.setContentPane(jComponent);
        jFrame.setSize(800, 600);
        jFrame.setVisible(true);
        return jFrame;
    }
}
