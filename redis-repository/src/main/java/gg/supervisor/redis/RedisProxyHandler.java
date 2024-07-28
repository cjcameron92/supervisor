package gg.supervisor.redis;

import gg.supervisor.repository.AbstractProxyHandler;
import redis.clients.jedis.Jedis;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RedisProxyHandler<T> extends AbstractProxyHandler<T> {

    private final Redis redis;

    public RedisProxyHandler(Class<T> serviceInterface, Redis redis) {
        super(serviceInterface);
        this.redis = redis;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try (Jedis jedis = redis.getResource()) {
            return switch (method.getName()) {
                case "find" -> CompletableFuture.supplyAsync(() -> {
                    final String key = (String) args[0];
                    final String json = jedis.hget(serviceInterface.getSimpleName(), key);
                    if (json != null) {
                        return GSON.fromJson(json, entityType);
                    }
                    return null;
                });
                case "save" -> CompletableFuture.supplyAsync(() -> {
                    final String key = (String) args[0];
                    final String json = GSON.toJson(args[1], entityType);
                    jedis.hset(serviceInterface.getSimpleName(), key, json);
                    return null;
                });
                case "delete" -> CompletableFuture.supplyAsync(() -> {
                    final String key = (String) args[0];
                    jedis.hdel(serviceInterface.getSimpleName(), key);
                    return null;
                });
                case "containsKey" -> CompletableFuture.supplyAsync(() -> {
                    final String key = (String) args[0];
                    return jedis.hexists(serviceInterface.getSimpleName(), key);
                });
                case "values" -> CompletableFuture.supplyAsync(() -> {
                    final Map<String, String> values = jedis.hgetAll(serviceInterface.getSimpleName());
                    return values.values().stream()
                            .map(value -> GSON.fromJson(value, entityType))
                            .toList();
                });
                case "keys" -> CompletableFuture.supplyAsync(() -> (List<String>) new ArrayList<>(jedis.hkeys(serviceInterface.getSimpleName())));
                default -> throw new UnsupportedOperationException("Unsupported operation: " + method.getName());
            };
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
