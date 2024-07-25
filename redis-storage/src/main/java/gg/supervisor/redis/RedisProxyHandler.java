package gg.supervisor.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import redis.clients.jedis.Jedis;

import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RedisProxyHandler<T> implements InvocationHandler {

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .registerTypeAdapterFactory(new GeneralTypeAdapterFactory())
            .create();
    private final Class<T> serviceInterface;

    public RedisProxyHandler(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @SuppressWarnings("unchecked")
    public T getInstance() {
        return (T) Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class<?>[]{serviceInterface},
                this
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try (Jedis jedis = Redis.getResource()) {


            return switch (method.getName()) {
                case "find" -> CompletableFuture.supplyAsync(() -> {
                    String key = (String) args[0];
                    String json = jedis.hget(serviceInterface.getSimpleName(), key);
                    if (json != null) {
                        System.out.println("Found JSON: " + json);
                        Type entityType = ((ParameterizedType) serviceInterface.getGenericInterfaces()[0]).getActualTypeArguments()[0];
                        return GSON.fromJson(json, entityType);
                    }
                    System.out.println("No entry found for key: " + key);
                    return null;
                });
                case "save" -> CompletableFuture.supplyAsync(() -> {
                    String key = (String) args[0];
                    Type entityType = ((ParameterizedType) serviceInterface.getGenericInterfaces()[0]).getActualTypeArguments()[0];
                    String json = GSON.toJson(args[1], entityType);
                    jedis.hset(serviceInterface.getSimpleName(), key, json);
                    System.out.println("Saved key: " + key + " with JSON: " + json);
                    return null;
                });
                case "delete" -> CompletableFuture.supplyAsync(() -> {
                    String key = (String) args[0];
                    jedis.hdel(serviceInterface.getSimpleName(), key);
                    System.out.println("Deleted key: " + key);
                    return null;
                });
                case "containsKey" -> CompletableFuture.supplyAsync(() -> {
                    String key = (String) args[0];
                    boolean exists = jedis.hexists(serviceInterface.getSimpleName(), key);
                    System.out.println("Key exists: " + key + " -> " + exists);
                    return exists;
                });
                case "values" -> CompletableFuture.supplyAsync(() -> {
                    Map<String, String> values = jedis.hgetAll(serviceInterface.getSimpleName());
                    System.out.println("Retrieved values: " + values);
                    Type entityType = ((ParameterizedType) serviceInterface.getGenericInterfaces()[0]).getActualTypeArguments()[0];
                    return values.values().stream()
                            .map(value -> GSON.fromJson(value, entityType))
                            .toList();
                });
                case "keys" -> CompletableFuture.supplyAsync(() -> {
                    var keys = jedis.hkeys(serviceInterface.getSimpleName());
                    System.out.println("Retrieved keys: " + keys);
                    return keys;
                });
                default -> throw new UnsupportedOperationException("Unsupported operation: " + method.getName());
            };
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
