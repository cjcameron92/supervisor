package com.cjcameron92.demo.storage;

import gg.supervisor.api.Component;
import gg.supervisor.storage.json.JsonStorageRegistry;
import org.bukkit.plugin.Plugin;

import java.io.File;

@Component
public class DemoStorage extends JsonStorageRegistry<Double> {

    public DemoStorage(Plugin plugin) {
        super(Double.class, new File(plugin.getDataFolder(), "storage"));
        put("demo", 50d);
        save();
    }
}
