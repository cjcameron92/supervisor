package gg.supervisor.core.loader;

import gg.supervisor.core.annotation.Component;
import gg.supervisor.core.annotation.ComponentConstructor;
import gg.supervisor.core.annotation.Configuration;
import gg.supervisor.core.config.ConfigService;
import gg.supervisor.core.util.Services;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SupervisorLoader {


    public static void register(Object plugin, Object... registeredObjects) {
        if (plugin instanceof Plugin p) {
            p.getDataFolder().mkdirs();
        }

        for (Object o : registeredObjects)
            Services.register(o.getClass(), o);

        Services.register(plugin.getClass(), plugin);

        final String pluginPackageName = plugin.getClass().getPackage().getName();
        final ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();

        final Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(pluginPackageName, pluginClassLoader))
                .setScanners(new SubTypesScanner(false)));


        final List<Class<?>> allClasses = reflections.getAll(new SubTypesScanner(false)).stream().filter(x -> x.startsWith(pluginPackageName)).map(x -> {
            try {
                Class<?> clazz = Class.forName(x);

                if (!clazz.isAnnotationPresent(Component.class))
                    return null;

                return clazz;

            } catch (ClassNotFoundException e) {
                System.out.println("Couldn't locate class with the name " + x);
                return null;
            }
        }).filter(Objects::nonNull).sorted(Comparator.comparingInt(clazz -> clazz.getAnnotation(Component.class).priority().getPriority())).collect(Collectors.toList());

        for (Class<?> clazz : allClasses) {
            try {
                if ((clazz.isAnnotationPresent(Configuration.class) || clazz.isAnnotationPresent(Component.class))) {

                    final Object instance = createComponentInstance(clazz, (Plugin) plugin);
                    if (instance instanceof Listener) {
                        Bukkit.getPluginManager().registerEvents((Listener) instance, (Plugin) plugin);
                    }
                    Services.register(clazz, instance);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static Object createComponentInstance(Class<?> clazz, Plugin plugin) throws Exception {
//        System.out.println("Attempting to create Instance of " + clazz.getSimpleName());

        final Constructor<?> constructor = getComponentConstructor(clazz);

        final Class<?>[] paramTypes = constructor.getParameterTypes();
        final Object[] params = new Object[paramTypes.length];

        Object mainInstance = Services.getService(clazz);

        if (mainInstance != null)
            return mainInstance;

        for (int i = 0; i < paramTypes.length; i++) {
            final Class<?> paramType = paramTypes[i];
            Object serviceInstance = Services.getService(paramType);

            if (serviceInstance != null) {
                params[i] = serviceInstance;
            } else if (paramType.isAnnotationPresent(Component.class)) {
                Object paramInstance = createComponentInstance(paramType, plugin);
                Services.register(paramType, paramInstance);
                params[i] = paramInstance;

            } else if (paramType.isAnnotationPresent(Configuration.class)) {
                Object paramInstance = createComponentInstance(paramType, plugin);
                final Configuration configuration = paramInstance.getClass().getAnnotation(Configuration.class);

                final ConfigService configService = (ConfigService) createComponentInstance(configuration.service(), plugin);

                @SuppressWarnings("unchecked")
                Class<Object> paramClass = (Class<Object>) paramInstance.getClass();
                configService.register(paramClass, paramInstance, new File(plugin.getDataFolder(), "/" + configuration.path() + "/" + configuration.fileName()));

                Bukkit.getLogger().info("Registered configuration for file " + configuration.fileName());
                Services.register(paramType, paramInstance);
                params[i] = paramInstance;
            } else {
                throw new Exception("No component found for required type: " + paramType.getName() + " in " + clazz.getName());
            }
        }

        return constructor.newInstance(params);
    }

    private static Constructor<?> getComponentConstructor(Class<?> clazz) {
        for (Constructor<?> clazzConstructor : clazz.getConstructors()) {
            if (clazzConstructor.isAnnotationPresent(ComponentConstructor.class))
                return clazzConstructor;
        }

        return clazz.getConstructors()[0];
    }

}
