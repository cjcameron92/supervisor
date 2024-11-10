package com.vertmix.supervisor.core.velocity;

import com.vertmix.supervisor.core.CoreProvider;
import com.velocitypowered.api.plugin.PluginContainer;

import java.nio.file.Path;

public class VelocityProvider extends CoreProvider<PluginContainer> {

    public VelocityProvider(Path dataFolder, PluginContainer source) {
        super(dataFolder, source);
    }

    public static CoreProvider<PluginContainer> velocity(PluginContainer plugin, Path dataFolder) {
        return new VelocityProvider(dataFolder, plugin);
    }
}