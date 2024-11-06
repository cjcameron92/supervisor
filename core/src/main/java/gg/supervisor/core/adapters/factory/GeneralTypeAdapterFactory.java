package gg.supervisor.core.adapters.factory;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import gg.supervisor.core.adapters.allocator.UnsafeAllocator;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * A custom TypeAdapterFactory that supports creating TypeAdapters for a variety of classes.
 * This factory attempts to handle classes that may not have default constructors,
 * including classes with fields that are inaccessible by default.
 *
 * It relies on the use of {@link UnsafeAllocator} to instantiate classes without calling constructors,
 * making it versatile for serialization and deserialization of complex types.
 */
public class GeneralTypeAdapterFactory implements TypeAdapterFactory {

    // A map to hold custom adapters for specific classes
    private final Map<Class<?>, TypeAdapter<?>> customAdapters = new HashMap<>();

    /**
     * Creates a new TypeAdapter for the given type if possible. This will use the GeneralTypeAdapter
     * if the type is suitable, otherwise it will return null.
     *
     * @param gson      The Gson object for the serialization context.
     * @param typeToken The type token representing the type to be serialized/deserialized.
     * @param <T>       The type of the object.
     * @return A TypeAdapter for the given type, or null if none could be created.
     */
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Class<?> rawType = typeToken.getRawType();

        // Check if a custom adapter has been registered for this type
        TypeAdapter<?> customAdapter = customAdapters.get(rawType);
        if (customAdapter != null) {
            return (TypeAdapter<T>) customAdapter;
        }

        // Skip Gson's default handling for interfaces, Object, and core Java classes
        if (rawType.isInterface() || rawType.equals(Object.class) || rawType.getPackage().getName().startsWith("java.")) {
            return null;
        }

        // Create a GeneralTypeAdapter to handle the given type
        return new GeneralTypeAdapter<>(gson, typeToken);
    }

    /**
     * Registers a custom adapter for a specific type.
     *
     * @param type    The class type for which the custom adapter is to be registered.
     * @param adapter The TypeAdapter that will handle serialization/deserialization for this type.
     * @param <T>     The type of the object.
     */
    public <T> void registerCustomAdapter(Class<T> type, TypeAdapter<T> adapter) {
        customAdapters.put(type, adapter);
    }

    /**
     * A general-purpose TypeAdapter for serializing and deserializing objects of various classes.
     * This adapter uses UnsafeAllocator to instantiate objects without requiring constructors.
     *
     * @param <T> The type of the object being serialized/deserialized.
     */
    private static class GeneralTypeAdapter<T> extends TypeAdapter<T> {
        private final Gson gson;
        private final Class<T> type;
        private final Map<String, Field> fieldMap = new HashMap<>(); // A map to hold fields by name for quick lookup
        private final UnsafeAllocator allocator;

        /**
         * Constructs a GeneralTypeAdapter that can serialize and deserialize instances of the given type.
         *
         * @param gson      The Gson instance used for serialization.
         * @param typeToken The TypeToken representing the type to be handled.
         */
        public GeneralTypeAdapter(Gson gson, TypeToken<T> typeToken) {
            this.gson = gson;
            this.type = (Class<T>) typeToken.getRawType();
            this.allocator = UnsafeAllocator.create();

            // Cache all declared fields, making them accessible for future use
            for (Field field : type.getDeclaredFields()) {
                if (!field.isAccessible()) {
                    field.setAccessible(true); // Allow reflective access to private fields
                }
                fieldMap.put(field.getName(), field); // Store fields in a map for quick lookup by name
            }
        }

        /**
         * Writes the fields of an object to JSON output.
         *
         * @param out   The JsonWriter to which the object's state will be written.
         * @param value The object to be serialized.
         * @throws IOException if an I/O error occurs during writing.
         */
        @Override
        public void write(JsonWriter out, T value) throws IOException {
            out.beginObject();
            for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
                try {
                    Object fieldValue = entry.getValue().get(value); // Retrieve field value using reflection
                    out.name(entry.getKey()); // Write the field name to JSON
                    gson.toJson(fieldValue, fieldValue != null ? fieldValue.getClass() : Object.class, out); // Write field value to JSON
                } catch (IllegalAccessException e) {
                    throw new JsonIOException(e); // Wrap the exception in a JsonIOException for consistency with Gson
                }
            }
            out.endObject();
        }

        /**
         * Reads a JSON object and populates an instance of the type.
         *
         * @param in The JsonReader from which the JSON representation is read.
         * @return An instance of the type with fields populated from the JSON input.
         * @throws IOException if an I/O error occurs during reading.
         */
        @Override
        public T read(JsonReader in) throws IOException {
            T instance;
            try {
                // Create a new instance using UnsafeAllocator to bypass constructors
                instance = allocator.newInstance(type);
            } catch (Exception e) {
                throw new JsonIOException("Failed to create instance of " + type, e);
            }

            in.beginObject();
            while (in.hasNext()) {
                String name = in.nextName();
                Field field = fieldMap.get(name); // Find the field by name
                if (field != null) {
                    Type fieldType = field.getGenericType();
                    Object fieldValue = gson.fromJson(in, fieldType); // Deserialize the field value
                    try {
                        field.set(instance, fieldValue); // Set the field value using reflection
                    } catch (IllegalAccessException e) {
                        throw new JsonIOException(e); // Wrap the exception in a JsonIOException for consistency with Gson
                    }
                } else {
                    // Skip any unknown fields to allow for flexible deserialization
                    in.skipValue();
                }
            }
            in.endObject();
            return instance;
        }
    }
}
