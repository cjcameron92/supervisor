package gg.supervisor.core.annotation;

import gg.supervisor.core.config.ConfigService;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The {@code @Configuration} annotation is used to designate a class as a configuration holder
 * that will be loaded and managed by the framework. It defines the location and file name of
 * the configuration, as well as the service responsible for managing it. This annotation plays
 * a key role in managing the application's configuration lifecycle, allowing the framework to
 * load, save, and update configurations automatically.
 *
 * <p>Classes annotated with {@code @Configuration} should represent specific configurations,
 * which are typically backed by a file that contains data in formats such as JSON, YAML, or
 * properties. The annotation facilitates a consistent and automated way to manage configurations
 * across different parts of the system.</p>
 *
 * <p><strong>Attributes:</strong></p>
 * <ul>
 *     <li><strong>path</strong> (default: "/") - Specifies the directory path where the configuration
 *     file should be stored. This can be used to organize configuration files into different folders.</li>
 *     <li><strong>fileName</strong> - Specifies the name of the configuration file. This attribute is
 *     required and must be unique within its directory to avoid file conflicts.</li>
 *     <li><strong>service</strong> - Specifies the {@link ConfigService} class that will handle
 *     loading, saving, and validating the configuration. The provided {@link ConfigService} must
 *     extend {@code ConfigService} and is responsible for ensuring that configuration changes
 *     are appropriately managed.</li>
 * </ul>
 *
 * <p><strong>Common Usage:</strong></p>
 * <ul>
 *     <li>Configuring plugin-specific settings in a Minecraft plugin system.</li>
 *     <li>Persisting application settings or user-defined preferences.</li>
 *     <li>Facilitating changes to the system configuration at runtime by enabling automatic
 *     loading and saving through the specified {@link ConfigService}.</li>
 * </ul>
 *
 * <p><strong>Example Usage:</strong></p>
 * <pre>
 * &#64;Configuration(path = "configs/", fileName = "database-config.json", service = DatabaseConfigService.class)
 * public class DatabaseConfig {
 *     private String databaseUrl;
 *     private String username;
 *     private String password;
 *
 *     // Getters and setters for each field
 * }
 * </pre>
 *
 * <p>In the example above, the {@code @Configuration} annotation is used to define a configuration
 * file named {@code "database-config.json"} located in the {@code "configs/"} directory. The
 * {@code DatabaseConfigService} class is specified as the handler for managing the configuration lifecycle.</p>
 *
 * <p><strong>Best Practices:</strong></p>
 * <ul>
 *     <li>Ensure the {@code fileName} is unique within its path to prevent overwriting
 *     or conflicts with other configuration files.</li>
 *     <li>Use descriptive paths to organize configuration files, especially in larger
 *     systems where multiple configurations are used (e.g., separate user settings from system settings).</li>
 *     <li>Ensure that the {@code service} class provides appropriate validation to detect
 *     and handle invalid configuration values.</li>
 * </ul>
 *
 * <p><strong>Framework Integration:</strong></p>
 * <p>During component initialization, the framework inspects the {@code @Configuration} annotation
 * and automatically creates the associated configuration instance. It uses the specified
 * {@link ConfigService} to load the existing data, save changes, and handle updates to ensure
 * the configuration remains consistent throughout the application's lifecycle.</p>
 *
 * @see ConfigService
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {

    /**
     * Specifies the directory path where the configuration file should be stored.
     * This allows for organized storage of configuration files in different directories.
     *
     * <p>Defaults to the root directory ("/").</p>
     *
     * @return The path of the configuration directory.
     */
    String path() default "/";

    /**
     * Specifies the name of the configuration file. This is a required attribute
     * and must be unique within its directory to avoid file conflicts.
     *
     * <p>For example, {@code "settings.json"} or {@code "database-config.yaml"}.</p>
     *
     * @return The name of the configuration file.
     */
    String fileName();

    /**
     * Specifies the service class that will handle the configuration's lifecycle,
     * including loading, saving, and validation.
     *
     * <p>The provided service must extend the {@link ConfigService} base class.</p>
     *
     * @return The service class that will manage this configuration.
     */
    Class<? extends ConfigService> service();

}
