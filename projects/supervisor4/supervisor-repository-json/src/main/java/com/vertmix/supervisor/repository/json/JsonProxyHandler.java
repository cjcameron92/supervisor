package com.vertmix.supervisor.repository.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vertmix.supervisor.reflection.AbstractProxyHandler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@code JsonProxyHandler} class serves as both a proxy handler for repository interfaces
 * and a JSON-backed storage implementation. This class enables repositories to interact with
 * JSON-based data storage while also providing an in-memory caching mechanism for enhanced performance.
 *
 * @param <T> The type of entities managed by the repository.
 */
public class JsonProxyHandler<T> extends AbstractProxyHandler<T> {

    private final File file;
    private final Class<?> entityType;
    private Map<String, T> cache = new HashMap<>();

    private static final Gson GSON = new Gson();

    public JsonProxyHandler(Class<T> serviceInterface, File file) {
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

        this.file = file;
        //this.cache = loadFromFile(); // Assume loadFromFile is defined elsewhere
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "find":
                return cache.get((String) args[0]);
            case "save":
                cache.put((String) args[0], (T) args[1]);
                //saveToFile();
                return null;
            case "delete":
                cache.remove((String) args[0]);
                //saveToFile();
                return null;
            case "containsKey":
                return cache.containsKey((String) args[0]);
            case "values":
                return new ArrayList<>(cache.values());
            case "keys":
                return new ArrayList<>(cache.keySet());
            default:
                throw new UnsupportedOperationException("Unsupported operation: " + method.getName());
        }
    }

    private Map<String, T> loadFromFile() {
        if (!file.exists()) {
            return new ConcurrentHashMap<>();
        }
        try (FileReader reader = new FileReader(file)) {
            Type type = TypeToken.getParameterized(Map.class, String.class, entityType).getType();
            Map<String, T> loadedCache = GSON.fromJson(reader, type);
            return loadedCache != null ? new ConcurrentHashMap<>(loadedCache) : new ConcurrentHashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ConcurrentHashMap<>();
        }
    }

    private void saveToFile() {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            CompletableFuture.runAsync(() -> {
                try (FileWriter writer = new FileWriter(file)) {
                    GSON.toJson(cache, writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
