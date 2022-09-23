package org.processmining.specpp.prom.mvc.swing;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;

public class TextBasedInputField<T> extends LabeledTextField {
    private final Function<String, T> parseInput;
    private final InputVerifier iv;
    public static final Color HIGHLIGHT_COLOR = new Color(ColorSpace.getInstance(ColorSpace.CS_sRGB), Color.red.getColorComponents(new float[3]), 0.3f);
    private final Border ogBorder;

    public TextBasedInputField(String label, Function<String, T> parseInput, int inputTextColumns) {
        super(label, inputTextColumns);
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
                if (permittedToBeWrong()) return true;
                T t = tryParse();
                return t != null;
            }
        };

        field.setInputVerifier(iv);
        HashSet<AWTKeyStroke> awtKeyStrokes = new HashSet<>(KeyboardFocusManager.getCurrentKeyboardFocusManager()
                                                                                .getDefaultFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        awtKeyStrokes.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        field.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, awtKeyStrokes);

        showVerificationStatus();
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

    protected T tryParse() {
        try {
            return parseInput.apply(field.getText());
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        showVerificationStatus();
    }

    protected boolean permittedToBeWrong() {
        return !isVisible() || !isEnabled();
    }

    public T getInput() {
        //SwingUtilities.invokeLater(this::showVerificationStatus);
        return tryParse();
    }

    public void setText(String text) {
        SwingUtilities.invokeLater(() -> {
            field.setText(text);
            showVerificationStatus();
        });
    }
}
