package com.vertmix.supervisor.repository.mongo.bukkit;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.vertmix.supervisor.core.CoreProvider;
import com.vertmix.supervisor.core.module.Module;
import com.vertmix.supervisor.core.service.Services;
import com.vertmix.supervisor.reflection.AbstractProxyHandler;
import com.vertmix.supervisor.repository.mongo.MongoContext;
import com.vertmix.supervisor.repository.mongo.MongoPlayerProxyHandler;
import com.vertmix.supervisor.repository.mongo.MongoRepository;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Proxy;

public class BukkitMongoModule implements Module<Plugin> {
    private MongoClient mongoClient;

    @Override
    public void onEnable(CoreProvider<Plugin> provider) {
        mongoClient = MongoClients.create("localhost");

        System.out.println("Enabled Mongo");
        Services.register(BukkitMongoPlayerRepository.class, clazz -> {
            MongoContext context = clazz.getAnnotation(MongoContext.class);
            MongoDatabase mongoDatabase = mongoClient.getDatabase("dev");
            String collection = clazz.getSimpleName().replaceAll("repository", "");
            if (context != null) {
                mongoDatabase = mongoClient.getDatabase(context.database());
                collection = context.collection();
            }
            return newRepository(clazz, new MongoPlayerProxyHandler<>(clazz, mongoDatabase, collection));
        });


    }

    @Override
    public void onDisable() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
        }
    }

    public static <T> BukkitMongoPlayerRepository<T> newRepository(Class<T> clazz, AbstractProxyHandler<T> handler) {
        return (BukkitMongoPlayerRepository<T>) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                handler
        );
    }
}
