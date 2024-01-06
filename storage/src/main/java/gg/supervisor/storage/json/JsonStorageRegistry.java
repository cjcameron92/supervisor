package gg.supervisor.storage.json;

import gg.supervisor.storage.Storage;
import gg.supervisor.storage.StorageRegistry;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class JsonStorageRegistry<T> extends HashMap<String, T> implements StorageRegistry<T> {

    private final Map<String, Storage<T>> storages = new HashMap<>();
    private final Class<T> clazz;
    private final File folder;

    public JsonStorageRegistry(Class<T> clazz, File folder) {
        super();
        folder.mkdirs();
        this.clazz = clazz;
        this.folder = folder;
    }

    @Override
    public void save() {
        forEach((key, value) -> {
            final Storage<T> storage = this.storages.getOrDefault(key, new JsonStorage<>(clazz, new File(folder, key + ".json")));
            storage.update(value);
            storage.save();
        });
    }


    @Override
    public void load() {
        clear();
        final File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file == null) continue;
            final String filename = file.getName().split("\\.")[0];
            Storage<T> storage = storages.get(filename);
            if (storage == null) {
                storage = new JsonStorage<>(clazz, new File(folder, filename + ".json"));
                T loadedInstance = storage.load();
                if (loadedInstance != null) {
                    storages.put(filename, storage);
                    put(filename, loadedInstance);
                }

            }
        }
    }

}
