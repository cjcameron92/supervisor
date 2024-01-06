package gg.supervisor.storage.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gg.supervisor.storage.Storage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class JsonStorage<T> implements Storage<T> {

    private final static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    protected final Class<T> clazz;
    protected final File file;

    protected T type;

    public JsonStorage(Class<T> clazz, File file) {
        this.clazz = clazz;
        this.file = file;

        if (!file.exists()) {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    System.err.println("Failed to create the parent directory: " + parentDir);
                }
            }

            try {
                if (!file.createNewFile()) {
                    System.err.println("Failed to create new file: " + file);
                }
            } catch (IOException e) {
                System.err.println("An error occurred while creating the file: " + file);
                e.printStackTrace();
            }
        }

    }

    @Override
    public void save() {
        CompletableFuture.runAsync(() -> {
            try (FileWriter fileWriter = new FileWriter(file)) {
                GSON.toJson(type, clazz, fileWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public T load() {
        try (FileReader fileReader = new FileReader(file)) {
           return GSON.fromJson(fileReader, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void update(T type) {
        this.type = type;
    }

    @Override
    public T get() {
        return type;
    }
}
