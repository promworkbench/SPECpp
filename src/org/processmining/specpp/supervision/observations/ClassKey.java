package org.processmining.specpp.supervision.observations;

public class ClassKey<T> extends StaticHashWrapper<Class<? extends T>> {
    public ClassKey(Class<? extends T> internal) {
        super(internal);
    }

    public static <T> ClassKey<? extends T> ofObj(T obj) {
        return new ClassKey<>((Class<? extends T>) obj.getClass());
    }

    @Override
    public String toString() {
        return "Class(" + internal.getSimpleName() + ")";
    }
}
