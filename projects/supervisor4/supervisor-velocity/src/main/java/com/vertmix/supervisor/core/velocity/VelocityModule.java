package com.vertmix.supervisor.core.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.ProxyServer;
import com.vertmix.supervisor.core.CoreProvider;
import com.vertmix.supervisor.core.module.Module;
import com.vertmix.supervisor.core.service.Services;
import org.slf4j.Logger;

public class VelocityModule implements Module<PluginContainer> {

    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public VelocityModule(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Override
    public void onEnable(CoreProvider<PluginContainer> provider) {
        Services.registerConsumer(o -> {
            if (o.getClass().isAnnotationPresent(com.velocitypowered.api.event.Subscribe.class)) {
                EventManager eventManager = server.getEventManager();
                eventManager.register(provider.getSource(), o);
                logger.info("Registered event listener: {}", o.getClass().getSimpleName());
            }
        });

        logger.info("VelocityModule has been enabled.");
    }

    @Override
    public void onDisable() {
        logger.info("VelocityModule has been disabled.");
    }
}
