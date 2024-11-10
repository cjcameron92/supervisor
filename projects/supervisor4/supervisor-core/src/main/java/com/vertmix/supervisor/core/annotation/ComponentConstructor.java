package com.vertmix.supervisor.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The {@code @ComponentConstructor} annotation is used to mark a specific constructor
 * of a component class to be used by the framework during the automatic creation and
 * initialization of components. This annotation helps in identifying the preferred
 * constructor, especially when multiple constructors are available within a class.
 *
 * <p>When multiple constructors exist in a class, the framework uses this annotation
 * to determine which one should be used to create the component instance. This is
 * particularly useful for dependency injection, where a specific set of parameters
 * may be required during construction.</p>
 *
 * <p><strong>Common Usage:</strong></p>
 * <ul>
 *     <li>Marking a constructor that has dependencies that should be injected automatically by the framework.</li>
 *     <li>Ensuring a consistent way to initialize complex components with specific arguments.</li>
 *     <li>Helping to avoid ambiguity when there are multiple overloaded constructors.</li>
 * </ul>
 *
 * <p><strong>Example Usage:</strong></p>
 * <pre>
 * &#64;Component
 * public class DatabaseService {
 *
 *     private final ConfigService configService;
 *
 *     &#64;ComponentConstructor
 *     public DatabaseService(ConfigService configService) {
 *         this.configService = configService;
 *     }
 * }
 * </pre>
 *
 * <p>In the above example, the {@code @ComponentConstructor} annotation is used to
 * indicate which constructor should be utilized by the framework's loader during
 * initialization. This ensures that the {@code ConfigService} is correctly injected.</p>
 *
 * <p><strong>Best Practices:</strong></p>
 * <ul>
 *     <li>Use {@code @ComponentConstructor} when the component requires specific dependencies to be injected.</li>
 *     <li>Ensure that the annotated constructor has all necessary dependencies that the framework can provide.</li>
 *     <li>Avoid using the annotation on constructors that require manual or user-provided inputs, unless the framework supports that input type.</li>
 * </ul>
 *
 * @see Component
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentConstructor {
}
