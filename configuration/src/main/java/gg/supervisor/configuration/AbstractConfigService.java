package gg.supervisor.configuration;

import gg.supervisor.configuration.exception.ConfigNotRegisteredException;
import gg.supervisor.core.config.Config;
import gg.supervisor.core.config.ConfigService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractConfigService implements ConfigService {

    private static final Logger LOGGER = Logger.getLogger(AbstractConfigService.class.getName());
    protected final Map<Class<?>, Object> loadedConfigs = new ConcurrentHashMap<>();

    public AbstractConfigService() {
    }

    protected void registerData(Class<?> clazz, Object type) {
        this.loadedConfigs.put(clazz, type);
    }

    @Override
    public <Type> Type register(Class<Type> clazz, Type instance, File file) {
        if (!file.exists()) {
            try {
                Files.createDirectories(file.getParentFile().toPath());
                Files.createFile(file.toPath());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error creating file: " + file.getPath(), e);
                return null;  // Return early if file creation fails
            }
        }

        registerData(clazz, instance);
        reload(clazz, instance, file);
        save(instance, file);

        return instance;
    }

    protected void trySave(Object obj, File file, Predicate<File> predicate, BiConsumer<Object, File> consumer) {
        try {
            if (file.exists()) {
                Files.delete(file.toPath());
            }
            Files.createFile(file.toPath());

            if (predicate.test(file)) {
                consumer.accept(obj, file);
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving file: " + file.getPath(), e);
        }
    }

    @Override
    public void save(Object obj) {
        if (this.loadedConfigs.containsKey(obj.getClass())) {
            final Config config = (Config) this.loadedConfigs.get(obj.getClass());
            if (config != null) {
                save(obj, config.getFile());
            }
        } else {
            throw new ConfigNotRegisteredException("Config not registered for class: " + obj.getClass().getName());
        }
    }

    protected <Type> Type tryLoad(Class<Type> clazz, File file, Predicate<File> predicate, BiFunction<File, Class<Type>, Type> function) {
        if (!file.exists()) {
            try {
                Files.createFile(file.toPath());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error creating file: " + file.getPath(), e);
                return null;
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
                return load(clazz, configuration.getFile());
            }
        }

        throw new ConfigNotRegisteredException("Config not registered for class: " + clazz.getName());
    }

    @Override
    public Collection<Object> getConfigs() {
        return this.loadedConfigs.values();
    }

}
