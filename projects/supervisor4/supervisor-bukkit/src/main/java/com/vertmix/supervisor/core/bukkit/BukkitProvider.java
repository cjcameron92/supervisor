package com.vertmix.supervisor.core.bukkit;

import com.vertmix.supervisor.core.CoreProvider;
import org.bukkit.plugin.Plugin;

public class BukkitProvider extends CoreProvider<Plugin> {

    public BukkitProvider(Plugin source) {
        super(source.getDataFolder().toPath(), source);
    }

    public static CoreProvider<Plugin> bukkit(Plugin plugin) {
        return new BukkitProvider(plugin);
    }
}
