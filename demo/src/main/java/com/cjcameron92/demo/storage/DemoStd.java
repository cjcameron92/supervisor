package com.cjcameron92.demo.storage;

import gg.supervisor.storage.json.JsonStorage;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class DemoStd extends JsonStorage<String> {

    public DemoStd(Plugin plugin) {
        super(String.class, new File(plugin.getDataFolder(), "demo.json"));
    }
}
