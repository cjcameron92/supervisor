package gg.supervisor.core.annotation;

import gg.supervisor.core.util.ServicePriority;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The @Component annotation is used to indicate that a class is a core component
 * within the framework. It marks the annotated class as a candidate for automatic
 * registration and management by the framework's component lifecycle handler.
 *
 * Components are the building blocks of the system, typically representing business
 * logic, services, or other critical parts that need to be managed centrally. This
 * annotation allows the framework to discover and register components at runtime.
 *
 * <p>Each component can have a specified {@link ServicePriority} to control the
 * order in which components are initialized, managed, and their dependencies are
 * resolved. The priority is crucial when dealing with dependent services, ensuring
 * that components required by other services are ready before the dependent services
 * are initialized.</p>
 *
 * <p><strong>Common Usage:</strong></p>
 * <ul>
 *     <li>Marking business logic classes, such as service classes, for automatic registration.</li>
 *     <li>Indicating utility components that need to be initialized early or later based on priority.</li>
 *     <li>Registering middleware or aspect components to be processed in a specific order.</li>
 * </ul>
 *
 * <p><strong>Example Usage:</strong></p>
 * <pre>
 * &#64;Component(priority = ServicePriority.HIGH)
 * public class UserService {
 *     // Core business logic related to user management.
 * }
 * </pre>
 *
 * @see ServicePriority
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

    /**
     * Defines the priority of this component within the framework's lifecycle.
     * Components are initialized based on their priority, with lower values being
     * initialized first. This is particularly useful when ensuring that higher
     * priority components are available for other components that may depend on them.
     *
     * @return The priority level of this component.
     */
    ServicePriority priority() default ServicePriority.NORMAL;

}
