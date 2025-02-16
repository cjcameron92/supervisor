package gg.supervisor.util.storage;

import gg.supervisor.core.config.ConfigService;
import gg.supervisor.core.util.Services;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class AbstractStorageManager<K, V> {
    protected final Plugin plugin;
    protected final Class<V> clazz;
    protected final ConfigService configService;

    protected final File folder;

    protected final Function<Void, V> newInstanceConsumer;
    protected final Function<String, K> stringToKey;
    protected final Function<K, String> keyToString;

    public AbstractStorageManager(Plugin plugin, Class<? extends ConfigService> configService, Class<V> clazz, String folderId, Function<Void, V> newInstanceConsumer, Function<String, K> stringToKey, Function<K, String> keyToString) {
        this.plugin = plugin;
        this.clazz = clazz;

        this.configService = Services.loadIfPresent(configService);

        this.newInstanceConsumer = newInstanceConsumer;
        this.stringToKey = stringToKey;
        this.keyToString = keyToString;

        this.folder = new File(plugin.getDataFolder(), folderId);

        this.folder.mkdirs();
    }

    public V loadFromString(String id) {
        return load(new File(folder, id + this.configService.getExtension()));
    }

    public V load(K id) {
        return load(new File(folder, keyToString.apply(id) + this.configService.getExtension()));
    }

    public V load(File file) {

        Optional<V> load = configService.load(clazz, file);

        if (load.isPresent()) {
            V value = load.get();
            return value;
        }

        final V apply = newInstanceConsumer.apply(null);

        if (apply != null)
            return configService.register(clazz, apply, file);

        return null;
    }

    public Map<K, V> loadAll() {

        Map<K, V> map = new HashMap<>();

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            map.put(stringToKey.apply(file.getName().replaceAll(this.configService.getExtension(), "")), load(file));
        }

        return map;
    }

    public void save(K id, V value) {
        save(id, value, true);
    }

    public void save(K id, V value, boolean async) {
        saveFromString(keyToString.apply(id), value, async);
    }

    public void saveFromString(String id, V value, boolean async) {
        Runnable runnable = () -> configService.save(value, new File(folder, id + this.configService.getExtension()));

        if (async) Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        else runnable.run();
    }

    public void delete(K id) {
        deleteFromString(keyToString.apply(id));
    }

    public void deleteFromString(String id) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> new File(folder, id + this.configService.getExtension()).delete());
    }

}