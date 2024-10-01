package gg.supervisor.util.storage;

import gg.supervisor.core.config.ConfigService;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class AbstractStorageRegistry<K, V> extends AbstractStorageManager<K, V> {
    private final Map<K, V> registry;

    public AbstractStorageRegistry(Plugin plugin, Class<? extends ConfigService> configService, Class<V> clazz, String folderId, Function<Void, V> newInstanceConsumer, Function<String, K> stringToKey, Function<K, String> keyToString) {
        super(plugin, configService, clazz, folderId, newInstanceConsumer, stringToKey, keyToString);

        this.registry = new HashMap<>();
    }

    public Map<K, V> getRegistry() {
        return registry;
    }

    public void modify(K key, Consumer<V> consumer) {
        final V value = get(key, true);

        consumer.accept(value);

        super.save(key, value);
    }

    @Override
    public V load(File file) {

        V value = super.load(file);

        K key = super.stringToKey.apply(file.getName().replaceAll(super.configService.getExtension(), ""));

        getRegistry().put(key, value);

        return value;
    }

    @Override
    public V load(K id) {
        return load(new File(folder, keyToString.apply(id) + super.configService.getExtension()));
    }

    public Map<K, V> loadAllToMap() {

        registry.putAll(super.loadAll());

        return registry;
    }

    public void unloadAll(boolean save) {

        if (save)
            saveAll(true);

        registry.clear();
    }

    public V get(K key) {
        return registry.get(key);
    }

    public V get(K key, boolean loadIfNull) {

        if (registry.containsKey(key))
            return registry.get(key);
        else if (loadIfNull)
            return super.load(key);
        else return null;
    }

    @Override
    public void save(K id, V value) {
        save(id, value, true);
    }

    public void save(K id) {
        save(id, getRegistry().get(id), true);
    }

    @Override
    public void save(K id, V value, boolean async) {
        getRegistry().put(id, value);
        super.save(id, value, async);
    }

    public void saveAll(boolean async) {
        registry.forEach((key, value) -> save(key, value, async));
    }

}