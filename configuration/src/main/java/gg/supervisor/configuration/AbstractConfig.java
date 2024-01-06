package gg.supervisor.configuration;

import gg.supervisor.api.Config;
import gg.supervisor.api.ConfigService;
import gg.supervisor.api.Configuration;
import gg.supervisor.api.Services;
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
