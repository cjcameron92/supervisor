package com.cjcameron92.demo;

import gg.supervisor.loader.SupervisorLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class DemoPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        SupervisorLoader.register(this);
    }
}
