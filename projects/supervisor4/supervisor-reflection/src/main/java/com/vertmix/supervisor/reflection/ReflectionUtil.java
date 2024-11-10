package com.vertmix.supervisor.reflection;

import com.vertmix.supervisor.core.annotation.ComponentConstructor;

import java.lang.reflect.Constructor;

public class ReflectionUtil {

    public static Constructor<?> getComponentConstructor(Class<?> clazz) {
        if (clazz.isInterface()) {
            return null;
        }

        Constructor<?>[] constructors = clazz.getConstructors();
        if (constructors.length == 0) {
            throw new IllegalStateException("No public constructors found for class: " + clazz.getName());
        }

        for (Constructor<?> clazzConstructor : constructors) {
            if (clazzConstructor.isAnnotationPresent(ComponentConstructor.class)) {
                return clazzConstructor;
            }
        }

        return constructors[0];
    }
}
