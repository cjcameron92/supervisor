package gg.supervisor.core.util;

import java.util.HashMap;
import java.util.Map;

public class Services {

    private static final Map<Class<?>, Object> registeredServices = new HashMap<>();

    public static void register(Class<?> clazz, Object type) {
        registeredServices.put(clazz, type);
    }

    public static <T> T loadIfPresent(Class<T> clazz) {
        return (T) registeredServices.get(clazz);
    }

    public static Map<Class<?>, Object> getRegisteredServices() {
        return registeredServices;
    }
}
