package gg.supervisor.configuration;

import gg.supervisor.core.annotation.Configuration;
import gg.supervisor.core.config.Config;
import gg.supervisor.core.config.ConfigService;
import gg.supervisor.core.util.Services;
import org.bukkit.plugin.Plugin;

import java.io.File;

public abstract class AbstractConfig implements Config {

    private final transient File file;

    private final transient ConfigService configService;

    public AbstractConfig() {
        final Plugin plugin = Services.loadIfPresent(Plugin.class);
        final Configuration configuration = getClass().getAnnotation(Configuration.class);

        this.file = new File(plugin.getDataFolder(), configuration.fileName());
        this.configService = Services.loadIfPresent(configuration.service());
    }

    @Override
    public void save() {
        this.configService.save(this, file);
    }

    @Override
    public void reload() {
       this.configService.reload(getClass(), this, file);
    }

    @Override
    public File getFile() {
        return this.file;
    }
}
