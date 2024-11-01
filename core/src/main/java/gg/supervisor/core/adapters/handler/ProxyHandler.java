package gg.supervisor.core.adapters.handler;

import java.lang.reflect.InvocationHandler;

/**
 * ProxyHandler is an interface that extends Java's {@link InvocationHandler}.
 * It provides a mechanism for creating dynamic proxy instances for a given type.
 *
 * This interface serves as a bridge for dynamic behavior injection by allowing
 * method calls on a proxy instance to be intercepted and handled in a custom way.
 * It is used primarily in situations where additional processing is required around
 * the methods of the proxied interface, such as logging, data validation, or persistence handling.
 *
 * @param <T> The type of the service interface that is being proxied.
 */
public interface ProxyHandler<T> extends InvocationHandler {

    /**
     * Returns a proxy instance that implements the service interface.
     * The proxy instance routes method calls through the {@link InvocationHandler}
     * to provide custom behavior, such as executing additional logic before or after
     * method execution.
     *
     * @return A proxy instance of the service interface.
     */
    T getInstance();
}
