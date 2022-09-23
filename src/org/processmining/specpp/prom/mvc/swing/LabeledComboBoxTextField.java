package org.processmining.specpp.prom.mvc.swing;

import com.fluxicon.slickerbox.factory.SlickerFactory;

import javax.swing.*;

public class LabeledComboBoxTextField<T> extends LabeledTextField {

    protected final JComboBox<T> comboBox;

    public LabeledComboBoxTextField(String label, T[] values, int inputTextColumns) {
        super(new JTextField(inputTextColumns));
        add(SlickerFactory.instance().createLabel(label));
        comboBox = SlickerFactory.instance().createComboBox(values);
        comboBox.setRenderer(SwingFactory.getMyListCellRenderer());
        addSpaced(comboBox);
        addSpaced(field);
    }

    public JComboBox<T> getComboBox() {
        return comboBox;
    }

}
