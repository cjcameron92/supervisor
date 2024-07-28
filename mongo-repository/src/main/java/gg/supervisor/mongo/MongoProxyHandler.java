package gg.supervisor.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import gg.supervisor.repository.AbstractProxyHandler;
import org.bson.Document;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MongoProxyHandler<T> extends AbstractProxyHandler<T> {
    private final MongoClient mongoClient;
    private final String databaseName;
    private final Class<T> entityType;

    public MongoProxyHandler(MongoData mongoData, String databaseName, Class<T> serviceInterface) {
        super(serviceInterface);
        this.mongoClient = mongoData.getMongoClient();
        this.databaseName = databaseName;
        this.entityType = (Class<T>) ((ParameterizedType) serviceInterface.getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

    private MongoCollection<Document> getCollection() {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        return database.getCollection(serviceInterface.getSimpleName());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MongoCollection<Document> collection = getCollection();

        switch (method.getName()) {
            case "find":
                return CompletableFuture.supplyAsync(() -> {
                    final String id = (String) args[0];
                    Document document = collection.find(Filters.eq("_id", id)).first();
                    return document != null ? GSON.fromJson(document.toJson(), entityType) : null;
                });
            case "save":
                return CompletableFuture.supplyAsync(() -> {
                    final String id = (String) args[0];
                    final Document document = Document.parse(GSON.toJson(args[1]));
                    document.put("_id", id);
                    collection.replaceOne(Filters.eq("_id", id), document, new ReplaceOptions().upsert(true));
                    return null;
                });
            case "delete":
                return CompletableFuture.supplyAsync(() -> {
                    final String id = (String) args[0];
                    collection.deleteOne(Filters.eq("_id", id));
                    return null;
                });
            case "containsKey":
                return CompletableFuture.supplyAsync(() -> {
                    final String id = (String) args[0];
                    return collection.countDocuments(Filters.eq("_id", id)) > 0;
                });
            case "values":
                return CompletableFuture.supplyAsync(() -> {
                    List<Document> documents = collection.find().into(new ArrayList<>());
                    return documents.stream()
                            .map(doc -> GSON.fromJson(doc.toJson(), entityType))
                            .toList();
                });
            case "keys":
                return CompletableFuture.supplyAsync(() -> {
                    List<String> keys = new ArrayList<>();
                    collection.find().forEach((Document doc) -> keys.add(doc.getString("_id")));
                    return keys;
                });
            default:
                throw new UnsupportedOperationException("Unsupported operation: " + method.getName());
        }
    }
}
