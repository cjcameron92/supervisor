package gg.supervisor.storage;

import gg.supervisor.api.Services;
import gg.supervisor.api.Storage;
import gg.supervisor.api.StorageService;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class JsonStorageService implements StorageService {

    private final Plugin plugin;

    public JsonStorageService(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Object loadService(Class<?> clazz) {
        Storage storage = clazz.getAnnotation(Storage.class);
        if (storage != null) {
            final String path = storage.fileName();
            final File file = new File(plugin.getDataFolder(), path);
            try {
                JsonStorageHandler<?> storageHandler = new JsonStorageHandler<>(file, storage.type());
                if (file.createNewFile()) {

                }

                Services.register(clazz, storageHandler);
                return storageHandler;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
