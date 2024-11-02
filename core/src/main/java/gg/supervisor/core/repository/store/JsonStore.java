package gg.supervisor.core.repository.store;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static gg.supervisor.core.loader.SupervisorLoader.GSON;

/**
 * The {@code JsonStore} class provides a storage mechanism for managing entities in JSON format.
 * This class implements the {@code Store} interface and supports typical CRUD operations, backed by
 * a persistent file-based store with an in-memory caching mechanism to enhance performance.
 *
 * <p>Using this class, you can store and retrieve entities in a simple key-value manner, with all
 * records being stored in a single JSON file. This is ideal for scenarios where lightweight storage
 * is needed without the overhead of a full-fledged database.</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *     <li><strong>File-Based Storage:</strong> Stores all entities in a single JSON file, providing
 *     a persistent record that survives application restarts.</li>
 *     <li><strong>In-Memory Caching:</strong> All data is cached in-memory for faster access,
 *     reducing file I/O operations and improving overall performance.</li>
 *     <li><strong>Asynchronous File Writes:</strong> Updates to the storage file are written
 *     asynchronously to avoid blocking the main thread, enhancing responsiveness.</li>
 * </ul>
 *
 * @param <T> The type of entities being managed by the {@code JsonStore}.
 */
public class JsonStore<T> implements Store<T> {

    // Path to the JSON file where data is persisted
    private final File file;

    // The type of entity that this store will manage
    private final Class<T> entityType;

    // In-memory cache for faster access to stored data
    private final Map<String, T> cache;

    /**
     * Constructs a new {@code JsonStore} with the given file and entity type.
     * The data is loaded from the file into the in-memory cache during initialization.
     *
     * @param file       The JSON file where the data is stored.
     * @param entityType The class type of the entities being managed.
     */
    public JsonStore(File file, Class<T> entityType) {
        this.file = file;
        this.entityType = entityType;
        this.cache = loadFromFile(); // Load the data from the file into the cache
    }

    /**
     * Retrieves an entity from the store by its key.
     *
     * @param key The unique key associated with the entity.
     * @return The entity associated with the given key, or {@code null} if no such entity exists.
     */
    @Override
    public T get(String key) {
        return cache.get(key);
    }

    /**
     * Saves or updates an entity in the store with the given key.
     * The data is both cached in-memory and persisted to the file asynchronously.
     *
     * @param key   The key associated with the entity to be saved.
     * @param value The entity to save.
     */
    @Override
    public void save(String key, T value) {
        cache.put(key, value);
        saveToFile(); // Persist changes to the file
    }

    /**
     * Deletes an entity from the store by its key.
     * The entity is removed from both the in-memory cache and the persistent file.
     *
     * @param key The key of the entity to be deleted.
     */
    @Override
    public void delete(String key) {
        cache.remove(key);
        saveToFile(); // Persist changes to the file
    }

    /**
     * Checks whether an entity with the given key exists in the store.
     *
     * @param key The key to check.
     * @return {@code true} if the entity exists, {@code false} otherwise.
     */
    @Override
    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }

    /**
     * Returns all entities in the store as a map of key-value pairs.
     *
     * @return A map containing all key-value pairs from the store.
     */
    @Override
    public Map<String, T> values() {
        // Return a non-null map, even if there are no values
        return new HashMap<>(cache);
    }

    /**
     * Loads data from the JSON file into an in-memory cache.
     * If the file does not exist, an empty cache is created.
     *
     * @return A map containing the loaded data, or an empty map if the file does not exist.
     */
    private Map<String, T> loadFromFile() {
        if (!file.exists()) {
            return new ConcurrentHashMap<>(); // Return empty map if file doesn't exist
        }
        try (FileReader reader = new FileReader(file)) {
            Type type = TypeToken.getParameterized(Map.class, String.class, entityType).getType();
            Map<String, T> loadedCache = GSON.fromJson(reader, type);
            return loadedCache != null ? new ConcurrentHashMap<>(loadedCache) : new ConcurrentHashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ConcurrentHashMap<>();
        }
    }

    /**
     * Persists the in-memory cache to the JSON file asynchronously.
     * If the file or its parent directories do not exist, they are created.
     */
    private void saveToFile() {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs(); // Create parent directories if they don't exist
                file.createNewFile(); // Create the file if it doesn't exist
            }

            // Save to file asynchronously to avoid blocking the main thread
            CompletableFuture.runAsync(() -> {
                try (FileWriter writer = new FileWriter(file)) {
                    GSON.toJson(cache, writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
