package gg.supervisor.core.util;

import gg.supervisor.core.annotation.Component;
import gg.supervisor.core.loader.SupervisorLoader;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code Services} class is a utility for managing and accessing service instances across the system.
 * It acts as a central service registry, storing various service components and providing methods to
 * register, retrieve, and dynamically load service instances.
 *
 * <p>This utility is particularly useful for simplifying dependency injection and reducing the need
 * for manually creating instances throughout the codebase. It provides an interface to access
 * already registered components and also supports lazy loading of components as needed.</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *     <li><strong>Service Registration:</strong> Allows services to be registered with the central registry
 *     so they can be accessed anywhere within the system.</li>
 *     <li><strong>Lazy Loading:</strong> Dynamically loads components annotated with {@code @Component} if they
 *     are not already registered in the system.</li>
 *     <li><strong>Central Service Access:</strong> Facilitates consistent access to services, ensuring proper
 *     initialization and minimizing errors related to manual instantiation.</li>
 * </ul>
 */
public class Services {

    // Central registry for storing instances of services, accessible by their class type
    private static final Map<Class<?>, Object> registeredServices = new HashMap<>();

    /**
     * Registers a service instance with the central registry.
     * This allows the service to be accessed anywhere by its class type.
     *
     * @param clazz The class type of the service.
     * @param type  The instance of the service to be registered.
     */
    public static void register(Class<?> clazz, Object type) {
        registeredServices.put(clazz, type);
    }

    /**
     * Retrieves a registered service instance by its class type.
     *
     * @param clazz The class type of the service to retrieve.
     * @param <T>   The type of the service.
     * @return The registered instance of the service, or {@code null} if no instance has been registered.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> clazz) {
        return (T) registeredServices.get(clazz);
    }

    /**
     * Loads a service instance if it is present in the registry or attempts to create and register it if the
     * component is annotated with {@code @Component} but has not yet been loaded.
     *
     * <p>If the service instance does not already exist and is annotated with {@code @Component}, this method
     * attempts to create the instance using the {@code SupervisorLoader} class, registering it in the process.</p>
     *
     * @param clazz The class type of the service to load.
     * @param <T>   The type of the service.
     * @return The loaded instance of the service, or {@code null} if it cannot be created or found.
     */
    @SuppressWarnings("unchecked")
    public static <T> T loadIfPresent(Class<T> clazz) {
        // Retrieve the instance from the registered services
        T instance = (T) registeredServices.get(clazz);

        // If the instance is not already present and is marked with @Component, try to create it
        if (instance == null && clazz.isAnnotationPresent(Component.class)) {
            try {
                // Use reflection to call the createComponentInstance method in SupervisorLoader
                Method method = SupervisorLoader.class.getDeclaredMethod("createComponentInstance", Class.class, Plugin.class);
                method.setAccessible(true);

                // Create and return the service instance, registering it in the process
                return (T) method.invoke(SupervisorLoader.class, clazz, (Plugin) registeredServices.get(Plugin.class));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException("Failed to load component: " + clazz.getName(), e);
            }
        }
        return instance;
    }

    /**
     * Provides a map of all currently registered services.
     *
     * @return A {@code Map} where each key is a {@code Class<?>} representing a service class, and each value is the service instance.
     */
    public static Map<Class<?>, Object> getRegisteredServices() {
        return registeredServices;
    }
}
