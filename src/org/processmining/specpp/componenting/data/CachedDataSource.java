package org.processmining.specpp.componenting.data;

public class CachedDataSource<T> implements DataSource<T> {

    private final DataSource<T> supplier;
    private T internal;

    public CachedDataSource(DataSource<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> CachedDataSource<T> of(DataSource<T> supplier) {
        return new CachedDataSource<>(supplier);
    }

    @Override
    public T getData() {
        if (internal == null) internal = supplier.getData();
        return internal;
    }

    public void clear() {
        internal = null;
    }

}
