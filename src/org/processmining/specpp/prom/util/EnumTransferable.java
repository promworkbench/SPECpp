package org.processmining.specpp.prom.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class EnumTransferable<E extends Enum<E>> implements Transferable {

    private final E data;

    public EnumTransferable(E data) {
        this.data = data;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{myFlave};
    }

    public static DataFlavor myFlave = new DataFlavor(Enum.class, "false");


    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(myFlave);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return data;
    }

}
