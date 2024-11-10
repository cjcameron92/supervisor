package com.vertmix.supervisor.repository.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.vertmix.supervisor.core.CoreProvider;
import com.vertmix.supervisor.core.annotation.Navigation;
import com.vertmix.supervisor.core.module.Module;
import com.vertmix.supervisor.core.service.Services;
import com.vertmix.supervisor.reflection.AbstractProxyHandler;

import java.io.File;
import java.lang.reflect.Proxy;

public class MongoModule implements Module<Object> {

    private MongoClient mongoClient;


    @Override
    public void onEnable(CoreProvider<Object> provider) {
        mongoClient = MongoClients.create("localhost");

        System.out.println("Enabled Mongo");
        Services.register(MongoRepository.class, clazz -> {
            MongoContext context = clazz.getAnnotation(MongoContext.class);
            MongoDatabase mongoDatabase = mongoClient.getDatabase("dev");
            String collection = clazz.getSimpleName().replaceAll("repository", "");
            if (context != null) {
                mongoDatabase = mongoClient.getDatabase(context.database());
                collection = context.collection();
            }
            return newRepository(clazz, new MongoProxyHandler<>(clazz, mongoDatabase, collection));
        });


    }

    @Override
    public void onDisable() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
        }
    }

    public static <T> MongoRepository<T> newRepository(Class<T> clazz, AbstractProxyHandler<T> handler) {
        return (MongoRepository<T>) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                handler
        );
    }
}
