package gg.supervisor.dummy.plugin;

import gg.supervisor.core.loader.SupervisorLoader;
import org.bukkit.Chunk;
import org.bukkit.plugin.java.JavaPlugin;

public class DummyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        SupervisorLoader.register(this);
    }


}
