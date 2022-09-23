package org.processmining.specpp.prom.mvc.swing;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;

public class ActivatableTextBasedInputField<T> extends CheckboxedTextField {
    private final Function<String, T> parseInput;
    private final InputVerifier iv;
    private final Border ogBorder;
    private boolean isActivated;

    public ActivatableTextBasedInputField(String label, Function<String, T> parseInput, boolean activatedByDefault, int inputTextColumns) {
        super(label, activatedByDefault, inputTextColumns);
        this.listeners = new LinkedList<>();
        this.parseInput = parseInput;

        ogBorder = field.getBorder();

        iv = new InputVerifier() {
            @Override
            public boolean shouldYieldFocus(JComponent input) {
                showVerificationStatus();
                return true;
            }

            @Override
            public boolean verify(JComponent input) {
                if (permittedToBeInvalid()) return true;
                T t = tryParse();
                return t != null;
            }
        };

        field.setInputVerifier(iv);
        HashSet<AWTKeyStroke> awtKeyStrokes = new HashSet<>(KeyboardFocusManager.getCurrentKeyboardFocusManager()
                                                                                .getDefaultFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        awtKeyStrokes.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        field.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, awtKeyStrokes);

        checkBox.addChangeListener(c -> setInternalActivationStatus(checkBox.isSelected()));
        if (isActivated) showVerificationStatus();
    }

    protected java.util.List<Consumer<Boolean>> listeners;

    public void addVerificationStatusListener(Consumer<Boolean> bc) {
        listeners.add(bc);
    }

    public void showVerificationStatus() {
        boolean verified = iv.verify(field);
        field.setBorder(verified ? ogBorder : BorderFactory.createLineBorder(Color.red, 2, false));
        listeners.forEach(bc -> bc.accept(verified));
    }

    private boolean permittedToBeInvalid() {
        return !isActivated;
    }

    protected T tryParse() {
        try {
            return parseInput.apply(field.getText());
        } catch (Exception ignored) {
            return null;
        }
    }

    public T getInput() {
        //SwingUtilities.invokeLater(this::showVerificationStatus);
        return isActivated ? tryParse() : null;
    }

    public void setText(String text) {
        SwingUtilities.invokeLater(() -> {
            field.setText(text);
            showVerificationStatus();
        });
    }

    private void setInternalActivationStatus(boolean newState) {
        isActivated = newState;
        showVerificationStatus();
    }

    public void activate() {
        SwingUtilities.invokeLater(() -> checkBox.setSelected(true));
    }

    public void deactivate() {
        SwingUtilities.invokeLater(() -> checkBox.setSelected(false));
    }

}
