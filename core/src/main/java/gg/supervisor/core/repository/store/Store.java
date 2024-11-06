package gg.supervisor.core.repository.store;

import java.util.Map;

/**
 * The {@code Store} interface provides a contract for managing the persistence and retrieval of key-value pairs.
 * This interface acts as a generic abstraction for a storage system, enabling developers to implement various
 * persistence strategies such as in-memory caches, file-based storage, or database-backed storage.
 *
 * <p>By offering methods for basic CRUD (Create, Read, Update, Delete) operations, the {@code Store} interface
 * aims to ensure consistency in data management across the application, enhancing modularity and maintainability.</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *     <li>Provides a generic mechanism for managing key-value data.</li>
 *     <li>Supports operations for adding, retrieving, updating, and deleting items.</li>
 *     <li>Designed to be implemented by classes using various storage backends (e.g., in-memory, file system, or database).</li>
 * </ul>
 *
 * <p><strong>Typical Use Cases:</strong></p>
 * <ul>
 *     <li>Caching frequently accessed data to reduce retrieval times and enhance performance.</li>
 *     <li>Persisting configuration settings or user data across sessions.</li>
 *     <li>Providing a consistent data access layer for repositories or services in an application.</li>
 * </ul>
 *
 * @param <T> The type of the values managed by this store.
 */
public interface Store<T> {

    /**
     * Retrieves an item from the store based on the provided key.
     * <p>
     * This method is used to look up and retrieve the corresponding value for a given key.
     * If the key does not exist, it returns {@code null}. It is recommended to handle the
     * {@code null} scenario to prevent runtime exceptions.
     * </p>
     *
     * @param key The key used to identify the item.
     * @return The value associated with the key, or {@code null} if the key does not exist.
     */
    T get(String key);

    /**
     * Saves or updates an item in the store with the provided key-value pair.
     * <p>
     * This method either adds a new entry or updates an existing entry in the store.
     * The key serves as the identifier, while the value is the data being stored.
     * It is commonly used when adding new data or updating existing data.
     * </p>
     *
     * @param key   The key used to identify the item.
     * @param value The value to be associated with the key.
     */
    void save(String key, T value);

    /**
     * Deletes an item from the store based on the provided key.
     * <p>
     * This method removes the key-value pair associated with the provided key.
     * It is useful for removing outdated or no longer required data from the store.
     * </p>
     *
     * @param key The key used to identify the item to be deleted.
     */
    void delete(String key);

    /**
     * Checks if a specific key exists in the store.
     * <p>
     * This method verifies whether a particular key is present in the store.
     * It is often used to ensure the presence of data before attempting to retrieve or manipulate it.
     * </p>
     *
     * @param key The key to be checked.
     * @return {@code true} if the key exists in the store, {@code false} otherwise.
     */
    boolean containsKey(String key);

    /**
     * Retrieves all key-value pairs currently stored.
     * <p>
     * This method provides a complete view of the items stored in the form of a map.
     * It is particularly useful for iterating through all stored values or exporting data
     * for backup and analysis purposes.
     * </p>
     *
     * @return A map containing all key-value pairs currently stored. If the store is empty, returns an empty map.
     */
    Map<String, T> values();
}
