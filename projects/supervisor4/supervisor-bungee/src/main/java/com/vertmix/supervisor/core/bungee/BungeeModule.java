package com.vertmix.supervisor.core.bungee;

import com.vertmix.supervisor.core.CoreProvider;
import com.vertmix.supervisor.core.module.Module;
import com.vertmix.supervisor.core.service.Services;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeModule implements Module<Plugin> {

    @Override
    public void onEnable(CoreProvider<Plugin> provider) {
        Services.registerConsumer(o -> {
            if (o instanceof Listener) {
                ProxyServer.getInstance().getPluginManager().registerListener(provider.getSource(), (Listener) o);
            }
        });
    }

    @Override
    public void onDisable() {
    }
}
