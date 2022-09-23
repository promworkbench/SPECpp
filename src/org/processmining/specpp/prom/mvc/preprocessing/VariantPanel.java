package org.processmining.specpp.prom.mvc.preprocessing;

import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.impls.IndexedVariant;
import org.processmining.specpp.prom.mvc.swing.SwingFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Iterator;

public class VariantPanel extends JPanel {

    private final DefaultTableModel tableModel;

    public VariantPanel() {
        super(new BorderLayout());
        tableModel = new DefaultTableModel(new String[]{"Variant ID", "Frequency", "Variant"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                    case 1:
                        return Integer.class;
                    case 2:
                        return String.class;
                    default:
                        return super.getColumnClass(columnIndex);
                }
            }
        };
        ProMTable proMTable = SwingFactory.proMTable(tableModel);
        proMTable.getColumnModel().getColumn(0).setMaxWidth(100);
        proMTable.getColumnModel().getColumn(1).setMaxWidth(200);
        proMTable.setAutoCreateRowSorter(true);
        add(proMTable, BorderLayout.CENTER);
    }


    public void updateLog(Log log) {
        SwingUtilities.invokeLater(() -> {
            int oldCount = tableModel.getRowCount();
            tableModel.setRowCount(0);
            Iterator<IndexedVariant> it = log.iterator();
            while (it.hasNext()) {
                IndexedVariant next = it.next();
                int i = next.getIndex();
                int f = log.getVariantFrequency(i);
                String variantString = next.getVariant().toString();
                tableModel.addRow(new Object[]{i, f, variantString});
            }
            tableModel.fireTableDataChanged();
        });

    }
}
