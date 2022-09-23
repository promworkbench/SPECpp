package org.processmining.specpp.prom.mvc.swing;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.google.common.collect.Multimap;
import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.specpp.prom.mvc.config.ProMConfig;
import org.processmining.specpp.prom.util.Iconic;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class SwingFactory {

    public static <T> JComboBox<T> comboBox(T[] values) {
        return (JComboBox<T>) SlickerFactory.instance().createComboBox(values);
    }

    public static <T> ProMComboBox<T> promComboBox(T[] values) {
        return new ProMComboBox<>(values);
    }

    public static <T> LabeledComboBox<T> labeledComboBox(String label, T[] values) {
        return new LabeledComboBox<>(label, values);
    }

    public static JButton help(String hint, String contextIndependentText) {
        return help(hint, () -> contextIndependentText);
    }

    public static JButton help(String hint, Supplier<String> contextDependentText) {
        JButton button = new JButton(Iconic.tiny_circled_questionmark);
        button.setRolloverIcon(Iconic.tiny_circled_questionmark_hovered);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setMaximumSize(new Dimension(25, 25));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setToolTipText(hint == null ? "click me" : hint);
        button.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, contextDependentText.get(), "Help", JOptionPane.INFORMATION_MESSAGE);
        });
        return button;
    }

    public static LabeledTextField labeledTextField(String label, int inputTextColumns) {
        return new LabeledTextField(label, inputTextColumns);
    }

    public static <T, K> ComboBoxAndTextBasedInputField<T, K> comboBoxAndTextBasedInputField(String label, K[] values, Function<String, T> parseInput, int inputTextColumns) {
        return new ComboBoxAndTextBasedInputField<>(label, values, parseInput, inputTextColumns);
    }

    public static <T> TextBasedInputField<T> textBasedInputField(String label, Function<String, T> parseInput, int inputTextColumns) {
        return new TextBasedInputField<>(label, parseInput, inputTextColumns);
    }

    public static <T> ActivatableTextBasedInputField<T> activatableTextBasedInputField(String label, boolean activatedByDefault, Function<String, T> parseInput, int textInputColumns) {
        return new ActivatableTextBasedInputField<>(label, parseInput, activatedByDefault, textInputColumns);
    }

    public static <T> CheckboxedComboBox<T> checkboxedComboBox(String label, boolean enabledByDefault, T[] values) {
        return new CheckboxedComboBox<>(label, enabledByDefault, values);
    }

    public static CheckboxedTextField checkboxedTextField(String label, boolean enabledByDefault, int inputTextColumns) {
        return new CheckboxedTextField(label, enabledByDefault, inputTextColumns);
    }

    public static JCheckBox labeledCheckBox(String label) {
        return labeledCheckBox(label, false);
    }

    public static JCheckBox labeledCheckBox(String label, boolean checked) {
        return SlickerFactory.instance().createCheckBox(label, checked);
    }

    public static ProMTable proMTable(TableModel tableModel) {
        return new ProMTable(tableModel) {

            @Override
            protected JTable createTable(TableModel model, TableColumnModel columnModel) {
                return new JTable(model, columnModel) {
                    @Override
                    public String getToolTipText(MouseEvent event) {
                        String tip = null;
                        java.awt.Point p = event.getPoint();
                        int rowIndex = rowAtPoint(p);
                        int colIndex = columnAtPoint(p);

                        if (rowIndex < 0 || colIndex < 0) return null;

                        try {
                            tip = getValueAt(rowIndex, colIndex).toString();
                        } catch (RuntimeException ignored) {
                        }

                        return tip;
                    }
                };
            }

        };
    }

    public static JLabel createHeader(String s) {
        return SlickerFactory.instance().createLabel("<html><h3>" + s + "</h3></html>");
    }

    public static JLabel createHeaderWithSubtitle(String s, String sub) {
        return SlickerFactory.instance().createLabel("<html><h3>" + s + "</h3><br>" + sub + "</html>");
    }

    public static DefaultTableModel readOnlyTableModel(String... columnNames) {
        return new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }


        };
    }

    public static DefaultTableModel readOnlyTableModel(String[] columnNames, Multimap<Class<?>, Integer> types) {
        Class<?>[] columnClasses = new Class[columnNames.length];
        Arrays.fill(columnClasses, Object.class);
        types.forEach((c, i) -> columnClasses[i] = c);
        return new DefaultTableModel(columnNames, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnClasses[columnIndex];
            }
        };
    }

    public static String html(String s) {
        return "<html>" + s + "</html>";
    }

    public static DefaultListCellRenderer getMyListCellRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel comp = ((JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus));
                if (value instanceof ProMConfig.DisplayableEnum) {
                    ProMConfig.DisplayableEnum displayableEnum = (ProMConfig.DisplayableEnum) value;
                    comp.setText(displayableEnum.getDisplayText());
                    comp.setToolTipText(displayableEnum.getDescription());
                } else comp.setToolTipText(Objects.toString(value));
                return comp;
            }
        };
    }

    public static void resizeComboBox(JComboBox<?> heuristicComboBox, int width, int height) {
        heuristicComboBox.setMinimumSize(new Dimension(width, height));
        heuristicComboBox.setPreferredSize(new Dimension(width, height));
    }
}
