package org.processmining.specpp.util;

public class JavaTypingUtils {

    @SuppressWarnings("unchecked")
    public static <T> Class<T> castClass(Class<?> aClass) {
        return (Class<T>) aClass;
    }


}
