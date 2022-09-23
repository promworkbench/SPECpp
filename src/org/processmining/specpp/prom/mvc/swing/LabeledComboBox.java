package org.processmining.specpp.prom.mvc.swing;

import com.fluxicon.slickerbox.factory.SlickerFactory;

import javax.swing.*;

public class LabeledComboBox<T> extends HorizontalJPanel {

    private final JComboBox<T> comboBox;

    public LabeledComboBox(String label, T[] values) {
        add(SlickerFactory.instance().createLabel(label));
        comboBox = SlickerFactory.instance().createComboBox(values);
        comboBox.setRenderer(SwingFactory.getMyListCellRenderer());

        add(comboBox);
    }

    public JComboBox<T> getComboBox() {
        return comboBox;
    }
}
