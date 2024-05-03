package gg.supervisor.storage.json;

import gg.supervisor.storage.PlayerStorage;
import gg.supervisor.storage.Storage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

@Deprecated
public class JsonPlayerStorage<T> implements PlayerStorage<T>, Listener {

    private final Map<UUID, Storage<T>> playerStorages = new HashMap<>();
    private final Class<T> clazz;
    private final File folder;

    private final Function<Player, T> function;

    public JsonPlayerStorage(Class<T> clazz, File folder, Function<Player, T> function) {
        this.clazz = clazz;
        this.folder = folder;
        this.function = function;
    }

    @Override
    public T getPlayer(UUID uuid) {
        final Storage<T> storage = this.playerStorages.get(uuid);
        return storage != null ? storage.get() : null;
    }

    @Override
    public void savePlayer(UUID uuid) {
        final Storage<T> storage = this.playerStorages.get(uuid);
        if (storage != null) {
            storage.save();
        }
    }

    @Override
    public void update(UUID uuid, T type) {
        Storage<T> storage = this.playerStorages.get(uuid);
        if (storage != null) {
            storage.update(type);
            storage.save();
        }
    }

    @Override
    public void update(UUID uuid, Consumer<T> consumer) {
        Storage<T> storage = this.playerStorages.get(uuid);
        if (storage != null) {
            T type = storage.get();
            consumer.accept(type);
            storage.update(type);
            storage.save();
        }
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        CompletableFuture.runAsync(() -> {
            Storage<T> storage = playerStorages.get(uuid);
            if (storage == null) {
                storage = new JsonStorage<>(clazz, new File(folder, uuid + ".json"));
                T loadedInstance = storage.load();

                if (loadedInstance == null) {

                    T newInstance = function.apply(player);

                    storage.update(newInstance);
                    storage.save();

                    playerStorages.put(uuid, storage);

                } else {
                    playerStorages.put(uuid, storage);
                }
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        CompletableFuture.runAsync(() -> {
            savePlayer(uuid);
            playerStorages.remove(uuid);
        });
    }
}