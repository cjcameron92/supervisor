package gg.supervisor.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.lang.reflect.Proxy;

public class MongoStorageFactory {

    public static <T> MongoStorage_V1<T> create(Class<T> entityClass, String databaseName) {
        MongoClient client = MongoClients.create();
        MongoDatabase database = client.getDatabase(databaseName);
        MongoStorageHandler<T> handler = new MongoStorageHandler<>(database, entityClass);
        return (MongoStorage_V1<T>) Proxy.newProxyInstance(
            MongoStorageFactory.class.getClassLoader(),
            new Class<?>[] { MongoStorage_V1.class },
            handler
        );
    }
}
