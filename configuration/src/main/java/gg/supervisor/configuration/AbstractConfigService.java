package gg.supervisor.configuration;

import gg.supervisor.api.Config;
import gg.supervisor.api.ConfigService;
import gg.supervisor.configuration.exception.ConfigNotRegisteredException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public abstract class AbstractConfigService implements ConfigService {

    protected final Map<Class<?>, Object> loadedConfigs = new ConcurrentHashMap<>();

    private final Plugin plugin;

    public AbstractConfigService(Plugin plugin) {
        this.plugin = plugin;
    }

    protected  void registerData(Class<?> clazz, Object type, File file) {
        this.loadedConfigs.put(clazz, type);
    }

    @Override
    public Object register(Class<?> clazz, Object instance, File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        registerData(clazz, instance, file);

        reload(clazz, instance, file);
        save(instance, file);

        return instance;
    }

    protected void trySave(Object obj, File file, Predicate<File> predicate, BiConsumer<Object, File> consumer) {
        try {
            if (!file.createNewFile()) {
                file.delete();
                file.createNewFile();
            }

            if (predicate.test(file)) {
                consumer.accept(obj, file);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Object obj) {
        if (this.loadedConfigs.containsKey(obj.getClass())) {
            final Config config = (Config) this.loadedConfigs.get(obj.getClass());
            if (config != null) {
//                save(obj, config.getFile());
            }
        } else throw new ConfigNotRegisteredException();
    }

    protected <Type> Type tryLoad(Class<Type> clazz, File file, Predicate<File> predicate, BiFunction<File, Class<Type>, Type> function) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (predicate.test(file)) {
            return function.apply(file, clazz);
        } else {
            return null;
        }
    }


    @Override
    public <Type> Optional<Type> load(Class<Type> clazz) {
        if (this.loadedConfigs.containsKey(clazz)) {

            final Config configuration = (Config) this.loadedConfigs.get(clazz);
            if (configuration != null) {
                return null;
//                return load(clazz, configuration.getFile());
            }
        }
        throw new ConfigNotRegisteredException();
    }

    @Override
    public Collection<Object> getConfigs() {
        return this.loadedConfigs.values();
    }


}
