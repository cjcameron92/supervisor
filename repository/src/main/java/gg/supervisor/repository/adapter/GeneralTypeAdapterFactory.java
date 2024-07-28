package gg.supervisor.repository.adapter;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gg.supervisor.repository.util.UnsafeAllocator;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GeneralTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Class<?> rawType = typeToken.getRawType();
        if (rawType.isInterface() || rawType.equals(Object.class) || rawType.getPackage().getName().startsWith("java.")) {
            return null; // Skip Gson's default handling for interfaces and core Java classes
        }
        return new GeneralTypeAdapter<>(gson, typeToken);
    }

    private static class GeneralTypeAdapter<T> extends TypeAdapter<T> {
        private final Gson gson;
        private final Class<T> type;
        private final Map<String, Field> fieldMap = new HashMap<>();
        private final UnsafeAllocator allocator;

        public GeneralTypeAdapter(Gson gson, TypeToken<T> typeToken) {
            this.gson = gson;
            this.type = (Class<T>) typeToken.getRawType();
            this.allocator = UnsafeAllocator.create();
            for (Field field : type.getDeclaredFields()) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                fieldMap.put(field.getName(), field);
            }
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            out.beginObject();
            for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
                try {
                    Object fieldValue = entry.getValue().get(value);
                    out.name(entry.getKey());
                    gson.toJson(fieldValue, fieldValue != null ? fieldValue.getClass() : Object.class, out);
                } catch (IllegalAccessException e) {
                    throw new JsonIOException(e);
                }
            }
            out.endObject();
        }

        @Override
        public T read(JsonReader in) throws IOException {
            T instance;
            try {
                instance = allocator.newInstance(type);
            } catch (Exception e) {
                throw new JsonIOException("Failed to create instance of " + type, e);
            }
            in.beginObject();
            while (in.hasNext()) {
                String name = in.nextName();
                Field field = fieldMap.get(name);
                if (field != null) {
                    Type fieldType = field.getGenericType();
                    Object fieldValue = gson.fromJson(in, fieldType);
                    try {
                        field.set(instance, fieldValue);
                    } catch (IllegalAccessException e) {
                        throw new JsonIOException(e);
                    }
                } else {
                    in.skipValue();
                }
            }
            in.endObject();
            return instance;
        }
    }
}
