package com.vertmix.supervisor.configuration;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Optional;

/**
 * The {@code ConfigService} interface provides essential methods for managing configurations
 * across the application. It facilitates operations such as registering, saving, loading, and
 * reloading configuration files. This interface abstracts away the complexities involved in
 * handling configuration files, allowing easy integration into various parts of the application.
 *
 * <p>Configuration management is vital for maintaining flexible settings, customizable
 * properties, and ensuring consistency across different environments. {@code ConfigService}
 * is intended for enterprise-level applications where a central configuration service is needed
 * to maintain numerous configurable components effectively.</p>
 *
 * <p><strong>Key Responsibilities:</strong></p>
 * <ul>
 *     <li>Manage configuration file extensions, registration, persistence, and loading operations.</li>
 *     <li>Allow developers to register configuration classes, save them to files, or load them as needed.</li>
 *     <li>Facilitate reloading operations, especially useful when configuration files are modified externally.</li>
 * </ul>
 *
 * <p><strong>Typical Use Cases:</strong></p>
 * <ul>
 *     <li>Registering configuration instances for runtime updates and persistence.</li>
 *     <li>Saving the current state of configurations to disk to ensure changes are maintained between restarts.</li>
 *     <li>Loading configurations during application startup to initialize settings correctly.</li>
 *     <li>Reloading configuration values when an update is made externally to reflect the latest changes.</li>
 * </ul>
 */
public interface ConfigService {

    /**
     * Gets the file extension used by this configuration service.
     * <p>
     * This method is useful for ensuring the correct file format is used when saving or loading
     * configurations. The extension might differ depending on the implementation (e.g., ".json", ".yaml").
     * </p>
     *
     * @return The file extension, including the dot prefix (e.g., ".json").
     */
    String getExtension();

    /**
     * Registers a configuration instance with the service and associates it with a file.
     * <p>
     * The registered instance is tracked by the configuration service and is persisted to the
     * specified file. This is typically done during initialization to ensure any subsequent changes
     * to the instance are saved correctly.
     * </p>
     *
     * @param clazz    The class type of the configuration to be registered.
     * @param instance The instance of the configuration to be managed.
     * @param file     The file where the configuration will be persisted.
     * @param <Type>   The type of the configuration.
     * @return The registered instance for chaining operations if needed.
     */
    <Type> Type register(Class<Type> clazz, Type instance, File file);

    /**
     * Saves the given configuration object to the specified file.
     * <p>
     * This method persists the current state of the provided configuration object to disk. It should
     * be used whenever changes are made that need to be saved, ensuring that the latest values are available
     * for future loads.
     * </p>
     *
     * @param obj  The configuration object to be saved.
     * @param file The file where the configuration should be saved.
     */
    void save(Object obj, File file);

    /**
     * Saves the given configuration object to its previously associated file.
     * <p>
     * This method should be used when a configuration has already been registered with a file,
     * allowing for seamless updates without specifying the file path again. The service must ensure
     * the mapping of the object to the file persists.
     * </p>
     *
     * @param obj The configuration object to be saved.
     */
    void save(Object obj);

    /**
     * Loads a configuration from the specified file.
     * <p>
     * This method attempts to load the configuration data from the provided file and creates an instance
     * of the provided class type. It is particularly useful during startup or when a manual reload is
     * required.
     * </p>
     *
     * @param clazz The class type of the configuration to be loaded.
     * @param file  The file from which the configuration should be loaded.
     * @param <Type> The type of the configuration.
     * @return An {@code Optional} containing the loaded configuration, or {@code Optional.empty()} if
     *         the file could not be loaded (e.g., if the file is missing or improperly formatted).
     */
    <Type> Optional<Type> load(Class<Type> clazz, File file);

    /**
     * Loads a configuration that has previously been registered.
     * <p>
     * This method uses the registered file associated with the class to load the configuration. It is
     * convenient for reloading configurations without specifying the file location each time.
     * </p>
     *
     * @param clazz The class type of the configuration to be loaded.
     * @param <Type> The type of the configuration.
     * @return An {@code Optional} containing the loaded configuration, or {@code Optional.empty()} if
     *         the file could not be loaded or was not previously registered.
     */
    <Type> Optional<Type> load(Class<Type> clazz);

    /**
     * Reloads a given configuration instance from the specified file, updating only the fields present
     * in the loaded configuration.
     * <p>
     * This method allows for reloading configuration data without fully replacing the existing instance,
     * instead updating the fields with new values from the loaded file. It respects field modifiers such
     * as {@code transient} and {@code final} to avoid modifying fields that should not be overwritten.
     * </p>
     *
     * @param clazz    The class type of the configuration to reload.
     * @param instance The instance to update with reloaded values.
     * @param file     The file from which to reload the configuration.
     * @param <Type>   The type of the configuration.
     */
    default <Type> void reload(Class<Type> clazz, Object instance, File file) {
        load(clazz, file).ifPresent(config -> {
            for (Field field : config.getClass().getDeclaredFields()) {
                int mod = field.getModifiers();

                // Skip transient or final fields
                if (Modifier.isTransient(mod)) continue;
                if (Modifier.isFinal(mod)) continue;

                field.setAccessible(true);

                try {
                    // Update the instance with the field from the loaded configuration
                    field.set(instance, field.get(config));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Retrieves all configuration objects currently managed by the service.
     * <p>
     * This method provides a collection of all configuration instances that have been registered
     * and are being managed by this service. It can be useful for iterating over configurations
     * for batch operations, monitoring, or saving.
     * </p>
     *
     * @return A collection of all managed configuration objects.
     */
    Collection<Object> getConfigs();
}
