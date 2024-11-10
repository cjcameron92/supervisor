package com.vertmix.supervisor.repository.mongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.vertmix.supervisor.reflection.AbstractProxyHandler;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@code MongoPlayerProxyHandler} class extends the {@code AbstractProxyHandler} to provide a MongoDB-based
 * storage mechanism for player data with in-memory caching and reflective method handling.
 *
 * @param <T> The type of player data managed by this handler.
 */
public class MongoPlayerProxyHandler<T> extends AbstractProxyHandler<T> {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().disableHtmlEscaping().create();
    private final MongoCollection<Document> collection;
    private final Class<T> entityType;
    private final Map<String, T> cache = new ConcurrentHashMap<>();

    /**
     * Constructs a {@code MongoPlayerProxyHandler} for managing player-specific MongoDB data.
     *
     * @param serviceInterface The service interface representing the repository.
     * @param database         The MongoDB database instance.
     * @param collectionName   The name of the MongoDB collection.
     */
    public MongoPlayerProxyHandler(Class<T> serviceInterface, MongoDatabase database, String collectionName) {
        super(serviceInterface);

        if (!serviceInterface.isInterface()) {
            throw new IllegalArgumentException("The service interface must be an interface.");
        }
        // Ensure the interface is parameterized correctly
        Type genericInterface = serviceInterface.getGenericInterfaces()[0];
        if (genericInterface instanceof ParameterizedType parameterizedType) {
            Type typeArgument = parameterizedType.getActualTypeArguments()[0];

            // Check if the type argument is a concrete class
            if (typeArgument instanceof Class<?> clazz) {
                this.entityType = (Class<T>) clazz;
            } else if (typeArgument instanceof TypeVariable<?>) {
                throw new IllegalArgumentException("The generic type parameter must not be a type variable. Ensure a concrete class is specified.");
            } else {
                throw new IllegalArgumentException("Unsupported type argument: " + typeArgument.getClass().getName());
            }
        } else {
            throw new IllegalArgumentException("The service interface must specify generic type arguments.");
        }
        this.collection = database.getCollection(collectionName);

        if (this.collection == null) {
            throw new IllegalArgumentException("The specified MongoDB collection does not exist: " + collectionName);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "get":
                return get((String) args[0]);
            case "save":
                save((String) args[0], (T) args[1]);
                return null;
            case "delete":
                delete((String) args[0]);
                return null;
            case "containsKey":
                return containsKey((String) args[0]);
            case "values":
                return values();
            default:
                throw new UnsupportedOperationException("Unsupported operation: " + method.getName());
        }
    }

    private T get(String key) {
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        Bson filter = Filters.eq("_id", key);
        Document document = collection.find(filter).first();

        if (document == null) {
            return null;
        }

        T data = documentToEntity(document);
        cache.put(key, data);
        return data;
    }

    private void save(String key, T value) {
        cache.put(key, value);

        Document document = entityToDocument(key, value);

        // Use upsert to insert if the document does not exist, or replace if it does
        ReplaceOptions options = new ReplaceOptions().upsert(true);
        collection.replaceOne(Filters.eq("_id", key), document, options);
    }

    private void delete(String key) {
        cache.remove(key);

        Bson filter = Filters.eq("_id", key);
        collection.deleteOne(filter);
    }

    private boolean containsKey(String key) {
        if (cache.containsKey(key)) {
            return true;
        }

        Bson filter = Filters.eq("_id", key);
        return collection.find(filter).iterator().hasNext();
    }

    private Map<String, T> values() {
        Map<String, T> allValues = new HashMap<>(cache);

        for (Document document : collection.find()) {
            String key = document.getString("_id");
            if (!cache.containsKey(key)) {
                T value = documentToEntity(document);
                allValues.put(key, value);
            }
        }

        return allValues;
    }

    /**
     * Converts a MongoDB document into an entity object.
     *
     * @param document The MongoDB document.
     * @return The entity object.
     */
    private T documentToEntity(Document document) {
        try {
            return GSON.fromJson(document.toJson(), entityType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts an entity object into a MongoDB document.
     *
     * @param key   The unique key (ID) of the document.
     * @param value The entity object.
     * @return The MongoDB document.
     */
    private Document entityToDocument(String key, T value) {
        Document document = Document.parse(GSON.toJson(value));
        document.put("_id", key);
        return document;
    }
}
