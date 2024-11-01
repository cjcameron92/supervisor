package gg.supervisor.core.store;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static gg.supervisor.core.loader.SupervisorLoader.GSON;

/**
 * The {@code JsonPlayerStore} class provides a mechanism for storing and managing individual player data
 * in JSON format. This store is designed for systems where each player has their own file, allowing easy
 * per-player data management.
 *
 * <p>This implementation utilizes a file-based storage system to persist player data, coupled with an
 * in-memory cache to provide faster access to frequently used data. This class is useful for applications
 * that need to persist and access player-specific data efficiently.</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *     <li><strong>Player-Specific Storage:</strong> Each player’s data is stored in a separate file named
 *     using the player's key, making it easier to access or delete individual records.</li>
 *     <li><strong>In-Memory Caching:</strong> Frequently accessed player data is cached in memory to reduce
 *     file I/O operations and enhance performance.</li>
 *     <li><strong>Robust File Handling:</strong> Proper directory and file handling to ensure files are created,
 *     read, and deleted correctly with appropriate checks and debug statements.</li>
 * </ul>
 *
 * @param <T> The type of player data being managed by the {@code JsonPlayerStore}.
 */
public class JsonPlayerStore<T> implements Store<T> {

    // Directory where player data files are stored
    private final File directory;

    // Type of the player data entity
    private final Class<T> entityType;

    // In-memory cache for faster data retrieval
    private final Map<String, T> cache;

    /**
     * Constructs a new {@code JsonPlayerStore} for managing player data.
     *
     * @param directory  The directory where player-specific JSON files will be stored.
     * @param entityType The class type of the player data entities.
     */
    public JsonPlayerStore(File directory, Class<T> entityType) {
        this.directory = directory;
        this.entityType = entityType;
        this.cache = new ConcurrentHashMap<>();

        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it does not exist
        }
    }

    /**
     * Retrieves the player data associated with the given key from the store.
     * The method first checks the in-memory cache before attempting to load from the file system.
     *
     * @param key The unique key for the player data (typically a UUID).
     * @return The player data associated with the given key, or {@code null} if no such data exists.
     */
    @Override
    public T get(String key) {
        // Check if the player's data is in the cache
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        // If not in cache, try to load from file
        File file = new File(directory, key + ".json");
        if (!file.exists()) {
            return null;
        }
        try (FileReader reader = new FileReader(file)) {
            T data = GSON.fromJson(reader, entityType);
            // Cache the loaded data if it was successfully read
            if (data != null) {
                cache.put(key, data);
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Saves or updates the player data associated with the given key.
     * The data is both saved to the in-memory cache and persisted to a file.
     *
     * @param key   The unique key for the player data (typically a UUID).
     * @param value The player data to save.
     */
    @Override
    public void save(String key, T value) {
        // Save to cache
        cache.put(key, value);

        // Persist to file
        File file = new File(directory, key + ".json");
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs(); // Create parent directories if they don't exist
                file.createNewFile(); // Create the file if it doesn't exist
            }
            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(value, writer);
            }
            // Debug statement to verify file path and content
            System.out.println("Saved data for key: " + key + " to file: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the player data associated with the given key.
     * The data is removed from both the in-memory cache and the file system.
     *
     * @param key The unique key for the player data (typically a UUID).
     */
    @Override
    public void delete(String key) {
        // Remove from cache
        cache.remove(key);

        // Delete from filesystem
        File file = new File(directory, key + ".json");
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Deleted file for key: " + key);
            } else {
                System.out.println("Failed to delete file for key: " + key);
            }
        }
    }

    /**
     * Checks whether player data exists in the store for the given key.
     *
     * @param key The unique key for the player data (typically a UUID).
     * @return {@code true} if data for the given key exists in the store, {@code false} otherwise.
     */
    @Override
    public boolean containsKey(String key) {
        // Check in cache first
        if (cache.containsKey(key)) {
            return true;
        }

        // If not in cache, check if the file exists
        File file = new File(directory, key + ".json");
        return file.exists();
    }

    /**
     * Retrieves all player data stored in this store.
     * This includes data from both the in-memory cache and files on the disk.
     *
     * @return A map containing all player data from both the cache and files.
     */
    @Override
    public Map<String, T> values() {
        // Ensure the returned map is always non-null
        Map<String, T> allValues = new ConcurrentHashMap<>(cache);

        // Add values from files if they are not already in the cache
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                String key = file.getName().replace(".json", "");
                if (!cache.containsKey(key)) {
                    try (FileReader reader = new FileReader(file)) {
                        T value = GSON.fromJson(reader, entityType);
                        if (value != null) {
                            allValues.put(key, value);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Debug statement to verify returned values
        System.out.println("Returning all values: " + allValues.size() + " items");
        return allValues;
    }
}
