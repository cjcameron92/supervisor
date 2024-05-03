package gg.supervisor.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JsonStorageHandler<T> implements InvocationHandler {

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    private final File file;
    private final Class<T> clazz;

    public JsonStorageHandler(File file, Class<T> clazz) {
        this.file = file;
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();
        Class<?> entityClass = method.getReturnType();  // Assuming the method's return type is the entity class

        return switch (methodName) {
            case "write" -> write();
            case "read" -> read();
            case "reload" -> reload();
            default -> throw new IllegalStateException("Unexpected value: " + methodName);
        };
    }

    private Object write() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            GSON.toJson(this, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object read() {
        try (FileReader fileReader = new FileReader(file)) {
            return GSON.fromJson(fileReader, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object reload() {
        // implement todo: implement reflection reloading
        return "";
    }
}
