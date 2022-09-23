package org.processmining.specpp.prom.alg;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginDescriptor;
import org.processmining.framework.plugin.PluginExecutionResult;
import org.processmining.framework.plugin.PluginParameterBinding;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.util.Pair;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.prom.mvc.swing.SwingFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class ProMPostProcessor {


    public static Pair<Set<Pair<Integer, PluginParameterBinding>>, Set<Pair<Integer, PluginParameterBinding>>> searchPlugins(PluginContext pc) {
        Set<Pair<Integer, PluginParameterBinding>> pnTransformers = pc.getPluginManager()
                                                                      .find(Plugin.class, Petrinet.class, pc.getClass(), true, true, false, Petrinet.class);
        Set<Pair<Integer, PluginParameterBinding>> apnTransformers = pc.getPluginManager()
                                                                       .find(Plugin.class, AcceptingPetriNet.class, pc.getClass(), true, true, false, AcceptingPetriNet.class);

        return new Pair<>(pnTransformers, apnTransformers);
    }


    public static void createPluginFinderWindow(PluginContext pc, Consumer<FrameworkBridge.AnnotatedPostProcessor> consumer) {
        Pair<Set<Pair<Integer, PluginParameterBinding>>, Set<Pair<Integer, PluginParameterBinding>>> plugins = searchPlugins(pc);
        List<Pair<Integer, PluginParameterBinding>> pnTransformers = new ArrayList<>(plugins.getFirst());
        List<Pair<Integer, PluginParameterBinding>> apnTransformers = new ArrayList<>(plugins.getSecond());

        DefaultTableModel model = SwingFactory.readOnlyTableModel("Plugin Name", "Description", "Parameter Names", "Return Objects Names");
        ArrayList<Pair<Integer, PluginParameterBinding>> combined = new ArrayList<>(pnTransformers);
        combined.addAll(apnTransformers);
        for (Pair<Integer, PluginParameterBinding> pair : combined) {
            PluginDescriptor plugin = pair.getSecond().getPlugin();
            model.addRow(new Object[]{plugin.getName(), plugin.getHelp(), plugin.getParameterNames().toString(), plugin.getReturnNames().toString()});
        }

        JFrame jf = new JFrame("ProM (accepting) Petri net transforming plugin search");
        ProMTable table = SwingFactory.proMTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0 && e.getClickCount() == 2) {
                    int i = table.getRowSorter().convertRowIndexToModel(row);
                    handleImport(pc, consumer, jf, pnTransformers, apnTransformers, i);
                }
            }
        });
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        JTextField searchField = new JTextField(300);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            private void filter() {
                try {
                    RowFilter<Object, Object> rf = RowFilter.regexFilter(searchField.getText(), 0);
                    sorter.setRowFilter(rf);
                } catch (PatternSyntaxException ignored) {
                }
            }
        });
        jf.add(SlickerFactory.instance()
                             .createLabel(SwingFactory.html("<h4>Plugins loaded in ProM that transform (Accepting) Petri nets into (Accepting) Petri nets</h4>")), BorderLayout.PAGE_START);
        jf.add(table, BorderLayout.CENTER);
        JPanel bottomLine = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.gridx = 0;
        bottomLine.add(SlickerFactory.instance().createLabel("Search"), c);
        c.gridx++;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        bottomLine.add(searchField);
        c.weightx = 0;
        c.gridx++;
        JButton importButton = SlickerFactory.instance().createButton("import currently selected");
        importButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) handleImport(pc, consumer, jf, pnTransformers, apnTransformers, row);
        });
        c.fill = GridBagConstraints.NONE;
        bottomLine.add(importButton, c);

        jf.add(bottomLine, BorderLayout.PAGE_END);
        jf.setPreferredSize(new Dimension(650, 450));
        jf.pack();
        jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jf.setVisible(true);
    }

    private static void handleImport(PluginContext pc, Consumer<FrameworkBridge.AnnotatedPostProcessor> consumer, JFrame jf, List<Pair<Integer, PluginParameterBinding>> pnTransformers, List<Pair<Integer, PluginParameterBinding>> apnTransformers, int row) {
        FrameworkBridge.AnnotatedPostProcessor wrapPlugin = wrapPlugin(pc, pnTransformers, apnTransformers, row);
        if (wrapPlugin == null) return;
        JOptionPane.showMessageDialog(jf, "Successfully imported " + wrapPlugin + ".", "Import", JOptionPane.INFORMATION_MESSAGE);
        // jf.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        consumer.accept(wrapPlugin);
    }

    private static FrameworkBridge.AnnotatedPostProcessor wrapPlugin(PluginContext pc, List<Pair<Integer, PluginParameterBinding>> pnTransformers, List<Pair<Integer, PluginParameterBinding>> apnTransformers, int row) {


        PluginParameterBinding binding;
        BiFunction<PluginContext, ProMPetrinetWrapper, ProMPetrinetWrapper> invoker;

        if (row < pnTransformers.size()) {
            binding = pnTransformers.get(row).getSecond();
            invoker = (PluginContext cont, ProMPetrinetWrapper pnw) -> {
                PluginExecutionResult invoke = binding.invoke(cont, pnw);
                try {
                    invoke.synchronize();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Optional<Petrinet> first = Arrays.stream(invoke.getResults())
                                                 .filter(r -> r instanceof Petrinet)
                                                 .map(r -> (Petrinet) r)
                                                 .findFirst();
                if (!first.isPresent()) return null;
                Petrinet net = first.get();
                Collection<Place> places = net.getPlaces();
                Marking im = new Marking(pnw.getInitialMarking()
                                            .stream()
                                            .filter(places::contains)
                                            .collect(Collectors.toList()));
                Set<Marking> fms = pnw.getFinalMarkings()
                                      .stream()
                                      .map(Marking::stream)
                                      .map(s -> s.filter(places::contains))
                                      .map(s -> new Marking(s.collect(Collectors.toList())))
                                      .collect(Collectors.toSet());
                return ProMPetrinetWrapper.of(net, im, fms);
            };
        } else if (row - pnTransformers.size() < apnTransformers.size()) {
            binding = apnTransformers.get(row - pnTransformers.size()).getSecond();
            invoker = (PluginContext cont, ProMPetrinetWrapper pnw) -> {
                PluginExecutionResult invoke = binding.invoke(cont, pnw);
                try {
                    invoke.synchronize();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Optional<AcceptingPetriNet> first = Arrays.stream(invoke.getResults())
                                                          .filter(r -> r instanceof AcceptingPetriNet)
                                                          .map(r -> (AcceptingPetriNet) r)
                                                          .findFirst();
                if (!first.isPresent()) return null;
                AcceptingPetriNet apn = first.get();
                return new ProMPetrinetWrapper(apn);
            };
        } else return null;

        PostProcessor<ProMPetrinetWrapper, ProMPetrinetWrapper> pp = new PostProcessor<ProMPetrinetWrapper, ProMPetrinetWrapper>() {

            @Override
            public ProMPetrinetWrapper postProcess(ProMPetrinetWrapper result) {
                PluginContext childContext = pc.createChildContext("post processing child context");
                return invoker.apply(childContext, result);
            }

            @Override
            public Class<ProMPetrinetWrapper> getInputClass() {
                return ProMPetrinetWrapper.class;
            }

            @Override
            public Class<ProMPetrinetWrapper> getOutputClass() {
                return ProMPetrinetWrapper.class;
            }
        };

        return new FrameworkBridge.AnnotatedPostProcessor(binding.getPlugin()
                                                                 .getName(), ProMPetrinetWrapper.class, ProMPetrinetWrapper.class, () -> (() -> pp));
    }

    private static void print(List<Pair<Integer, PluginParameterBinding>> pairs) {
        System.out.println("#Results: " + pairs.size());
        for (Pair<Integer, PluginParameterBinding> pair : pairs) {
            PluginDescriptor plugin = pair.getSecond().getPlugin();
            String name = plugin.getName();
            String desc = plugin.getHelp();
            System.out.println(String.format("\t[%d] ", pair.getFirst()) + name + " :: " + desc + " - " + plugin.getParameterNames() + " => " + plugin.getReturnNames());
        }
    }

}
