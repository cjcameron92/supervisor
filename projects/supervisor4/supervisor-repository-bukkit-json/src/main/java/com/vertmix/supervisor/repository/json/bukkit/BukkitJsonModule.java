package com.vertmix.supervisor.repository.json.bukkit;

import com.vertmix.supervisor.core.CoreProvider;
import com.vertmix.supervisor.core.annotation.Navigation;
import com.vertmix.supervisor.core.module.Module;
import com.vertmix.supervisor.core.service.Services;
import com.vertmix.supervisor.reflection.AbstractProxyHandler;
import com.vertmix.supervisor.repository.bukkit.PlayerRepositoryListener;
import com.vertmix.supervisor.repository.json.JsonPlayerProxyHandler;
import com.vertmix.supervisor.repository.json.JsonPlayerRepository;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Proxy;

public class BukkitJsonModule implements Module<Plugin> {

    @Override
    public void onEnable(CoreProvider<Plugin> provider) {
        Services.registerConsumer(o -> {
            if (o.getClass().isAssignableFrom(JsonPlayerRepository.class)) {
                Bukkit.getPluginManager().registerEvents(new PlayerRepositoryListener<>(provider, (BukkitJsonPlayerRepository<Object>) o), provider.getSource());
            }
        });

        Services.register(BukkitJsonPlayerRepository.class, clazz -> {
            File file = provider.getPath().toFile();
            Navigation navigation = clazz.getAnnotation(Navigation.class);
            if (navigation != null) {
                file = new File(navigation.path(), "storage");
            }
            return newPlayerRepository(clazz, new JsonPlayerProxyHandler<>(clazz, file));
        });

    }

    @Override
    public void onDisable() {

    }

    public static <T> BukkitJsonPlayerRepository<T> newPlayerRepository(Class<T> clazz, AbstractProxyHandler<T> handler) {
        return (BukkitJsonPlayerRepository<T>) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                handler
        );
    }

}
