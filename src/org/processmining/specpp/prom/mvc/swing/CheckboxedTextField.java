package org.processmining.specpp.prom.mvc.swing;

import com.fluxicon.slickerbox.factory.SlickerFactory;

import javax.swing.*;

public class CheckboxedTextField extends HorizontalJPanel {

    protected final JTextField field;
    protected final JCheckBox checkBox;

    public CheckboxedTextField(String label, boolean enabledByDefault, int inputTextColumns) {
        checkBox = SlickerFactory.instance().createCheckBox(label, enabledByDefault);
        checkBox.addActionListener(e -> updateTextFieldState());
        add(checkBox);
        field = new JTextField(inputTextColumns);
        addSpaced(field);
        updateTextFieldState();
    }

    private void updateTextFieldState() {
        field.setEnabled(checkBox.isEnabled());
    }

    public JCheckBox getCheckBox() {
        return checkBox;
    }

    public JTextField getTextField() {
        return field;
    }

    public String getText() {
        return checkBox.isEnabled() ? field.getText() : null;
    }


}
