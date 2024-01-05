package gg.llamas.supervisor.loader;

import gg.llama.supervisor.api.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Set;

public class SupervisorLoader {


    public static void register(Object plugin, Object... registeredObjects) {

        if (plugin instanceof Plugin p) {
            p.getDataFolder().mkdirs();
        }

        for (Object o : registeredObjects)
            Services.register(o.getClass(), o);

        Services.register(plugin.getClass(), plugin);
        Services.register(Plugin.class, plugin);

        final String pluginPackageName = plugin.getClass().getPackage().getName();

        final Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(pluginPackageName))
                .setScanners(new SubTypesScanner(false)));

        final Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
        allClasses.removeIf(clazz -> !clazz.getPackage().getName().startsWith(pluginPackageName));

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
        final Constructor<?>[] constructors = clazz.getConstructors();
        final Constructor<?> constructor = constructors[0];
        final Class<?>[] paramTypes = constructor.getParameterTypes();
        final Object[] params = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            final Class<?> paramType = paramTypes[i];
            Object serviceInstance = Services.loadIfPresent(paramType);

            if (serviceInstance != null) {
                params[i] = serviceInstance;
            } else if (paramType.isAnnotationPresent(Component.class)) {
                Object paramInstance = createComponentInstance(paramType, plugin);
                Services.register(paramType, paramInstance); // Register immediately
                params[i] = paramInstance;
            } else if (paramType.isAnnotationPresent(Configuration.class)) {
                Object paramInstance = createComponentInstance(paramType, plugin);
                final Configuration configuration = paramInstance.getClass().getAnnotation(Configuration.class);
                final Class<? extends ConfigService> cfz = configuration.service();
                final Constructor<? extends ConfigService> crz = cfz.getConstructor(Plugin.class);
                final ConfigService configService = crz.newInstance(plugin);
                configService.register(paramInstance.getClass(), paramInstance, new File(plugin.getDataFolder(), configuration.fileName()));
                Bukkit.getLogger().info("Registered configuration for file " + configuration.fileName());
                Services.register(paramType, paramInstance); // Register immediately
                params[i] = paramInstance;
            } else {
                throw new Exception("No component found for required type: " + paramType.getName());
            }
        }

        return constructor.newInstance(params);
    }
}
