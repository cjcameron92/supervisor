package com.vertmix.supervisor.loader;

import com.vertmix.supervisor.core.CoreProvider;
import com.vertmix.supervisor.core.annotation.Component;
import com.vertmix.supervisor.core.annotation.Navigation;
import com.vertmix.supervisor.core.module.Module;
import com.vertmix.supervisor.core.service.Services;
import com.vertmix.supervisor.repository.json.JsonProxyHandler;
import com.vertmix.supervisor.repository.json.JsonRepository;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.vertmix.supervisor.reflection.ReflectionUtil.getComponentConstructor;
import static com.vertmix.supervisor.repository.json.JsonRepositoryModule.newRepository;

public class SupervisorLoader {


    public static void register(CoreProvider<?> provider) {
        try {
            String packageName = provider.getClass().getPackage().getName();
            ClassLoader classLoader = provider.getClass().getClassLoader();

            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .setUrls(ClasspathHelper.forPackage(packageName, classLoader))
                    .setScanners(new SubTypesScanner(false)));

            // Collect all classes in the package
            Set<Class<?>> classes = reflections.getSubTypesOf(Object.class).stream()
                    .filter(clazz -> clazz.getName().startsWith(packageName))
                    .collect(Collectors.toSet());

            Set<Module> modules = new HashSet<>();
            for (Class<?> clazz : classes) {
                if (clazz.isAssignableFrom(Module.class)) {
                    Module module = (Module) clazz.getDeclaredConstructor().newInstance();
                    module.onEnable(provider);
                    modules.add(module);
                }
            }

            Services.register(JsonRepository.class, clazz -> {
                System.out.println("CREATING + " + clazz.getSimpleName());
                Navigation navigation = clazz.getAnnotation(Navigation.class);
                File file = new File("help");
                if (navigation != null) {
                    file = new File(navigation.path());
                }

                return newRepository(clazz, new JsonProxyHandler<>(clazz, file));
            });

            // First pass: register all components
            Set<Class<?>> componentClasses = new HashSet<>();
            for (Class<?> clazz : classes) {
                if (clazz.isAnnotationPresent(Component.class)) {
                    componentClasses.add(clazz);
                }
            }

            for (Class<?> componentClass : componentClasses) {
                try {
                    registerComponent(componentClass);
                } catch (Exception e) {
                    System.err.println("[ERROR] Error registering component: " + componentClass.getName());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Unexpected error during registration process.");
            e.printStackTrace();
        }
    }

    private static void registerComponent(Class<?> clazz) throws Exception {
        if (Services.getService(clazz) != null) {
            // Component is already registered
            return;
        }

        if (clazz.isInterface()) {
            // Handle interface by using factory from Services
            System.out.println("[DEBUG] Found interface component: " + clazz.getName());
            System.out.println(clazz.getInterfaces()[0]);
            if (Services.getFactories().containsKey(clazz.getInterfaces()[0])) {
                Services.register(clazz, Services.create(clazz));
                System.out.println("[DEBUG] Registered interface component using factory: " + clazz.getName());
            } else {
                System.err.println("[WARNING] No factory found for interface: " + clazz.getName());
            }
            return;
        }

        Constructor<?> constructor = getComponentConstructor(clazz);
        if (constructor == null) {
            System.err.println("[WARNING] No valid constructor found for component: " + clazz.getName());
            return;
        }

        // Resolve constructor parameters
        Object[] params = resolveDependencies(constructor.getParameterTypes());

        // Create and register the component
        Object componentInstance = constructor.newInstance(params);
        // Preform mutations
        Services.runConsumer(componentInstance);
        // Register
        Services.register(clazz, componentInstance);
        System.out.println("[DEBUG] Registered component: " + clazz.getName());
    }

    private static Object[] resolveDependencies(Class<?>[] paramTypes) throws Exception {
        Object[] params = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];
            Object serviceInstance = Services.getService(paramType);

            if (serviceInstance != null) {
                params[i] = serviceInstance;
            } else if (paramType.isAnnotationPresent(Component.class)) {
                // Register dependency if not already registered
                registerComponent(paramType);
                params[i] = Services.getService(paramType);
            } else {
                throw new IllegalStateException("Unable to resolve parameter: " + paramType.getName());
            }
        }
        return params;
    }
}
