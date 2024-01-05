package gg.llama.supervisor.configuration;

import gg.llama.supervisor.api.Config;
import gg.llama.supervisor.api.ConfigService;
import gg.llama.supervisor.api.Configuration;
import gg.llama.supervisor.api.Services;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Constructor;

public abstract class AbstractConfig implements Config {

    private final transient Plugin plugin;

    public AbstractConfig() {
        this.plugin = Services.loadIfPresent(Plugin.class);
    }

    @Override
    public void save() throws Exception {
        final Configuration configuration = this.getClass().getAnnotation(Configuration.class);
        if (configuration == null) {
            throw new NullPointerException("Configuration annotation is null!");
        }

        final File file = new File(plugin.getDataFolder(), configuration.fileName());
        final Constructor<? extends ConfigService> constructor = configuration.service().getConstructor(Plugin.class);
        final ConfigService configService = constructor.newInstance(plugin);

        configService.save(this, file);
    }

    @Override
    public void reload() throws Exception {
        final Configuration configuration = this.getClass().getAnnotation(Configuration.class);
        if (configuration == null) {
            throw new NullPointerException("Configuration annotation is null!");
        }

        final File file = new File(plugin.getDataFolder(), configuration.fileName());
        final Constructor<? extends ConfigService> constructor = configuration.service().getConstructor(Plugin.class);
        final ConfigService configService = constructor.newInstance(plugin);

        configService.reload(this.getClass(), this, file);
    }

}
