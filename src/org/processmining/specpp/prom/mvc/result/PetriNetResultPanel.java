package org.processmining.specpp.prom.mvc.result;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.google.common.collect.ImmutableMultimap;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.specpp.base.Evaluator;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.util.ImmutableTuple2;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.datastructures.vectorization.IntVector;
import org.processmining.specpp.evaluation.fitness.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.DetailedFitnessEvaluation;
import org.processmining.specpp.prom.mvc.discovery.LivePlacesGraph;
import org.processmining.specpp.prom.mvc.swing.HorizontalJPanel;
import org.processmining.specpp.prom.mvc.swing.SwingFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PetriNetResultPanel extends JSplitPane {


    private final DefaultTableModel tableModel;
    private final JLabel infoLabel;
    private final IntVector variantFrequencies;
    private final boolean addOriginalIdColumn;
    private final ImmutableMultimap<Class<?>, Integer> columnTypeMap;

    public PetriNetResultPanel(CollectionOfPlaces collectionOfPlaces, Evaluator<Place, DetailedFitnessEvaluation> evaluator, IntVector variantFrequencies, boolean addOriginalIdColumn) {
        super(JSplitPane.HORIZONTAL_SPLIT);
        this.variantFrequencies = variantFrequencies;
        this.addOriginalIdColumn = addOriginalIdColumn;

        JPanel left = new JPanel(new BorderLayout());
        left.add(Box.createHorizontalStrut(400), BorderLayout.PAGE_END);
        setLeftComponent(left);
        String overfedSymbol = "\u25B3(L)";
        String underfedSymbol = "\u25BD(L)";
        String fittingSymbol = "\u25A1(L)";
        String rel = "_rel";
        String[] columnNames;
        if (addOriginalIdColumn) {
            columnNames = new String[]{"Original Id", "Size", "Preset", "Postset", fittingSymbol, underfedSymbol, overfedSymbol, fittingSymbol + rel, underfedSymbol + rel, overfedSymbol + rel};
            columnTypeMap = ImmutableMultimap.<Class<?>, Integer>builder()
                                             .putAll(Integer.class, 0, 1)
                                             .putAll(String.class, 2, 3)
                                             .putAll(Double.class, 4, 5, 6, 7, 8, 9)
                                             .build();
        } else {
            columnNames = new String[]{"Size", "Preset", "Postset", fittingSymbol, underfedSymbol, overfedSymbol, fittingSymbol + rel, underfedSymbol + rel, overfedSymbol + rel};
            columnTypeMap = ImmutableMultimap.<Class<?>, Integer>builder()
                                             .put(Integer.class, 0)
                                             .putAll(String.class, 1, 2)
                                             .putAll(Double.class, 3, 4, 5, 6, 7, 8)
                                             .build();
        }
        tableModel = SwingFactory.readOnlyTableModel(columnNames, columnTypeMap);
        ProMTable proMTable = SwingFactory.proMTable(tableModel);
        for (Integer i : columnTypeMap.get(Integer.class)) {
            proMTable.getColumnModel().getColumn(i).setMaxWidth(50);
        }
        for (Integer i : columnTypeMap.get(Double.class)) {
            proMTable.getColumnModel().getColumn(i).setMaxWidth(80);
        }
        proMTable.setAutoCreateRowSorter(true);
        JPanel right = new JPanel(new BorderLayout());
        right.add(proMTable, BorderLayout.CENTER);
        HorizontalJPanel bottomLine = new HorizontalJPanel();
        bottomLine.add(SlickerFactory.instance().createLabel(String.format("Count: %d", collectionOfPlaces.size())));
        infoLabel = SlickerFactory.instance().createLabel("not yet computed");
        bottomLine.addSpaced(infoLabel);
        right.add(bottomLine, BorderLayout.PAGE_END);
        setRightComponent(right);


        new SwingWorker<JComponent, Void>() {

            @Override
            protected JComponent doInBackground() throws Exception {
                LivePlacesGraph graph = new LivePlacesGraph();
                graph.update(ProMPetrinetWrapper.of(collectionOfPlaces));
                return graph.getComponent();
            }

            @Override
            protected void done() {
                try {
                    JComponent jComponent = get();
                    if (!isCancelled()) left.add(jComponent, BorderLayout.CENTER);
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }.execute();

        new SwingWorker<List<Tuple2<Place, DetailedFitnessEvaluation>>, Tuple2<Place, DetailedFitnessEvaluation>>() {

            @Override
            protected List<Tuple2<Place, DetailedFitnessEvaluation>> doInBackground() throws Exception {
                return collectionOfPlaces.getPlaces()
                                         .stream()
                                         .map(p -> new ImmutableTuple2<>(p, evaluator.apply(p)))
                                         .collect(Collectors.toList());
            }

            @Override
            protected void done() {
                try {
                    List<Tuple2<Place, DetailedFitnessEvaluation>> map = get();
                    if (!isCancelled()) {
                        updateTable(map);
                    }
                } catch (InterruptedException | ExecutionException ignored) {
                }
            }
        }.execute();
    }

    private void updateTable(List<Tuple2<Place, DetailedFitnessEvaluation>> list) {
        if (list == null) {
            infoLabel.setText("computation failed");
            return;
        }
        tableModel.setRowCount(0);
        BitMask overallFittingVariants = null;
        for (int i = 0; i < list.size(); i++) {
            Place key = list.get(i).getT1();
            DetailedFitnessEvaluation value = list.get(i).getT2();
            BitMask fittingVariants = value.getFittingVariants();
            if (overallFittingVariants == null) overallFittingVariants = fittingVariants;
            else overallFittingVariants.intersection(fittingVariants);
            BasicFitnessEvaluation fractions = value.getFractionalEvaluation();
            Object[] rowData = createRow(i, key, fractions);
            tableModel.addRow(rowData);
        }
        double sum = overallFittingVariants == null ? Double.NaN : overallFittingVariants.stream()
                                                                                         .mapToDouble(variantFrequencies::getRelative)
                                                                                         .sum();
        infoLabel.setText(String.format("Combined Fitting Traces Fraction: %.2f", sum));
        tableModel.fireTableDataChanged();
    }

    private Object[] createRow(int id, Place key, BasicFitnessEvaluation fractions) {
        if (addOriginalIdColumn) {
            return new Object[]{id, key.size(), key.preset().toString(), key.postset().toString(), fractions.getFittingFraction(), fractions.getUnderfedFraction(), fractions.getOverfedFraction(), fractions.getRelativeFittingFraction(), fractions.getRelativeUnderfedFraction(), fractions.getRelativeOverfedFraction()};

        } else {
            return new Object[]{key.size(), key.preset().toString(), key.postset().toString(), fractions.getFittingFraction(), fractions.getUnderfedFraction(), fractions.getOverfedFraction(), fractions.getRelativeFittingFraction(), fractions.getRelativeUnderfedFraction(), fractions.getRelativeOverfedFraction()};

        }
    }
}
