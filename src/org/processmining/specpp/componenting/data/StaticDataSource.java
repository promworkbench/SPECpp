package org.processmining.specpp.componenting.data;

public class StaticDataSource<T> implements DataSource<T> {

    private final T data;

    public StaticDataSource(T data) {
        this.data = data;
    }

    public static <T> DataSource<T> of(T data) {
        return new StaticDataSource<>(data);
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "StaticDataSource(" + data.getClass().getSimpleName() + ")";
    }


}
