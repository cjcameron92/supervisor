package gg.supervisor.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that the annotated class is an adapter, typically used for transforming or
 * serializing/deserializing data objects in a customizable manner. This annotation is
 * particularly useful in an application where multiple components interact, and a common
 * data transformation is required to facilitate smooth communication between different
 * systems or modules.
 *
 * The use of the @Adapter annotation allows a central registry to easily discover and
 * apply these adapter classes dynamically, providing a consistent way to manage data
 * serialization and transformations. It often works in conjunction with other utility
 * classes and registries in the core framework to enhance flexibility and maintainability.
 *
 * <p>Common scenarios where @Adapter is applied include:</p>
 * <ul>
 *     <li>Data serialization/deserialization, particularly with JSON libraries such as Gson.</li>
 *     <li>Converting domain models to DTOs (Data Transfer Objects) and vice versa.</li>
 *     <li>Handling non-standard data types that require customized serialization or conversion logic.</li>
 * </ul>
 *
 * <p>Retention policy of {@link RetentionPolicy#RUNTIME} is chosen to allow runtime
 * inspection and processing by the framework, making it suitable for dynamic use cases
 * such as auto-registration or reflective lookup in component-based systems.</p>
 *
 * <p><strong>Usage Example:</strong></p>
 * <pre>
 * &#64;Adapter
 * public class CustomDataAdapter extends TypeAdapter&lt;CustomData&gt; {
 *     // Custom serialization/deserialization logic here.
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Adapter {
}
