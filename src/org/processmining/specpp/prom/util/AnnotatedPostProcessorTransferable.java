package org.processmining.specpp.prom.util;

import org.processmining.specpp.prom.alg.FrameworkBridge;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class AnnotatedPostProcessorTransferable implements Transferable {

    private final FrameworkBridge.AnnotatedPostProcessor app;

    public AnnotatedPostProcessorTransferable(FrameworkBridge.AnnotatedPostProcessor app) {
        this.app = app;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{myFlave};
    }

    public static DataFlavor myFlave = new DataFlavor(FrameworkBridge.AnnotatedPostProcessor.class, "false");


    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(myFlave);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return app;
    }

}
