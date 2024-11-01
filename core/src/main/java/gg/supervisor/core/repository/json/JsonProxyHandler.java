package gg.supervisor.core.repository.json;

import gg.supervisor.core.adapters.handler.AbstractProxyHandler;
import gg.supervisor.core.store.Store;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * The {@code JsonProxyHandler} class serves as a dynamic proxy handler for repositories that
 * interact with JSON-based storage. It extends the {@link AbstractProxyHandler} to provide
 * a bridge between repository interfaces and the underlying {@link Store} implementation.
 *
 * <p>This class is designed to handle method invocations dynamically by mapping repository
 * method calls to corresponding CRUD operations in the underlying storage. This enables
 * transparent and flexible interaction between high-level repository interfaces and
 * JSON-backed data storage.</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *     <li>Implements core CRUD operations such as finding, saving, deleting, and checking data existence.</li>
 *     <li>Supports dynamically fetching all keys and values stored in the JSON-based store.</li>
 *     <li>Facilitates abstraction of data access logic, promoting separation of concerns and flexibility in data management.</li>
 * </ul>
 *
 * @param <T> The type of the entities managed by the repository.
 */
public class JsonProxyHandler<T> extends AbstractProxyHandler<T> {

    private final Store<T> store;

    /**
     * Constructs a new {@code JsonProxyHandler} for a specified repository interface and store.
     *
     * @param serviceInterface The repository interface that this handler proxies.
     * @param store            The {@link Store} instance that provides persistence for the entities.
     */
    public JsonProxyHandler(Class<T> serviceInterface, Store<T> store) {
        super(serviceInterface, store);
        this.store = store;
    }

    /**
     * Handles method invocations for repository interfaces by routing them to corresponding
     * CRUD operations on the underlying {@link Store}.
     *
     * @param proxy  The proxy instance that the method was invoked on.
     * @param method The method that was invoked.
     * @param args   The arguments passed to the method.
     * @return The result of the method invocation.
     * @throws Throwable If any exception occurs during the method execution.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Switch based on the method name to determine the appropriate operation to perform.
        switch (method.getName()) {
            case "find":
                // Fetch the entity from the store by its key.
                final String key = (String) args[0];
                return store.get(key);

            case "save":
                // Save or update the entity in the store with the given key-value pair.
                final String saveKey = (String) args[0];
                final T entity = (T) args[1];
                store.save(saveKey, entity);
                return null;

            case "delete":
                // Delete the entity from the store by its key.
                final String keyToDelete = (String) args[0];
                store.delete(keyToDelete);
                return null;

            case "containsKey":
                // Check if the specified key exists in the store.
                final String keyToCheck = (String) args[0];
                return store.containsKey(keyToCheck);

            case "values":
                // Retrieve all values stored in the store as a list.
                return new ArrayList<>(store.values().values());

            case "keys":
                // Retrieve all keys currently present in the store as a list.
                return new ArrayList<>(store.values().keySet());

            default:
                // If the method name doesn't match any known operations, throw an exception.
                throw new UnsupportedOperationException("Unsupported operation: " + method.getName());
        }
    }
}
