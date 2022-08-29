package org.processmining.specpp.util;

import com.google.common.collect.Streams;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

@SuppressWarnings("unchecked")
public class Reflection {

    public static <T> Constructor<?> suitableConstructor(Class<T> aClass, Object... args) {
        for (Constructor<?> constructor : aClass.getConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == args.length) {
                if (Streams.zip(Arrays.stream(parameterTypes), Arrays.stream(args)
                                                                     .map(Object::getClass), Class::isAssignableFrom)
                           .allMatch(b -> b)) return constructor;
            }
        }
        return null;
    }

    public static <T> T instance(Class<T> aClass, Object... args) {
        try {
            Constructor<?> constructor = suitableConstructor(aClass, args);
            if (constructor == null) throw new NoSuitableConstructorException();
            return (T) constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T instance(Class<T> aClass) {
        try {
            return aClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static class NoSuitableConstructorException extends RuntimeException {
    }
}
