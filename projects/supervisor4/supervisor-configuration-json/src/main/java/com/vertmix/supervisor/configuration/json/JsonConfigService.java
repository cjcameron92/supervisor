package com.vertmix.supervisor.configuration.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vertmix.supervisor.configuration.AbstractConfigService;

import java.io.*;
import java.util.Optional;

public class JsonConfigService extends AbstractConfigService {

    private static final String EXT = ".json";
    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    @Override
    public String getExtension() {
        return EXT;
    }

    @Override
    public void save(Object obj, File file) {
        trySave(obj, file, f -> file.getName().endsWith(EXT), (o, f) -> {
            try (Writer writer = new FileWriter(f)) {
                GSON.toJson(o, writer);
            } catch (IOException e) {
                throw new RuntimeException("Error writing to file: " + f, e);
            }
        });
    }

    @Override
    public <Type> Optional<Type> load(Class<Type> clazz, File file) {
        // Check if the file exists and is not empty
        if (!file.exists() || file.length() == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(tryLoad(clazz, file, f -> f.getName().endsWith(EXT), ((f, instance) -> {
            try (Reader reader = new FileReader(f)) {
                return GSON.fromJson(reader, clazz);
            } catch (IOException e) {
                throw new RuntimeException("Error reading from file: " + f, e);
            }
        })));
    }
}
