package gg.supervisor.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MongoStorageHandler<T> implements InvocationHandler {

    private final MongoCollection<Document> collection;
    private final Class<T> entityClass;

    public MongoStorageHandler(MongoDatabase database, Class<T> entityClass) {
        this.entityClass = entityClass;
        this.collection = database.getCollection(entityClass.getSimpleName());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return switch (method.getName()) {
            case "save" -> save((String) args[0], (T) args[1]);
            case "delete" -> delete((String) args[0]);
            case "find" -> find((String) args[0]);
            case "findAll" -> findAll();
            default -> throw new IllegalStateException("Method not supported: " + method.getName());
        };
    }

    private Object save(String id, T entity) throws IllegalAccessException {
        Document doc = pojoToDocument(entity);
        doc.append("_id", id);
        collection.replaceOne(Filters.eq("_id", id), doc, new ReplaceOptions().upsert(true));
        return null;
    }

    private boolean delete(String id) {
        return collection.deleteOne(Filters.eq("_id", id)).getDeletedCount() > 0;
    }

    private T find(String id) throws IllegalAccessException, InstantiationException {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        if (doc != null) {
            return documentToPojo(doc, entityClass);
        }
        return null;
    }

    private List<T> findAll() throws IllegalAccessException, InstantiationException {
        List<T> results = new ArrayList<>();
        for (Document doc : collection.find()) {
            results.add(documentToPojo(doc, entityClass));
        }
        return results;
    }

    private Document pojoToDocument(T entity) throws IllegalAccessException {
        Document document = new Document();
        for (Field field : entityClass.getDeclaredFields()) {
            field.setAccessible(true);
            document.append(field.getName(), field.get(entity));
        }
        return document;
    }

    private T documentToPojo(Document document, Class<T> clazz) throws InstantiationException, IllegalAccessException {
        T pojo = clazz.newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            field.set(pojo, document.get(field.getName()));
        }
        return pojo;
    }
}
