package gg.supervisor.storage;

import java.util.UUID;
import java.util.function.Consumer;

@Deprecated
public interface PlayerStorage<V>  {

    V getPlayer(UUID uuid);

    void savePlayer(UUID uuid);

    void update(UUID uuid, V type);

    void update(UUID uuid, Consumer<V> consumer);

}
