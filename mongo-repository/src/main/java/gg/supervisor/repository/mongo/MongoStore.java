package gg.supervisor.repository.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import gg.supervisor.core.loader.SupervisorLoader;
import gg.supervisor.core.repository.store.Store;
import org.bson.Document;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

/**
 * The {@code MongoStore} class provides a storage mechanism for managing entities in MongoDB.
 * This class implements the {@code Store} interface and supports typical CRUD operations.
 *
 * <p>Using this class, you can store and retrieve entities in a key-value manner,
 * with each entity being stored as a document in a MongoDB collection.</p>
 *
 * @param <T> The type of entities being managed by the {@code MongoStore}.
 */
public class MongoStore<T> implements Store<T>, Closeable {

    private final MongoClient client;
    private final MongoCollection<Document> collection;
    private final Class<T> entityType;

    public MongoStore(MongoConfig mongoConfig, Class<T> entityType) {
        this.client = MongoClients.create(mongoConfig.mongoUri);
        MongoDatabase database = client.getDatabase(mongoConfig.database);
        this.collection = database.getCollection(mongoConfig.collectionTypes.getOrDefault(getClass().getSimpleName(), getClass().getSimpleName()));
        this.entityType = entityType;
    }

    @Override
    public T get(String key) {
        Document doc = collection.find(eq("_id", key)).first();
        return doc != null ? SupervisorLoader.GSON.fromJson(doc.toJson(), entityType) : null;
    }

    @Override
    public void save(String key, T value) {
        Document doc = Document.parse(SupervisorLoader.GSON.toJson(value));
        doc.put("_id", key);
        collection.replaceOne(eq("_id", key), doc, new ReplaceOptions().upsert(true));
    }

    @Override
    public void delete(String key) {
        collection.deleteOne(eq("_id", key));
    }

    @Override
    public boolean containsKey(String key) {
        return collection.find(eq("_id", key)).first() != null;
    }

    @Override
    public Map<String, T> values() {
        Map<String, T> map = new HashMap<>();
        collection.find().forEach(doc -> {
            String key = doc.getString("_id");
            T value = SupervisorLoader.GSON.fromJson(doc.toJson(), entityType);
            map.put(key, value);
        });
        return map;
    }

    @Override
    public void close() {
        if (client != null)
            client.close();
    }
}
