package com.vertmix.supervisor.reflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

public abstract class AbstractProxyHandler<T> implements ProxyHandler<T> {

    // The service interface to be proxied
    protected final Class<T> serviceInterface;


    // The specific entity type handled by this proxy, derived from the service interface's generic parameters
    protected final Type entityType;

    /**
     * Constructor for creating an instance of {@link AbstractProxyHandler}.
     *
     * @param serviceInterface The service interface that this proxy handler will implement.
     */
    public AbstractProxyHandler(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;

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
