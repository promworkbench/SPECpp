package org.processmining.specpp.prom.mvc.swing;

import com.fluxicon.slickerbox.factory.SlickerFactory;

import javax.swing.*;

public class LabeledTextField extends HorizontalJPanel {

    protected final JTextField field;

    public LabeledTextField(String label, int inputTextColumns) {
        add(SlickerFactory.instance().createLabel(label));
        field = new JTextField(inputTextColumns);
        addSpaced(field);
    }

    public LabeledTextField(JTextField field) {
        this.field = field;
    }

    public JTextField getTextField() {
        return field;
    }

    public String getText() {
        return field.getText();
    }

}
