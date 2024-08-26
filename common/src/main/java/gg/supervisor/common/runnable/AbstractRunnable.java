package gg.supervisor.common.runnable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class AbstractRunnable implements Runnable {

    private final Plugin plugin;
    private final boolean async;
    private BukkitTask bukkitTask;

    public AbstractRunnable(Plugin plugin, int duration, boolean async) {
        this.plugin = plugin;
        this.async = async;


        if (async)
            bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, duration, duration);
        else
            bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, this, duration, duration);

    }

    public void cancel() {
        bukkitTask.cancel();
    }

    public boolean isCancelled() {
        return bukkitTask.isCancelled();
    }

    public void reInitialize(int duration) {
        cancel();

        if (async)
            bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, duration, duration);
        else
            bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, this, duration, duration);
    }

}