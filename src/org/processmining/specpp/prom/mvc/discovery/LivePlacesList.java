package org.processmining.specpp.prom.mvc.discovery;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.prom.mvc.swing.SwingFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LivePlacesList implements LivePlacesVisualizer {

    private final DefaultTableModel tableModel;
    private final ProMTable table;
    private final JPanel canvas;
    private final JLabel label;

    public LivePlacesList() {
        canvas = new JPanel(new BorderLayout());
        tableModel = SwingFactory.readOnlyTableModel("Preset", "Postset");
        table = new ProMTable(tableModel);
        label = SlickerFactory.instance().createLabel("");
        canvas.add(label, BorderLayout.PAGE_START);
        canvas.add(table, BorderLayout.CENTER);
    }

    @Override
    public void update(List<Place> places) {
        tableModel.setRowCount(0);
        for (Place place : places) {
            tableModel.addRow(new Object[]{place.preset().toString(), place.postset().toString()});
        }
        SwingUtilities.invokeLater(() -> label.setText(String.format("Size: %d", places.size())));
        tableModel.fireTableDataChanged();
    }

    @Override
    public JComponent getComponent() {
        return canvas;
    }


}
