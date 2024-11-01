package gg.supervisor.core.adapters.handler;

import gg.supervisor.core.store.Store;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

/**
 * Abstract base class for creating dynamic proxy handlers for service interfaces.
 * This handler provides foundational logic for interacting with a {@link Store} to
 * facilitate persistence operations while offering dynamic proxy capabilities.
 *
 * The core functionality includes the ability to create a proxy instance of a given service interface,
 * abstracting away direct persistence interactions.
 *
 * @param <T> The type of the service interface.
 */
public abstract class AbstractProxyHandler<T> implements ProxyHandler<T> {

    // The service interface to be proxied
    protected final Class<T> serviceInterface;

    // Store to be used for persistence operations related to the entity
    protected final Store<T> store;

    // The specific entity type handled by this proxy, derived from the service interface's generic parameters
    protected final Type entityType;

    /**
     * Constructor for creating an instance of {@link AbstractProxyHandler}.
     *
     * @param serviceInterface The service interface that this proxy handler will implement.
     * @param store            The store for interacting with the data layer.
     */
    public AbstractProxyHandler(Class<T> serviceInterface, Store<T> store) {
        this.serviceInterface = serviceInterface;
        this.store = store;

        // Retrieve the entity type from the generic parameter of the service interface.
        // This allows the handler to know the exact type that is being proxied.
        this.entityType = ((ParameterizedType) serviceInterface.getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

    /**
     * Creates a proxy instance for the provided service interface. The generated proxy instance will
     * route method calls through this proxy handler, enabling dynamic behavior to be injected
     * into interactions with the service interface.
     *
     * This approach allows the interception of method calls, providing an opportunity to execute
     * custom logic (e.g., persistence operations) before or after the actual method invocation.
     *
     * @return A proxy instance of the service interface.
     */
    @SuppressWarnings("unchecked")
    public T getInstance() {
        return (T) Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),       // ClassLoader to define the proxy class
                new Class<?>[]{serviceInterface},        // The service interface implemented by the proxy
                this                                     // The handler that will handle method calls
        );
    }
}
