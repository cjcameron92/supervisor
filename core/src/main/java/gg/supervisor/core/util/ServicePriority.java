package gg.supervisor.core.util;

/**
 * The ServicePriority enum defines the priority levels for components in the framework.
 * These levels are used to determine the order in which components are initialized
 * and managed within the framework. The priority values range from HIGHEST to LOWEST,
 * where components with higher priority are initialized first, providing a clear
 * control over the component lifecycle sequence.
 *
 * <p>This enumeration is essential for managing dependencies among components. For
 * instance, services that are used by many other parts of the application should
 * have a high or highest priority to ensure they are available when other components
 * are being initialized.</p>
 *
 * <p><strong>Priority Levels:</strong></p>
 * <ul>
 *     <li>{@code HIGHEST}: Typically used for core system components or infrastructure that must be ready before anything else.</li>
 *     <li>{@code HIGH}: For components that provide significant functionality required by many others.</li>
 *     <li>{@code NORMAL}: The default priority, used when no specific initialization sequence is needed.</li>
 *     <li>{@code LOW}: Components that can be initialized later, and are less critical for core operations.</li>
 *     <li>{@code LOWEST}: Used for components that are either optional or should be initialized last.</li>
 * </ul>
 *
 * <p><strong>Usage Example:</strong></p>
 * <pre>
 * &#64;Component(priority = ServicePriority.HIGHEST)
 * public class DatabaseService {
 *     // Code to initialize database connections and maintain data access logic.
 * }
 * </pre>
 */
public enum ServicePriority {

    /**
     * Highest priority level, used for critical system components.
     */
    HIGHEST(-2),

    /**
     * High priority level, for essential components that should be initialized before most others.
     */
    HIGH(-1),

    /**
     * Normal priority level, the default for standard components.
     */
    NORMAL(0),

    /**
     * Low priority level, for components that should be initialized after more critical ones.
     */
    LOW(1),

    /**
     * Lowest priority level, for components that are non-essential and should be initialized last.
     */
    LOWEST(2);

    private final int priority;

    /**
     * Constructor to set the priority value.
     *
     * @param priority the numerical value representing the priority level.
     */
    ServicePriority(int priority) {
        this.priority = priority;
    }

    /**
     * Gets the numerical value of the priority, used internally for comparing priorities.
     *
     * @return the priority level as an integer.
     */
    public int getPriority() {
        return this.priority;
    }
}
