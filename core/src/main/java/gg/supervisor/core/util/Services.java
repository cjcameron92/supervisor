package gg.supervisor.core.util;

import gg.supervisor.core.annotation.Component;
import gg.supervisor.core.loader.SupervisorLoader;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Services {

    private static final Map<Class<?>, Object> registeredServices = new HashMap<>();

    public static void register(Class<?> clazz, Object type) {
        registeredServices.put(clazz, type);
    }

    public static <T> T getService(Class<T> clazz) {
        return (T) registeredServices.get(clazz);
    }

    public static <T> T loadIfPresent(Class<T> clazz) {

        T instance = (T) registeredServices.get(clazz);

        if (instance == null && clazz.isAnnotationPresent(Component.class)) {
            try {
                Method method = SupervisorLoader.class.getDeclaredMethod("createComponentInstance", Class.class, Plugin.class);
                method.setAccessible(true);
                return (T) method.invoke(SupervisorLoader.class, clazz, (Plugin) registeredServices.get(Plugin.class));

            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public static Map<Class<?>, Object> getRegisteredServices() {
        return registeredServices;
    }
}
