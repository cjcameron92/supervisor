package gg.supervisor.repository.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import gg.supervisor.core.loader.SupervisorLoader;
import gg.supervisor.core.repository.store.Store;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.mongodb.client.model.Filters.eq;

/**
 * The {@code MongoPlayerStore} class provides a storage mechanism for managing individual player data in MongoDB.
 * This store is designed for systems where each player has their own document, allowing easy per-player data management.
 *
 * <p>This implementation utilizes MongoDB as the storage system to persist player data,
 * coupled with an in-memory cache to provide faster access to frequently used data.</p>
 *
 * @param <T> The type of player data being managed by the {@code MongoPlayerStore}.
 */
public class MongoPlayerStore<T> implements Store<T>, Closeable {

    private final MongoClient client;
    // MongoDB collection where player data is stored
    private final MongoCollection<Document> collection;

    // Type of the player data entity
    private final Class<T> entityType;

    // In-memory cache for faster data retrieval
    private final Map<String, T> cache;

    /**
     * Constructs a new {@code MongoPlayerStore} for managing player data.
     *
     * @param entityType The class type of the player data entities.
     */
    public MongoPlayerStore(MongoConfig mongoConfig, Class<T> entityType) {
        this.client = MongoClients.create(mongoConfig.mongoUri);
        MongoDatabase database = client.getDatabase(mongoConfig.database);
        this.collection = database.getCollection(mongoConfig.collectionTypes.getOrDefault(getClass().getSimpleName(), getClass().getSimpleName()));
        this.entityType = entityType;
        this.cache = new ConcurrentHashMap<>();
    }

    /**
     * Retrieves the player data associated with the given key from the store.
     * The method first checks the in-memory cache before attempting to load from MongoDB.
     *
     * @param key The unique key for the player data (typically a UUID).
     * @return The player data associated with the given key, or {@code null} if no such data exists.
     */
    @Override
    public T get(String key) {
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        Document doc = collection.find(eq("_id", key)).first();
        if (doc != null) {
            T data = SupervisorLoader.GSON.fromJson(doc.toJson(), entityType);
            cache.put(key, data); // Cache the loaded data
            return data;
        }
        return null;
    }

    /**
     * Saves or updates the player data associated with the given key.
     * The data is both saved to the in-memory cache and persisted to MongoDB.
     *
     * @param key The unique key for the player data (typically a UUID).
     * @param value The player data to save.
     */
    @Override
    public void save(String key, T value) {
        cache.put(key, value); // Save to cache
        Document doc = Document.parse(SupervisorLoader.GSON.toJson(value));
        doc.put("_id", key);
        collection.replaceOne(eq("_id", key), doc, new ReplaceOptions().upsert(true)); // Upsert to MongoDB
    }

    /**
     * Deletes the player data associated with the given key.
     * The data is removed from both the in-memory cache and MongoDB.
     *
     * @param key The unique key for the player data (typically a UUID).
     */
    @Override
    public void delete(String key) {
        cache.remove(key); // Remove from cache
        collection.deleteOne(eq("_id", key)); // Delete from MongoDB
    }

    /**
     * Checks whether player data exists in the store for the given key.
     *
     * @param key The unique key for the player data (typically a UUID).
     * @return {@code true} if data for the given key exists in the store, {@code false} otherwise.
     */
    @Override
    public boolean containsKey(String key) {
        if (cache.containsKey(key)) {
            return true;
        }
        return collection.find(eq("_id", key)).first() != null;
    }

    /**
     * Retrieves all player data stored in this store.
     * This includes data from both the in-memory cache and MongoDB.
     *
     * @return A map containing all player data from both the cache and MongoDB.
     */
    @Override
    public Map<String, T> values() {
        Map<String, T> allValues = new ConcurrentHashMap<>(cache);

        collection.find().forEach(doc -> {
            String key = doc.getString("_id");
            if (!cache.containsKey(key)) {
                T value = SupervisorLoader.GSON.fromJson(doc.toJson(), entityType);
                if (value != null) {
                    allValues.put(key, value);
                }
            }
        });

        System.out.println("Returning all values: " + allValues.size() + " items");
        return allValues;
    }

    @Override
    public void close() {
        if (this.client != null)
            client.close();
    }
}
