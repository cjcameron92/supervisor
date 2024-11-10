package gg.supervisor.core.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import gg.supervisor.core.adapters.types.entity.PlayerTypeAdapter;
import gg.supervisor.core.adapters.types.item.ItemStackBase64Adapter;
import gg.supervisor.core.adapters.types.world.BlockTypeAdapter;
import gg.supervisor.core.adapters.types.world.ChunkTypeAdapter;
import gg.supervisor.core.adapters.types.world.LocationTypeAdapter;
import gg.supervisor.core.adapters.types.world.WorldTypeAdapter;
import gg.supervisor.core.annotation.*;
import gg.supervisor.core.config.ConfigService;
import gg.supervisor.core.repository.JsonPlayerRepository;
import gg.supervisor.core.repository.PlayerRepository;
import gg.supervisor.core.repository.Repository;
import gg.supervisor.core.repository.json.SimpleProxyHandler;
import gg.supervisor.core.repository.player.PlayerRepositoryListener;
import gg.supervisor.core.repository.store.JsonPlayerStore;
import gg.supervisor.core.repository.store.JsonStore;
import gg.supervisor.core.repository.store.Store;
import gg.supervisor.core.util.Services;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SupervisorLoader {

    public static Gson GSON;
    private static final List<Runnable> DISABLE = new ArrayList<>();

    public static void register(Object plugin, Object... registeredObjects) {
        if (plugin instanceof Plugin p) {
            p.getDataFolder().mkdirs();
        }

        Services.register(plugin.getClass(), plugin);
        Services.register(Plugin.class, plugin);

        for (Object o : registeredObjects) {
            Services.register(o.getClass(), o);
        }

        String pluginPackageName = plugin.getClass().getPackage().getName();
        ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(pluginPackageName, pluginClassLoader))
                .setScanners(new SubTypesScanner(false)));

        GSON = createGsonWithAdapters(reflections, plugin);
        loadComponents(reflections, plugin);
    }

    public static void disable(Plugin plugin) {
        DISABLE.forEach(Runnable::run);
    }

    private static Gson createGsonWithAdapters(Reflections reflections, Object plugin) {
        GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting();

        gsonBuilder.registerTypeAdapter(ItemStack.class, new ItemStackBase64Adapter());
        gsonBuilder.registerTypeAdapter(Block.class, new BlockTypeAdapter());
        gsonBuilder.registerTypeAdapter(World.class, new WorldTypeAdapter());
        gsonBuilder.registerTypeAdapter(Location.class, new LocationTypeAdapter());
        gsonBuilder.registerTypeAdapter(Chunk.class, new ChunkTypeAdapter());
        gsonBuilder.registerTypeAdapter(Player.class, new PlayerTypeAdapter());

        List<Class<?>> allClasses = reflections.getAll(new SubTypesScanner(false)).stream()
                .filter(x -> x.startsWith(plugin.getClass().getPackage().getName()))
                .map(x -> {
                    try {
                        Class<?> clazz = Class.forName(x);
                        return clazz.isAnnotationPresent(Adapter.class) ? clazz : null;
                    } catch (ClassNotFoundException e) {
                        System.out.println("Couldn't locate class with the name " + x);
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());

        for (Class<?> adapterClass : allClasses) {
            if (TypeAdapter.class.isAssignableFrom(adapterClass)) {
                try {
                    Constructor<?> constructor = adapterClass.getConstructor();
                    TypeAdapter<?> adapterInstance = (TypeAdapter<?>) constructor.newInstance();
                    gsonBuilder.registerTypeAdapter(getGenericType(adapterClass), adapterInstance);
                    Bukkit.getLogger().info("Registered adapter " + adapterClass.getSimpleName().replaceAll("Class", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Failed to register adapter: " + adapterClass.getName());
                }
            }
        }

        return gsonBuilder.create();
    }

    private static Class<?> getGenericType(Class<?> adapterClass) {
        return ((Class<?>) ((ParameterizedType) adapterClass.getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    private static void loadComponents(Reflections reflections, Object plugin) {
        List<Class<?>> allClasses = reflections.getAll(new SubTypesScanner(false)).stream()
                .filter(x -> x.startsWith(plugin.getClass().getPackage().getName()))
                .map(x -> {
                    try {
                        Class<?> clazz = Class.forName(x);
                        return clazz.isAnnotationPresent(Component.class) ? clazz : null;
                    } catch (ClassNotFoundException e) {
                        System.out.println("Couldn't locate class with the name " + x);
                        return null;
                    }
                }).filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(clazz -> clazz.getAnnotation(Component.class).priority().getPriority()))
                .collect(Collectors.toList());

        for (Class<?> clazz : allClasses) {
            try {
                if (clazz.isAnnotationPresent(Configuration.class) || clazz.isAnnotationPresent(Component.class)) {
                    if (Repository.class.isAssignableFrom(clazz)) {
                        createRepositoryInstance(clazz, (Plugin) plugin);
                    } else {
                        Object instance = createComponentInstance(clazz, (Plugin) plugin);
                        if (instance instanceof Listener) {
                            Bukkit.getPluginManager().registerEvents((Listener) instance, (Plugin) plugin);
                        }
                        Services.register(clazz, instance);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Object createComponentInstance(Class<?> clazz, Plugin plugin) throws Exception {
        Constructor<?> constructor = getComponentConstructor(clazz);

        if (constructor == null) {
            if (Repository.class.isAssignableFrom(clazz)) {
                return createRepositoryInstance(clazz, plugin);
            }
            return null;
        }

        Class<?>[] paramTypes = constructor.getParameterTypes();
        Object[] params = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];

            Object serviceInstance = Services.getService(paramType);
            if (serviceInstance != null) {
                params[i] = serviceInstance;
            } else if (paramType.isAssignableFrom(Plugin.class)) {
                params[i] = plugin;
            } else if (paramType.isAnnotationPresent(Component.class)) {
                Object paramInstance = createComponentInstance(paramType, plugin);
                Services.register(paramType, paramInstance);
                params[i] = paramInstance;
            } else if (paramType.isAnnotationPresent(Configuration.class)) {
                params[i] = registerConfig(paramType, plugin);
            } else {
                throw new Exception("No component found for required type: " + paramType.getName() + " in " + clazz.getName());
            }
        }

        return constructor.newInstance(params);
    }

    public static Object registerConfig(Class<?> clazz, Plugin plugin) throws Exception {
        if (!clazz.isAnnotationPresent(Configuration.class)) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " must be annotated with @Configuration");
        }

        Configuration configuration = clazz.getAnnotation(Configuration.class);
        File configDirectory = new File(plugin.getDataFolder(), configuration.path());

        if (!configDirectory.exists() && !configDirectory.mkdirs()) {
            throw new IllegalStateException("Failed to create configuration directory at: " + configDirectory.getPath());
        }

        File configFile = new File(configDirectory, configuration.fileName());
        ConfigService configService = (ConfigService) createComponentInstance(configuration.service(), plugin);

        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object configInstance = constructor.newInstance();

        configService.register((Class<Object>) clazz, configInstance, configFile);
        Services.register(clazz, configInstance);

        Bukkit.getLogger().info("Registered configuration for file " + configFile.getPath());
        return configInstance;
    }

    private static Constructor<?> getComponentConstructor(Class<?> clazz) {
        if (clazz.isInterface()) {
            return null;
        }

        System.out.println(clazz);

        Constructor<?>[] constructors = clazz.getConstructors();
        if (constructors.length == 0) {
            throw new IllegalStateException("No public constructors found for class: " + clazz.getName());
        }

        for (Constructor<?> clazzConstructor : constructors) {
            if (clazzConstructor.isAnnotationPresent(ComponentConstructor.class)) {
                return clazzConstructor;
            }
        }

        return constructors[0];
    }

    private static <T> Repository<T> createRepositoryInstance(Class<T> clazz, Plugin plugin) throws Exception {
        File storageFolder = new File(plugin.getDataFolder(), "storage");
        if (!storageFolder.exists() && !storageFolder.mkdirs()) {
            throw new IllegalStateException("Failed to create storage folder at: " + storageFolder.getPath());
        }

        ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericInterfaces()[0];
        Class<T> entityType = (Class<T>) parameterizedType.getActualTypeArguments()[0];

        Store<T> store;
        if (JsonPlayerRepository.class.isAssignableFrom(clazz)) {
            File playerDataDirectory = new File(storageFolder, clazz.getSimpleName());
            store = new JsonPlayerStore<>(playerDataDirectory, entityType);
        } else if (isMongoRepository(clazz)) {
            Class<?> mongoConfigClass = Class.forName("gg.supervisor.repository.mongo.MongoConfig");
            Object mongoConfig = Services.loadIfPresent(mongoConfigClass);

            if (mongoConfig == null) {
                mongoConfig = registerConfig(mongoConfigClass, plugin);
            }

            if (isMongoPlayerRepository(clazz)) {
                store = createMongoStoreInstance("gg.supervisor.repository.mongo.MongoPlayerStore", mongoConfig, clazz);
            } else {
                store = createMongoStoreInstance("gg.supervisor.repository.mongo.MongoStore", mongoConfig, clazz);
            }

            DISABLE.add(() -> {
                try {
                    Method closeMethod = store.getClass().getMethod("close");
                    closeMethod.invoke(store);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } else if (isItemStackRepository(clazz)) {
            store = createItemStackStoreInstance("gg.supervisor.repository.itemstack.ItemStackStore", entityType, plugin);
        } else {
            File storeFile = new File(storageFolder, clazz.getSimpleName() + ".json");
            store = new JsonStore<>(storeFile, entityType);
        }

        SimpleProxyHandler<T> proxyHandler = new SimpleProxyHandler<>(clazz, store);
        Repository<T> repository = (Repository<T>) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                proxyHandler
        );

        Services.register(clazz, repository);

        if (repository instanceof PlayerRepository) {
            Bukkit.getPluginManager().registerEvents(new PlayerRepositoryListener<>((PlayerRepository<T>) repository), plugin);
            Bukkit.getLogger().info("Bounded " + repository.getClass().getSimpleName() + " with player profiles.");
        }

        return repository;
    }

    private static boolean isMongoRepository(Class<?> clazz) {
        try {
            Class<?> mongoRepositoryClass = Class.forName("gg.supervisor.repository.MongoRepository");
            return mongoRepositoryClass.isAssignableFrom(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean isMongoPlayerRepository(Class<?> clazz) {
        try {
            Class<?> mongoPlayerRepositoryClass = Class.forName("gg.supervisor.repository.MongoPlayerRepository");
            return mongoPlayerRepositoryClass.isAssignableFrom(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean isItemStackRepository(Class<?> clazz) {
        try {
            Class<?> itemStackRepositoryClass = Class.forName("gg.supervisor.repository.itemstack.ItemStackRepository");
            return itemStackRepositoryClass.isAssignableFrom(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static <T> Store<T> createMongoStoreInstance(String className, Object mongoConfig, Class<T> clazz) throws Exception {
        Class<?> mongoStoreClass = Class.forName(className);
        Class<?> mongoConfigClass = mongoConfig.getClass();

        Constructor<?> constructor = null;
        for (Constructor<?> c : mongoStoreClass.getConstructors()) {
            Class<?>[] paramTypes = c.getParameterTypes();
            if (paramTypes.length == 2 && paramTypes[0].isAssignableFrom(mongoConfigClass) && paramTypes[1] == Class.class) {
                constructor = c;
                break;
            }
        }
        if (constructor == null) {
            throw new NoSuchMethodException("No suitable constructor found for " + className);
        }

        return (Store<T>) constructor.newInstance(mongoConfig, clazz);
    }

    private static <T> Store<T> createItemStackStoreInstance(String className, Class<T> entityType, Plugin plugin) throws Exception {
        Class<?> itemStackStoreClass = Class.forName(className);

        Constructor<?> constructor = itemStackStoreClass.getConstructor(Class.class, Plugin.class);
        return (Store<T>) constructor.newInstance(entityType, plugin);
    }
}
