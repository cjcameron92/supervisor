package com.vertmix.supervisor.core.bukkit;

import com.vertmix.supervisor.core.CoreProvider;
import com.vertmix.supervisor.core.module.Module;
import com.vertmix.supervisor.core.service.Services;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class BukkitModule implements Module<Plugin> {

    @Override
    public void onEnable(CoreProvider<Plugin> provider) {
        Services.registerConsumer(o -> {
            if (o.getClass().isAssignableFrom(Listener.class)) {
                Bukkit.getPluginManager().registerEvents((Listener) o, provider.getSource());
            }
        });

    }

    @Override
    public void onDisable() {

    }
}
