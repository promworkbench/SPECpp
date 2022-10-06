package org.processmining.specpp.prom.mvc.config;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.ui.widgets.ProMList;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.prom.alg.FrameworkBridge;
import org.processmining.specpp.prom.alg.ProMPostProcessor;
import org.processmining.specpp.prom.mvc.swing.HorizontalJPanel;
import org.processmining.specpp.prom.mvc.swing.MyListModel;
import org.processmining.specpp.prom.mvc.swing.SwingFactory;
import org.processmining.specpp.prom.util.AnnotatedPostProcessorTransferable;
import org.processmining.specpp.prom.util.Iconic;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostProcessingConfigPanel extends JPanel {

    private final MyListModel<FrameworkBridge.AnnotatedPostProcessor> availablePostProcessorsListModel;
    private final DefaultTableModel tableModel;
    private final List<FrameworkBridge.AnnotatedPostProcessor> availablePostProcessors;
    private static final String DRAG_DROP_HELP_TEXT = "use drag & drop to add, remove and reorder the desired post processing steps";
    private static final int width = 475;
    private static final int height = 225;

    public PostProcessingConfigPanel(PluginContext pc, MyListModel<FrameworkBridge.AnnotatedPostProcessor> ppPipelineModel) {
        super(new GridBagLayout());

        availablePostProcessors = new ArrayList<>();
        availablePostProcessorsListModel = new MyListModel<>();
        tableModel = SwingFactory.readOnlyTableModel("Input", "Output", "Name");
        FrameworkBridge.POST_PROCESSORS.forEach(this::addAvailablePostProcessor);

        JList<FrameworkBridge.AnnotatedPostProcessor> outList = new JList<>(availablePostProcessorsListModel);
        ProMTable proMTable = SwingFactory.proMTable(tableModel);
        proMTable.setColumnSelectionAllowed(false);
        proMTable.getColumnModel().getColumn(0).setMaxWidth(175);
        proMTable.getColumnModel().getColumn(1).setMaxWidth(175);
        proMTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proMTable.getTable().setTransferHandler(new TransferHandler() {
            @Override
            protected Transferable createTransferable(JComponent c) {
                int i = proMTable.getSelectedRow();
                if (i < 0) return null;
                return new AnnotatedPostProcessorTransferable(availablePostProcessors.get(i));
            }

            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDrop() && support.isDataFlavorSupported(AnnotatedPostProcessorTransferable.myFlave);
            }

            @Override
            public boolean importData(TransferSupport support) {
                return canImport(support);
            }

            @Override
            public int getSourceActions(JComponent c) {
                return COPY;
            }

        });
        proMTable.getTable().setDragEnabled(true);
        proMTable.getTable().setDropMode(DropMode.INSERT);

        GridBagConstraints ppc = new GridBagConstraints();
        ppc.insets = new Insets(10, 15, 10, 15);
        ppc.gridx = 0;
        ppc.gridy = 0;
        ppc.weightx = 1;
        ppc.weighty = 0;
        ppc.anchor = GridBagConstraints.WEST;
        JLabel leftHeaderLabel = SwingFactory.createHeader("Available Post Processors");
        HorizontalJPanel leftHeader = new HorizontalJPanel();
        leftHeader.add(leftHeaderLabel);
        leftHeader.add(SwingFactory.help(null, SwingFactory.html("The left table lists all available post processing step implementations.<br>The right list contains the currently configured post processing pipeline.<br>Use drag & drop to add, remove and reorder the postprocessing steps as desired.<br>The pipeline is executed in top-to-bottom, so all in & output types need to be compatible. A type check is displayed below the list.<br>A technical note on the types \"PetriNet\" and \"ProMPetrinetWrapper\": this plugin internally uses the former class for discovery and provides the latter for <it>theoretical</it> interoperability with arbitrary ProM plugins used as post processors.")));
        add(leftHeader, ppc);
        ppc.fill = GridBagConstraints.BOTH;
        ppc.weighty = 1;
        ppc.gridy++;
        proMTable.setMaximumSize(new Dimension(width, height));
        proMTable.setPreferredSize(new Dimension(width, height));
        add(proMTable, ppc);
        ppc.fill = GridBagConstraints.NONE;
        ProMList<FrameworkBridge.AnnotatedPostProcessor> proMList = new ProMList<>("Selected Post Processing Steps", ppPipelineModel);
        JList<FrameworkBridge.AnnotatedPostProcessor> inList = proMList.getList();
        inList.setDragEnabled(true);
        inList.setDropMode(DropMode.INSERT);
        inList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inList.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    int i = inList.getSelectedIndex();
                    if (i >= 0) ppPipelineModel.remove(i);
                }
            }

        });

        MouseAdapter ml = new MouseAdapter() {

            public void popIt(MouseEvent e) {
                int i = inList.locationToIndex(e.getPoint());
                if (i < 0) return;
                boolean b = inList.getCellBounds(i, i).contains(e.getPoint());
                if (b) {
                    JPopupMenu jpm = new JPopupMenu() {{
                        JMenuItem remove = new JMenuItem("remove");
                        remove.addActionListener(e -> ppPipelineModel.remove(i));
                        add(remove);
                    }};
                    jpm.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) popIt(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) popIt(e);
            }
        };
        inList.addMouseListener(ml);
        inList.setTransferHandler(new TransferHandler() {

            private int importedIndex = -1;
            private int exportedIndex = -1;

            @Override
            protected Transferable createTransferable(JComponent c) {
                Object selectedValue = inList.getSelectedValue();
                exportedIndex = inList.getSelectedIndex();
                return new AnnotatedPostProcessorTransferable((FrameworkBridge.AnnotatedPostProcessor) selectedValue);
            }

            @Override
            public int getSourceActions(JComponent c) {
                return MOVE;
            }

            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDrop() && support.isDataFlavorSupported(AnnotatedPostProcessorTransferable.myFlave);
            }

            @Override
            protected void exportDone(JComponent source, Transferable data, int action) {
                if (action == MOVE) {
                    if (importedIndex < 0) ppPipelineModel.remove(exportedIndex);
                    else ppPipelineModel.remove(importedIndex < exportedIndex ? exportedIndex + 1 : exportedIndex);
                    exportedIndex = -1;
                    importedIndex = -1;
                }
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    FrameworkBridge.AnnotatedPostProcessor transferData = (FrameworkBridge.AnnotatedPostProcessor) support.getTransferable()
                                                                                                                          .getTransferData(AnnotatedPostProcessorTransferable.myFlave);
                    JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
                    int index = dl.getIndex();
                    importedIndex = index;
                    ppPipelineModel.insert(transferData, index);
                    return true;
                } catch (UnsupportedFlavorException | IOException | ClassCastException ignored) {
                }
                return false;
            }

        });

        ppc.gridx = 1;
        ppc.gridy = 0;
        ppc.anchor = GridBagConstraints.CENTER;
        ppc.weightx = 0;
        ppc.weighty = 0;
        JLabel arrow = new JLabel(Iconic.tiny_right_arrow);
        arrow.setToolTipText(DRAG_DROP_HELP_TEXT);
        add(arrow, ppc);
        ppc.gridheight = 2;
        ppc.gridy++;
        JLabel hand = new JLabel(Iconic.tiny_hand);
        hand.setToolTipText(DRAG_DROP_HELP_TEXT);
        add(hand, ppc);
        ppc.weightx = 1;
        ppc.gridheight = 1;
        ppc.gridx = 2;
        ppc.gridy = 0;
        ppc.anchor = GridBagConstraints.WEST;
        add(SwingFactory.createHeader("Post Processing Pipeline"), ppc);
        ppc.weighty = 1;
        ppc.gridy++;
        ppc.fill = GridBagConstraints.BOTH;
        //JScrollPane inListScrollPane = new JScrollPane(inList);
        //inListScrollPane.setMaximumSize(new Dimension(500, 300));
        //inListScrollPane.setPreferredSize(new Dimension(500, 300));
        proMList.setMaximumSize(new Dimension(width, height));
        proMList.setPreferredSize(new Dimension(width, height));
        add(proMList, ppc);
        JButton importPostProcessorButton = SlickerFactory.instance().createButton("import from ProM");
        importPostProcessorButton.addActionListener(e -> ProMPostProcessor.createPluginFinderWindow(pc, this::importAnnotatedPostProcessor));
        ppc.fill = GridBagConstraints.NONE;
        ppc.weightx = 0;
        ppc.weighty = 0.1;
        ppc.gridy = 2;
        ppc.gridx = 0;
        ppc.gridwidth = 1;
        add(importPostProcessorButton, ppc);
        JLabel ppTypesOkay = SlickerFactory.instance().createLabel("are types ok?");
        ppc.gridx = 2;
        add(ppTypesOkay, ppc);
        ppPipelineModel.addListDataListener(new ListDataListener() {

            private void updateValidationStatus() {
                if (ConfigurationPanel.validatePostProcessingPipeline(ppPipelineModel)) {
                    ppTypesOkay.setText(String.format("input, intermediate & output types match [%s => %s]", CollectionOfPlaces.class.getSimpleName(), ProMPetrinetWrapper.class.getSimpleName()));
                    ppTypesOkay.setIcon(Iconic.checkmark);
                } else {
                    ppTypesOkay.setText(String.format("input & output types are incompatible [%s => %s]", CollectionOfPlaces.class.getSimpleName(), ProMPetrinetWrapper.class.getSimpleName()));
                    ppTypesOkay.setIcon(Iconic.red_cross);
                }
            }

            @Override
            public void intervalAdded(ListDataEvent e) {
                updateValidationStatus();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                updateValidationStatus();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                updateValidationStatus();
            }
        });

    }

    public void importAnnotatedPostProcessor(FrameworkBridge.AnnotatedPostProcessor annotatedPostProcessor) {
        if (annotatedPostProcessor == null) return;
        addAvailablePostProcessor(annotatedPostProcessor);
    }

    private void addAvailablePostProcessor(FrameworkBridge.AnnotatedPostProcessor annotatedPostProcessor) {
        availablePostProcessors.add(annotatedPostProcessor);
        availablePostProcessorsListModel.append(annotatedPostProcessor);
        tableModel.addRow(new Object[]{annotatedPostProcessor.getInType().getSimpleName(), annotatedPostProcessor.getOutType().getSimpleName(), annotatedPostProcessor.getPrintableName()});
    }

}
