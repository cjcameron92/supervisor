package gg.supervisor.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import gg.supervisor.storage.Storage;
import org.bson.Document;
import org.bukkit.Bukkit;

import static com.mongodb.client.model.Filters.eq;
import static gg.supervisor.storage.json.JsonStorage.GSON;

public class MongoStorage<T> implements Storage<T> {

    private final String identifier;
    private final MongoCollection<Document> collection;
    private final Class<T> clazz;
    private final boolean verbose;
    private T instance;

    public MongoStorage(MongoData mongoData, String databaseName, String collectionName, Class<T> clazz, String identifier, boolean verbose) {
        this.collection = mongoData.getMongoClient().getDatabase(databaseName).getCollection(collectionName);
        this.clazz = clazz;
        this.identifier = identifier;
        this.verbose = verbose;
    }

    @Override
    public void save() {
        final Document document = Document.parse(GSON.toJson(instance)).append("_id", identifier);

        collection.replaceOne(Filters.eq("_id", identifier), document, new ReplaceOptions().upsert(true));

        if (verbose) {
            Bukkit.getLogger().info("Saved document " + identifier + " to MongoDB collection " + collection.getNamespace().getCollectionName());
        }
    }

    @Override
    public T load() {
        final Document document = collection.find(eq("_id", identifier)).first();
        if (document != null) {
            Object o = document.remove("_id");

            if (verbose) {
                Bukkit.getLogger().info("Loaded document " + o.toString() + " from MongoDB collection " + collection.getNamespace().getCollectionName());
            }

            instance = GSON.fromJson(document.toJson(), clazz);
        }

        return instance;
    }

    @Override
    public T get() {
        return instance;
    }

    @Override
    public void update(T type) {
        this.instance = type;
        save();
    }
}
