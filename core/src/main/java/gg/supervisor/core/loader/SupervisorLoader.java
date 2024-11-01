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
import gg.supervisor.core.repository.Repository;
import gg.supervisor.core.repository.json.JsonProxyHandler;
import gg.supervisor.core.repository.player.PlayerRepositoryListener;
import gg.supervisor.core.store.JsonPlayerStore;
import gg.supervisor.core.store.JsonStore;
import gg.supervisor.core.store.Store;
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
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * SupervisorLoader is responsible for loading and registering all components and repositories for a plugin.
 * It uses reflection to discover annotated components, register Gson type adapters,
 * and create repository instances using dynamic proxies.
 */
public class SupervisorLoader {

    public static Gson GSON;

    /**
     * Registers components, repositories, and configurations for a given plugin.
     *
     * @param plugin The plugin instance.
     * @param registeredObjects Additional objects to be registered as services.
     */
    public static void register(Object plugin, Object... registeredObjects) {
        if (plugin instanceof Plugin p) {
            p.getDataFolder().mkdirs();
        }

        for (Object o : registeredObjects) {
            Services.register(o.getClass(), o);
        }

        Services.register(plugin.getClass(), plugin);

        String pluginPackageName = plugin.getClass().getPackage().getName();
        ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(pluginPackageName, pluginClassLoader))
                .setScanners(new SubTypesScanner(false)));

        GSON = createGsonWithAdapters(reflections, plugin);
        loadComponents(reflections, plugin);
    }

    /**
     * Creates a Gson instance with custom type adapters.
     *
     * @param reflections Reflections instance for scanning classes.
     * @param plugin Plugin instance to identify the package scope.
     * @return A customized Gson instance.
     */
    private static Gson createGsonWithAdapters(Reflections reflections, Object plugin) {
        GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting();

        // Register known type adapters
        gsonBuilder.registerTypeAdapter(ItemStack.class, new ItemStackBase64Adapter());
        gsonBuilder.registerTypeAdapter(Block.class, new BlockTypeAdapter());
        gsonBuilder.registerTypeAdapter(World.class, new WorldTypeAdapter());
        gsonBuilder.registerTypeAdapter(Location.class, new LocationTypeAdapter());
        gsonBuilder.registerTypeAdapter(Chunk.class, new ChunkTypeAdapter());
        gsonBuilder.registerTypeAdapter(Player.class, new PlayerTypeAdapter());

        // Register adapters dynamically based on annotations
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
                    Bukkit.getLogger().info("Registered adatper " + adapterClass.getSimpleName().replaceAll("Class", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Failed to register adapter: " + adapterClass.getName());
                }
            }
        }

        return gsonBuilder.create();
    }

    /**
     * Gets the generic type from a TypeAdapter class.
     *
     * @param adapterClass The TypeAdapter class.
     * @return The generic type of the adapter.
     */
    private static Class<?> getGenericType(Class<?> adapterClass) {
        return ((Class<?>) ((ParameterizedType) adapterClass.getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    /**
     * Loads components annotated with @Component or @Configuration.
     *
     * @param reflections Reflections instance for scanning classes.
     * @param plugin Plugin instance for registering listeners and services.
     */
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

    /**
     * Creates an instance of a component using reflection.
     *
     * @param clazz Component class to instantiate.
     * @param plugin Plugin instance to be used in the constructor.
     * @return The created component instance.
     * @throws Exception If there is an error during instantiation.
     */
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

        Object mainInstance = Services.getService(clazz);
        if (mainInstance != null) {
            return mainInstance;
        }

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];
            Object serviceInstance = Services.getService(paramType);

            if (serviceInstance != null) {
                params[i] = serviceInstance;
            } else if (paramType.isAnnotationPresent(Component.class)) {
                Object paramInstance = createComponentInstance(paramType, plugin);
                Services.register(paramType, paramInstance);
                params[i] = paramInstance;
            } else if (paramType.isAnnotationPresent(Configuration.class)) {
                Object paramInstance = createComponentInstance(paramType, plugin);
                Configuration configuration = paramInstance.getClass().getAnnotation(Configuration.class);

                ConfigService configService = (ConfigService) createComponentInstance(configuration.service(), plugin);
                File configDirectory = new File(plugin.getDataFolder(), configuration.path());
                if (!configDirectory.exists() && !configDirectory.mkdirs()) {
                    throw new IllegalStateException("Failed to create configuration directory at: " + configDirectory.getPath());
                }

                File configFile = new File(configDirectory, configuration.fileName());
                configService.register((Class<Object>) paramInstance.getClass(), paramInstance, configFile);
                Bukkit.getLogger().info("Registered configuration for file " + configFile.getPath());
                Services.register(paramType, paramInstance);
                params[i] = paramInstance;
            } else {
                throw new Exception("No component found for required type: " + paramType.getName() + " in " + clazz.getName());
            }
        }

        return constructor.newInstance(params);
    }

    /**
     * Gets the constructor of a component class.
     *
     * @param clazz Component class to inspect.
     * @return The constructor to use for instantiation.
     */
    private static Constructor<?> getComponentConstructor(Class<?> clazz) {
        if (clazz.isInterface()) {
            return null;
        }

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

    /**
     * Creates an instance of a repository and registers it.
     *
     * @param clazz Repository class to create.
     * @param plugin Plugin instance for registration.
     * @return The created repository instance.
     */
    private static <T> Repository<T> createRepositoryInstance(Class<T> clazz, Plugin plugin) {
        File storageFolder = new File(plugin.getDataFolder(), "storage");
        if (!storageFolder.exists() && !storageFolder.mkdirs()) {
            throw new IllegalStateException("Failed to create storage folder at: " + storageFolder.getPath());
        }

        ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericInterfaces()[0];
        Class<T> entityType = (Class<T>) parameterizedType.getActualTypeArguments()[0];

        Store<T> store;
        if (JsonPlayerRepository.class.isAssignableFrom(clazz)) {
            File playerDataDirectory = new File(storageFolder, clazz.getSimpleName() + "_playerData");
            store = new JsonPlayerStore<>(playerDataDirectory, entityType);
        } else {
            File storeFile = new File(storageFolder, clazz.getSimpleName() + ".json");
            store = new JsonStore<>(storeFile, entityType);
        }

        JsonProxyHandler<T> proxyHandler = new JsonProxyHandler<>(clazz, store);
        Repository<T> repository = (Repository<T>) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                proxyHandler
        );

        Services.register(clazz, repository);

        if (repository instanceof JsonPlayerRepository<T> jsonPlayerRepository) {
            Bukkit.getPluginManager().registerEvents(new PlayerRepositoryListener<>(jsonPlayerRepository), plugin);
            Bukkit.getLogger().info("Bounded " + repository.getClass().getSimpleName() + " with player profiles.");
        }

        return repository;
    }
}
