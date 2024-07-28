package gg.supervisor.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.io.Closeable;

public class MongoData implements Closeable {

    private final MongoClient mongoClient;

    public MongoData(String mongodbUri) {
        this.mongoClient = MongoClients.create(mongodbUri);
    }

    @Override
    public void close() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
        }
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }
}