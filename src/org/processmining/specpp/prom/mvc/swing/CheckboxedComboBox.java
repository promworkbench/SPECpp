package org.processmining.specpp.prom.mvc.swing;

import com.fluxicon.slickerbox.factory.SlickerFactory;

import javax.swing.*;
import java.awt.*;

public class CheckboxedComboBox<T> extends HorizontalJPanel {

    protected final JComboBox<T> comboBox;
    protected final JCheckBox checkBox;

    public CheckboxedComboBox(String label, boolean enabledByDefault, T[] values) {
        checkBox = SlickerFactory.instance().createCheckBox(label, enabledByDefault);
        checkBox.addActionListener(e -> updateTextFieldState());
        add(checkBox);
        comboBox = SlickerFactory.instance().createComboBox(values);
        comboBox.setMinimumSize(new Dimension(135, 25));
        comboBox.setPreferredSize(new Dimension(135, 25));
        comboBox.setRenderer(SwingFactory.getMyListCellRenderer());
        addSpaced(comboBox);
        updateTextFieldState();
    }

    private void updateTextFieldState() {
        comboBox.setVisible(checkBox.isEnabled());
    }

    public JCheckBox getCheckBox() {
        return checkBox;
    }

    public JComboBox<T> getComboBox() {
        return comboBox;
    }

}
