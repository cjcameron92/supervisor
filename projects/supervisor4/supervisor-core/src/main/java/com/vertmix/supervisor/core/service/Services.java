package com.vertmix.supervisor.core.service;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class Services {

    private static final Map<Class<?>, Function<?, ?>> factories = new HashMap<>();
    private static final Map<Class<?>, Object> services = new HashMap<>();

    private static final Set<Consumer<Object>> consumers = new HashSet<>();


    // Run the registered consumer for the given type
    public static <T> void register(Class<T> clazz, Function<Class<T>, T> function) {
        factories.put(clazz, function);
    }

    // Register a consumer for a specific type
    public static <T> void registerConsumer(Consumer<Object> consumer) {
        consumers.add(consumer);
    }

    public static void runConsumer(Object type) {
        consumers.forEach(consumer -> consumer.accept(type));
    }



    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz) {
        // Check if the service is already created and cached
        if (services.containsKey(clazz)) {
            return (T) services.get(clazz);
        }

        if (factories.containsKey(clazz.getInterfaces()[0])) {
            Function<Class<T>, T> factory = (Function<Class<T>, T>) factories.get(clazz.getInterfaces()[0]);
            T instance = factory.apply(clazz);
            // Cache the created instance for future use
            services.put(clazz, instance);
            return instance;
        } else if (factories.containsKey(clazz)) {
            // Retrieve the factory function and use it to create a new instance
            Function<Class<T>, T> factory = (Function<Class<T>, T>) factories.get(clazz);
            T instance = factory.apply(clazz);

            // Cache the created instance for future use
            services.put(clazz, instance);
            return instance;
        }

        // If no factory is registered, return null
        return null;
    }

    public static Map<Class<?>, Function<?, ?>> getFactories() {
        return factories;
    }

    /**
     * Registers a service instance with the central registry.
     * This allows the service to be accessed anywhere by its class type.
     *
     * @param clazz The class type of the service.
     * @param type  The instance of the service to be registered.
     */
    public static void register(Class<?> clazz, Object type) {
        services.put(clazz, type);
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
        return (T) services.get(clazz);
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
        T instance = (T) services.get(clazz);

        return instance;
    }

    /**
     * Provides a map of all currently registered services.
     *
     * @return A {@code Map} where each key is a {@code Class<?>} representing a service class, and each value is the service instance.
     */
    public static Map<Class<?>, Object> getServices() {
        return services;
    }



}
