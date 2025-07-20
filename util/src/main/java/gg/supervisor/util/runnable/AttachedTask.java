package gg.supervisor.util.runnable;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public class AttachedTask<T> implements Closeable {

    private static Map<UUID, AttachedTask<?>> tasks = new HashMap<>();

    private final @Getter UUID uuid = UUID.randomUUID();
    private final @Getter T entity;
    private final @Getter Map<String, Object> data = new HashMap<>();
    private final @Getter AtomicInteger counter;

    private final Consumer<T> onTerminate;

    private @Getter boolean invalidated = false;

    private BukkitTask task;

    public AttachedTask(T entity, int duration, int ticks, Function<AttachedTask<T>, Boolean> condition, Consumer<AttachedTask<T>> trigger, Consumer<T> onTerminate) {
        this.entity = entity;
        this.onTerminate = onTerminate;

        this.counter = new AtomicInteger(duration);

        tasks.put(this.uuid, this);

        this.task = Bukkit.getScheduler().runTaskTimer(JavaPlugin.getProvidingPlugin(this.getClass()), () -> {

            if (condition.apply(this))
                trigger.accept(this);

            if (counter.decrementAndGet() == 0 || invalidated)
                close();

        }, ticks, ticks);

    }

    public static void terminateAll() {
        for (AttachedTask<?> task : tasks.values()) {
            task.close();
        }

        tasks.clear();
    }

    public static void closeAll() {
        tasks.values().forEach(AttachedTask::close);
    }

    @Override
    public void close() {
        this.task.cancel();

        this.invalidated = true;

        if (this.onTerminate != null)
            this.onTerminate.accept(this.entity);

        this.data.clear();

        tasks.remove(this.uuid);
    }

}
