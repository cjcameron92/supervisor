package com.vertmix.supervisor.configuration;

import com.vertmix.supervisor.core.CoreProvider;
import com.vertmix.supervisor.core.service.Services;

import java.io.File;
import java.lang.reflect.Constructor;

/**
 * The {@code Config} interface represents the essential behavior required for managing
 * a configuration file within the system. Implementing this interface provides the means
 * to handle the save, reload, and retrieval operations for configurations, allowing the
 * application to maintain up-to-date settings and preferences.
 *
 * <p>This interface is designed to be implemented by classes that manage the configuration
 * lifecycle. It ensures that configuration files are always current and persist any changes
 * made during the application runtime. Typical implementations could include configurations
 * for databases, application settings, or plugin-specific options.</p>
 *
 * <p><strong>Primary Responsibilities:</strong></p>
 * <ul>
 *     <li><strong>Save</strong> - Persist changes made to the configuration to ensure consistency.</li>
 *     <li><strong>Reload</strong> - Load the configuration file again, which is helpful when the configuration
 *     file is edited outside of the application and must be updated in memory.</li>
 *     <li><strong>Get File</strong> - Provide access to the underlying configuration file, enabling
 *     advanced operations or custom file handling as needed.</li>
 * </ul>
 *
 * <p><strong>Common Usage:</strong></p>
 * <ul>
 *     <li>Saving updated configuration settings after changes are made through a user interface or
 *     application action.</li>
 *     <li>Reloading a configuration to reflect external changes without restarting the application.</li>
 *     <li>Accessing the configuration file for custom backup or restoration purposes.</li>
 * </ul>
 *
 * <p><strong>Framework Integration:</strong></p>
 * <p>The {@code Config} interface is typically used by classes annotated with {@code @Configuration}.
 * The framework will manage these configuration classes automatically, including tasks like
 * registration, validation, and persistence. The interface methods allow for both manual and
 * automated configuration management at runtime, enhancing the flexibility of configuration
 * operations.</p>
 *
 * <p><strong>Example Implementation:</strong></p>
 * <pre>
 * public class DatabaseConfig implements Config {
 *     private File configFile;
 *
 *     public DatabaseConfig(File configFile) {
 *         this.configFile = configFile;
 *     }
 *
 *     {@literal @Override}
 *     public void save() {
 *         // Code to save the current configuration state to the file
 *     }
 *
 *     {@literal @Override}
 *     public void reload() {
 *         // Code to reload the configuration from the file
 *     }
 *
 *     {@literal @Override}
 *     public File getFile() {
 *         return configFile;
 *     }
 * }
 * </pre>
 *
 * <p>In the above example, {@code DatabaseConfig} provides specific behavior for saving and
 * reloading configuration related to database settings. The {@code getFile()} method returns
 * the associated configuration file.</p>
 *
 * @see Configuration
 */
public interface Config {

    /**
     * Persists the current state of the configuration to the file system.
     * <p>
     * This method should be called whenever changes are made to the configuration,
     * to ensure that the updated settings are saved properly. Implementations must
     * take care to write data to the file in a thread-safe manner if multiple threads
     * might access the configuration.
     * </p>
     */
    void save();

    /**
     * Reloads the configuration from the underlying file.
     * <p>
     * This is useful when the configuration file is manually edited while the application
     * is running, allowing the in-memory representation to reflect the latest changes
     * made to the file. Implementations must handle potential issues such as file
     * availability or formatting errors gracefully.
     * </p>
     */
    void reload();

    /**
     * Provides access to the underlying configuration file.
     * <p>
     * This method allows other components or services to access the raw configuration
     * file, which may be useful for advanced operations like backup, restoration, or
     * custom validation. Implementations must return a valid {@link File} reference.
     * </p>
     *
     * @return The configuration file managed by this instance.
     */
    File getFile();


    static Object registerConfig(Class<?> clazz, CoreProvider<Object> provider) throws Exception {
        if (!clazz.isAnnotationPresent(Configuration.class)) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " must be annotated with @Configuration");
        }

        Configuration configuration = clazz.getAnnotation(Configuration.class);
        File configDirectory = new File(provider.getPath().toFile(), configuration.path());

        if (!configDirectory.exists() && !configDirectory.mkdirs()) {
            throw new IllegalStateException("Failed to create configuration directory at: " + configDirectory.getPath());
        }

        File configFile = new File(configDirectory, configuration.fileName());
        ConfigService configService = Services.getService(configuration.service());
        if (configService == null) {
            throw new NullPointerException("ConfigService has not been registered");
        }

        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object configInstance = constructor.newInstance();

        configService.register((Class<Object>) clazz, configInstance, configFile);
        Services.register(clazz, configInstance);

        return configInstance;
    }
}
